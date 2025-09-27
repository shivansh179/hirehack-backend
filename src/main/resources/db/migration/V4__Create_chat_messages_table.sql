-- Create chat_messages table
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    interview_id BIGINT NOT NULL,
    sender_type VARCHAR(10) NOT NULL,
    message_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create foreign key constraint to interviews table
ALTER TABLE chat_messages 
ADD CONSTRAINT fk_chat_messages_interview_id 
FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE;

-- Create index on interview_id for faster lookups
CREATE INDEX idx_chat_messages_interview_id ON chat_messages(interview_id);

-- Create index on created_at for chronological ordering
CREATE INDEX idx_chat_messages_created_at ON chat_messages(created_at);

-- Create index on sender_type for filtering by message source
CREATE INDEX idx_chat_messages_sender_type ON chat_messages(sender_type);

-- Create composite index for interview message history
CREATE INDEX idx_chat_messages_interview_created ON chat_messages(interview_id, created_at ASC);

-- Add check constraint for sender_type values
ALTER TABLE chat_messages 
ADD CONSTRAINT chk_chat_messages_sender_type 
CHECK (sender_type IN ('USER', 'AI'));

-- Add comments to table and columns
COMMENT ON TABLE chat_messages IS 'Chat messages exchanged during interviews';
COMMENT ON COLUMN chat_messages.id IS 'Primary key - auto-generated message ID';
COMMENT ON COLUMN chat_messages.interview_id IS 'Foreign key reference to interviews table';
COMMENT ON COLUMN chat_messages.sender_type IS 'Type of sender: USER or AI';
COMMENT ON COLUMN chat_messages.message_text IS 'Content of the chat message';
COMMENT ON COLUMN chat_messages.created_at IS 'Message creation timestamp';
