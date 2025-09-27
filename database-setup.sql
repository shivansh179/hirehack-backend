-- Database setup script for HireHack application
-- Run this script as a PostgreSQL superuser to set up the database and user

-- Create database
CREATE DATABASE hirehacks;

-- Create user (if not exists)
-- Note: In production, use a more secure password
DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'postgres') THEN

      CREATE ROLE postgres LOGIN PASSWORD '1234';
   END IF;
END
$do$;

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON DATABASE hirehacks TO postgres;

-- Connect to the hirehacks database and grant schema privileges
\c hirehacks;

-- Grant privileges on the public schema
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO postgres;

-- Display success message
\echo 'Database hirehacks created successfully!';
\echo 'User postgres configured with password 1234';
\echo 'You can now run the application migrations.';
