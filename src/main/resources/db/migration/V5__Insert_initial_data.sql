-- Insert initial admin user (optional - for testing)
-- Note: In production, this should be done through a secure setup process
-- INSERT INTO users (phone_number, full_name, profession, years_of_experience, role) 
-- VALUES ('+1234567890', 'System Admin', 'Administrator', 5, 'ADMIN');

-- Create a function to clean up expired OTPs (for maintenance)
CREATE OR REPLACE FUNCTION cleanup_expired_otps()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM otp_verifications 
    WHERE expires_at < NOW() - INTERVAL '1 day';
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Add comments
COMMENT ON FUNCTION cleanup_expired_otps() IS 'Function to clean up expired OTP verification records older than 1 day';
