-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS yummly_db;

-- Use the database
USE yummly_db;

-- Grant privileges to root user
GRANT ALL PRIVILEGES ON yummly_db.* TO 'root'@'localhost';

-- Flush privileges to apply changes
FLUSH PRIVILEGES; 