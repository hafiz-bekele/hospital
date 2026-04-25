-- Run this if you already have the database set up and just need to add new features
USE hospital_db;

-- Add CANCELLED status to appointments (if not already present)
ALTER TABLE appointments MODIFY COLUMN status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING';

-- Add notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_read TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
