-- Align company/employer tables with the production contract.
-- This migration is data-preserving and can run after V1__init_profile_schema.sql.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Fallback definitions for databases where V1 was partially applied.
CREATE TABLE IF NOT EXISTS companies (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                        VARCHAR(200) NOT NULL,
    slug                        VARCHAR(200),
    tax_code                    VARCHAR(20),
    industry                    VARCHAR(150),
    description                 TEXT,
    benefits                    TEXT,
    logo_url                    VARCHAR(500),
    cover_image_url             VARCHAR(500),
    website_url                 VARCHAR(300),
    address                     VARCHAR(255),
    company_size                VARCHAR(100),
    status                      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    founded_year                INT,
    verification_status         VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    business_license_file_id    UUID,
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS company_images (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id      UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    image_url       VARCHAR(500) NOT NULL,
    caption         VARCHAR(255),
    display_order   INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS employers (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL UNIQUE,
    company_id  UUID REFERENCES companies(id) ON DELETE RESTRICT,
    name        VARCHAR(150) NOT NULL,
    phone       VARCHAR(50),
    avatar_url  VARCHAR(500),
    gender      VARCHAR(20) DEFAULT 'UNKNOWN',
    position    VARCHAR(100),
    is_admin    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE companies ADD COLUMN IF NOT EXISTS tax_code VARCHAR(20);
ALTER TABLE companies ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE';
ALTER TABLE companies ADD COLUMN IF NOT EXISTS benefits TEXT;
ALTER TABLE companies ADD COLUMN IF NOT EXISTS logo_url VARCHAR(500);
ALTER TABLE companies ADD COLUMN IF NOT EXISTS cover_image_url VARCHAR(500);
ALTER TABLE companies ADD COLUMN IF NOT EXISTS website_url VARCHAR(300);
ALTER TABLE companies ADD COLUMN IF NOT EXISTS address VARCHAR(255);
ALTER TABLE companies ADD COLUMN IF NOT EXISTS company_size VARCHAR(100);
ALTER TABLE companies ADD COLUMN IF NOT EXISTS founded_year INT;
ALTER TABLE companies ADD COLUMN IF NOT EXISTS verification_status VARCHAR(50) DEFAULT 'PENDING';
ALTER TABLE companies ADD COLUMN IF NOT EXISTS business_license_file_id UUID;
ALTER TABLE companies ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE companies ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

ALTER TABLE company_images ADD COLUMN IF NOT EXISTS caption VARCHAR(255);
ALTER TABLE company_images ADD COLUMN IF NOT EXISTS display_order INT DEFAULT 0;
ALTER TABLE company_images ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();

ALTER TABLE employers ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE employers ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);
ALTER TABLE employers ADD COLUMN IF NOT EXISTS gender VARCHAR(20) DEFAULT 'UNKNOWN';
ALTER TABLE employers ADD COLUMN IF NOT EXISTS position VARCHAR(100);
ALTER TABLE employers ADD COLUMN IF NOT EXISTS is_admin BOOLEAN DEFAULT FALSE;
ALTER TABLE employers ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE employers ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Normalize existing values before applying NOT NULL constraints.
UPDATE companies
SET slug = 'company-' || replace(id::text, '-', '')
WHERE slug IS NULL OR btrim(slug) = '';

UPDATE companies
SET status = 'ACTIVE'
WHERE status IS NULL OR btrim(status) = '';

UPDATE companies
SET verification_status = 'PENDING'
WHERE verification_status IS NULL OR btrim(verification_status) = '';

UPDATE companies
SET tax_code = 'ITJ-' || upper(substr(replace(id::text, '-', ''), 1, 16))
WHERE tax_code IS NULL OR btrim(tax_code) = '';

-- Every existing employer without a company receives a safe placeholder company.
-- This allows company_id to become NOT NULL without deleting employer data.
WITH orphan_employers AS (
    SELECT id, name
    FROM employers
    WHERE company_id IS NULL
), created_companies AS (
    INSERT INTO companies (id, name, slug, tax_code, status, verification_status, created_at, updated_at)
    SELECT
        gen_random_uuid(),
        left(coalesce(nullif(btrim(name), ''), 'Employer Company'), 200),
        'employer-' || replace(id::text, '-', ''),
        'ITJ-' || upper(substr(replace(id::text, '-', ''), 1, 16)),
        'ACTIVE',
        'PENDING',
        NOW(),
        NOW()
    FROM orphan_employers
    RETURNING id, tax_code
)
UPDATE employers e
SET company_id = c.id,
    is_admin = COALESCE(e.is_admin, FALSE),
    updated_at = NOW()
FROM created_companies c
WHERE c.tax_code = 'ITJ-' || upper(substr(replace(e.id::text, '-', ''), 1, 16));

ALTER TABLE companies ALTER COLUMN slug SET NOT NULL;
ALTER TABLE companies ALTER COLUMN tax_code SET NOT NULL;
ALTER TABLE companies ALTER COLUMN status SET DEFAULT 'ACTIVE';
ALTER TABLE companies ALTER COLUMN status SET NOT NULL;
ALTER TABLE employers ALTER COLUMN company_id SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_companies_slug ON companies(slug);
CREATE UNIQUE INDEX IF NOT EXISTS uk_companies_tax_code ON companies(tax_code);
CREATE INDEX IF NOT EXISTS idx_company_images_company_id ON company_images(company_id);
CREATE INDEX IF NOT EXISTS idx_employers_user_id ON employers(user_id);
CREATE INDEX IF NOT EXISTS idx_employers_company_id ON employers(company_id);

-- The requested contract is one employer per company. Do not fail the migration
-- if legacy data contains shared companies; the existing application supports
-- company members and those rows must be reviewed before adding this constraint.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM employers
        WHERE company_id IS NOT NULL
        GROUP BY company_id
        HAVING COUNT(*) > 1
    ) THEN
        CREATE UNIQUE INDEX IF NOT EXISTS uk_employers_company_id ON employers(company_id);
    END IF;
END $$;

ALTER TABLE employers DROP CONSTRAINT IF EXISTS employers_company_id_fkey;
ALTER TABLE employers
    ADD CONSTRAINT employers_company_id_fkey
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT;
