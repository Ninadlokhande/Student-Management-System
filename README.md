# Student Management System

a lightweight, high-performance, full-stack web application designed to manage student profiles, academic courses, department routing, and campus library metrics. 

Unlike standard beginner apps that rely on bulk frameworks like Spring Boot, this system is engineered from the ground up using **Vanilla Java Core APIs** (`com.sun.net.httpserver`), direct **JDBC**, and a custom JSON utility matrix. This architectural choice demonstrates deep fundamental mastery of low-level backend lifecycles, manual HTTP routing, and manual relational data binding.

---

## 🚀 Key Features

- **Advanced Relational Dashboard:** Seamlessly track students, metrics, departments, and active library loans on a single interface.
- **Normalized Data Architecture:** Implements a highly normalized relational system spanning 7 interconnected database tables.
- **Transactional Consistency:** Critical write endpoints leverage atomic SQL operations (`setAutoCommit(false)`) ensuring that complex, multi-table queries (such as simultaneously adding a student, an enrollment, and a book loan) either succeed entirely or roll back safely to prevent data corruption.
- **Cascading Operations:** Leverages automated `ON DELETE CASCADE` and `SET NULL` engine bindings to maintain flawless referential integrity.
- **Sleek Vanilla Frontend:** Features a modern, mobile-responsive single-page app architecture written in vanilla HTML5, CSS3 (using custom CSS variables and utility classes), and JavaScript (Asynchronous `Fetch` API).

---

## 🛠️ Tech Stack & Architecture

- **Backend:** Java SE 8+ (Core Java, `HttpServer`, `DriverManager`, PreparedStatements)
- **Database:** MySQL Community Server (Relational data engine)
- **Database Driver:** MySQL Connector/J (`v9.7.0`)
- **Frontend:** HTML5, CSS3 (Modern Flexbox/Grid layouts), Vanilla JavaScript (ES6)

---

## 📂 Project Structure

```text
├── s_m_s_backend/
│   ├── static/
│   │   ├── index.html       # Single Page Application UI Entry
│   │   ├── style.css        # Responsive dashboard design variables
│   │   └── script.js        # REST API Fetch controller logic
│   ├── src/
│   │   └── StudentManagementSystem.java  # Main Server Router & DB Repository
│   └── mysql-connector-j-9.7.0.jar       # JDBC Bridge Driver Core component
