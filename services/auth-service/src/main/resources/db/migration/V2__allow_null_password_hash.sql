-- Allow password_hash to be NULL for OAuth2 / Google registered users
ALTER TABLE user_accounts ALTER COLUMN password_hash DROP NOT NULL;
