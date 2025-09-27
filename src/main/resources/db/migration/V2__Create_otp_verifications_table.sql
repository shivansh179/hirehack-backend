-- Create otp_verifications table
CREATE TABLE otp_verifications (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(255) NOT NULL,
    otp_code VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    attempts INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER NOT NULL DEFAULT 3
);

-- Create index on phone_number for faster lookups
CREATE INDEX idx_otp_verifications_phone_number ON otp_verifications(phone_number);

-- Create index on created_at for cleanup queries
CREATE INDEX idx_otp_verifications_created_at ON otp_verifications(created_at);

-- Create index on expires_at for expired OTP cleanup
CREATE INDEX idx_otp_verifications_expires_at ON otp_verifications(expires_at);

-- Create composite index for active OTP lookups
CREATE INDEX idx_otp_verifications_phone_active ON otp_verifications(phone_number, is_verified, expires_at);

-- Add comments to table and columns
COMMENT ON TABLE otp_verifications IS 'OTP verification records for phone number authentication';
COMMENT ON COLUMN otp_verifications.id IS 'Primary key - auto-generated OTP verification ID';
COMMENT ON COLUMN otp_verifications.phone_number IS 'Phone number for which OTP was generated';
COMMENT ON COLUMN otp_verifications.otp_code IS 'The OTP code sent to user';
COMMENT ON COLUMN otp_verifications.created_at IS 'OTP creation timestamp';
COMMENT ON COLUMN otp_verifications.expires_at IS 'OTP expiration timestamp';
COMMENT ON COLUMN otp_verifications.is_verified IS 'Whether OTP has been verified';
COMMENT ON COLUMN otp_verifications.attempts IS 'Number of verification attempts made';
COMMENT ON COLUMN otp_verifications.max_attempts IS 'Maximum allowed verification attempts';
