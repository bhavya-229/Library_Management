-- ========================================================
-- LIBRARY MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Database: library_db
-- ========================================================

-- 1. Create the database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 2. Clean up any existing tables in reverse dependency order
DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- ========================================================
-- TABLE 1: USERS
-- Purpose: System credentials and role management (ADMIN vs MEMBER)
-- ========================================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================================
-- TABLE 2: BOOKS
-- Purpose: Catalog of books with title, author, category, ISBN, and stock
-- ========================================================
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    publication_year INT NOT NULL,
    total_quantity INT NOT NULL DEFAULT 1,
    available_quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT chk_quantity CHECK (available_quantity >= 0 AND available_quantity <= total_quantity),
    CONSTRAINT chk_publication_year CHECK (publication_year >= 1000 AND publication_year <= 2100)
);

-- ========================================================
-- TABLE 3: MEMBERS
-- Purpose: Library patrons who can borrow books
-- Note: user_id is an optional link to a login account in `users`
-- ========================================================
CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    registration_date DATE NOT NULL,
    CONSTRAINT fk_member_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) 
        ON DELETE SET NULL 
        ON UPDATE CASCADE
);

-- ========================================================
-- TABLE 4: ISSUES
-- Purpose: Tracks book loans, returns, due dates, and fines
-- ========================================================
CREATE TABLE issues (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status ENUM('ISSUED', 'RETURNED') NOT NULL DEFAULT 'ISSUED',
    fine_amount DECIMAL(8, 2) DEFAULT 0.00,
    CONSTRAINT fk_issue_book FOREIGN KEY (book_id) 
        REFERENCES books(book_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_issue_member FOREIGN KEY (member_id) 
        REFERENCES members(member_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- ========================================================
-- SEED DATA (Initial records for testing)
-- ========================================================

-- Insert default admin and member user credentials
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'ADMIN'),
('john_doe', 'member123', 'MEMBER');

-- Insert initial books
INSERT INTO books (title, author, category, isbn, publication_year, total_quantity, available_quantity) VALUES 
('Effective Java', 'Joshua Bloch', 'Programming', '978-0134685991', 2018, 5, 5),
('Clean Code', 'Robert C. Martin', 'Software Engineering', '978-0132350884', 2008, 4, 4),
('Head First Java', 'Kathy Sierra', 'Programming', '978-1491910771', 2022, 3, 3),
('The Pragmatic Programmer', 'David Thomas', 'Software Engineering', '978-0135957059', 2019, 2, 2);

-- Insert initial member linked to john_doe (user_id = 2)
INSERT INTO members (user_id, name, email, phone, registration_date) VALUES 
(2, 'John Doe', 'john.doe@example.com', '9876543210', CURDATE());
