CREATE DATABASE student_management;
USE student_management;
CREATE TABLE users (
user_id INT auto_increment primary KEY,
username VARCHAR(50) UNIQUE NOT NULL,
password_hash VARCHAR(255) NOT NULL,
role ENUM('admin','teacher','student') NOT NULL
);

CREATE TABLE students(
student_id INT auto_increment primary KEY,
first_name varchar(50)  NOT NULL,
last_name VARCHAR(50) NOT NULL,
date_of_birth DATE,
enrollment_date DATE,
user_id INT,
foreign KEY (user_id) references users(user_id) ON DELETE cascade
);

CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    teacher_id INT, 
    credits INT NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    grade VARCHAR(2),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);


CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('present', 'absent', 'late') NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);
CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) UNIQUE NOT NULL,
    head_of_department_id INT,
    FOREIGN KEY (head_of_department_id) REFERENCES users(user_id) ON DELETE SET NULL
);
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1
);

CREATE TABLE book_loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    student_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    return_date DATE,
    due_date DATE NOT NULL,
    status ENUM('borrowed', 'returned', 'overdue') DEFAULT 'borrowed',
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Add department_id to students
ALTER TABLE students ADD COLUMN department_id INT;
ALTER TABLE students ADD FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL;

-- Add department_id to courses
ALTER TABLE courses ADD COLUMN department_id INT;
ALTER TABLE courses ADD FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL;

-- 1. Insert Departments (Including ENTC)
INSERT INTO departments (department_name) VALUES 
('Computer Science'), 
('ENTC Dept');

-- 2. Insert Users (1 Admin, 1 Teacher, 3 Students)
-- Ninad is now the Admin!
INSERT INTO users (username, password_hash, role) VALUES
('admin_ninad', 'ninad123', 'admin'),
('teacher_sharma', 'sharma123', 'teacher'),
('student_amar', 'amar123', 'student'),
('student_atharva', 'atharva123', 'student'),
('student_vijay', 'vijay123', 'student');

-- 3. Assign the Teacher as the Head of the ENTC Dept (Department ID 2)
UPDATE departments SET head_of_department_id = 2 WHERE department_id = 2;

-- 4. Insert Students (Linked to the User accounts and the ENTC Dept)
-- Amar, Atharva, and Vijay are enrolled as students.
INSERT INTO students (first_name, last_name, date_of_birth, enrollment_date, user_id, department_id) VALUES
('Amar', 'Patil', '2001-08-22', '2023-09-01', 3, 2),
('Atharva', 'Deshmukh', '2002-01-10', '2023-09-01', 4, 2),
('Vijay', 'Kumar', '2001-11-30', '2023-09-01', 5, 2);

-- 5. Insert Courses (ENTC Specific)
INSERT INTO courses (course_name, teacher_id, credits, department_id) VALUES
('Signals and Systems', 2, 4, 2),
('Microcontrollers', 2, 3, 2);

-- 6. Insert Books for the Library
INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES
('Digital Signal Processing', 'John G. Proakis', '978-0131873742', 5, 5),
('Clean Code', 'Robert C. Martin', '978-0132350884', 3, 3);