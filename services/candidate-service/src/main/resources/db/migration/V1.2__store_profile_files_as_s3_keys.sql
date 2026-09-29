-- Store only S3 object keys. URLs are generated with presignedUrl() by the API.
ALTER TABLE candidate_profiles
    ADD COLUMN IF NOT EXISTS avatar_key VARCHAR(500),
    ADD COLUMN IF NOT EXISTS cv_key VARCHAR(500);

-- Preserve existing data when the old columns contain normal S3 URLs.
UPDATE candidate_profiles
SET avatar_key = regexp_replace(avatar_url, '^https?://[^/]+/', '')
WHERE avatar_key IS NULL AND avatar_url IS NOT NULL AND avatar_url <> '';

UPDATE candidate_profiles
SET cv_key = regexp_replace(cv_url, '^https?://[^/]+/', '')
WHERE cv_key IS NULL AND cv_url IS NOT NULL AND cv_url <> '';

ALTER TABLE candidate_profiles
    DROP COLUMN IF EXISTS avatar_url,
    DROP COLUMN IF EXISTS cv_url;
