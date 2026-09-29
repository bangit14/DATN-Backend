CREATE TABLE IF NOT EXISTS cv_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    thumbnail_url VARCHAR(500),
    structure_json TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_account_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_key VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size_kb INT,
    purpose VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_files_owner_id ON files(owner_account_id);
CREATE INDEX IF NOT EXISTS idx_files_purpose ON files(purpose);

CREATE TABLE IF NOT EXISTS cvs (
    id BIGSERIAL PRIMARY KEY,
    student_id UUID NOT NULL,
    cv_name VARCHAR(255) NOT NULL,
    template_id UUID,
    content_json TEXT,
    pdf_key VARCHAR(500),
    cv_key VARCHAR(500),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    raw_text TEXT,
    nlp_status VARCHAR(50),
    processed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cv_norm (
    cv_id BIGINT PRIMARY KEY,
    years_total DOUBLE PRECISION,
    education_level VARCHAR(100),
    model_version VARCHAR(50),
    processed_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_cvs_student_id ON cvs(student_id);
