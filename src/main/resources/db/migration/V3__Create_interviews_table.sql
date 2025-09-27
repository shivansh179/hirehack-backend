-- Create interviews table
CREATE TABLE interviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interview_duration_minutes INTEGER,
    status VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    ended_at TIMESTAMP WITH TIME ZONE,
    role VARCHAR(255),
    skills VARCHAR(500),
    interview_type VARCHAR(100),
    feedback TEXT
);

-- Create foreign key constraint to users table
ALTER TABLE interviews 
ADD CONSTRAINT fk_interviews_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Create index on user_id for faster lookups
CREATE INDEX idx_interviews_user_id ON interviews(user_id);

-- Create index on status for filtering
CREATE INDEX idx_interviews_status ON interviews(status);

-- Create index on created_at for time-based queries
CREATE INDEX idx_interviews_created_at ON interviews(created_at);

-- Create index on ended_at for completed interviews
CREATE INDEX idx_interviews_ended_at ON interviews(ended_at);

-- Create composite index for user's interview history
CREATE INDEX idx_interviews_user_created ON interviews(user_id, created_at DESC);

-- Add comments to table and columns
COMMENT ON TABLE interviews IS 'Interview sessions conducted with users';
COMMENT ON COLUMN interviews.id IS 'Primary key - auto-generated interview ID';
COMMENT ON COLUMN interviews.user_id IS 'Foreign key reference to users table';
COMMENT ON COLUMN interviews.interview_duration_minutes IS 'Duration of the interview in minutes';
COMMENT ON COLUMN interviews.status IS 'Current status of the interview';
COMMENT ON COLUMN interviews.created_at IS 'Interview start timestamp';
COMMENT ON COLUMN interviews.ended_at IS 'Interview end timestamp';
COMMENT ON COLUMN interviews.role IS 'Role being interviewed for';
COMMENT ON COLUMN interviews.skills IS 'Skills being assessed in the interview';
COMMENT ON COLUMN interviews.interview_type IS 'Type of interview (technical, behavioral, etc.)';
COMMENT ON COLUMN interviews.feedback IS 'Interview feedback and evaluation';
