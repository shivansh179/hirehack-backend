-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    profession VARCHAR(255),
    years_of_experience INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    resume_text TEXT,
    role VARCHAR(50) NOT NULL DEFAULT 'USER'
);

-- Create index on phone_number for faster lookups
CREATE INDEX idx_users_phone_number ON users(phone_number);

-- Create index on role for admin queries
CREATE INDEX idx_users_role ON users(role);

-- Create index on created_at for time-based queries
CREATE INDEX idx_users_created_at ON users(created_at);

-- Add comments to table and columns
COMMENT ON TABLE users IS 'User accounts with phone number authentication';
COMMENT ON COLUMN users.id IS 'Primary key - auto-generated user ID';
COMMENT ON COLUMN users.phone_number IS 'Unique phone number for authentication';
COMMENT ON COLUMN users.full_name IS 'User full name';
COMMENT ON COLUMN users.profession IS 'User profession/role';
COMMENT ON COLUMN users.years_of_experience IS 'Years of professional experience';
COMMENT ON COLUMN users.created_at IS 'Account creation timestamp';
COMMENT ON COLUMN users.resume_text IS 'User resume content in text format';
COMMENT ON COLUMN users.role IS 'User role: USER or ADMIN';
