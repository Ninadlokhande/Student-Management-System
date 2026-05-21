# 🎓 EduStream SMS - Native Full-Stack Student Management System

EduStream SMS is a lightweight, high-performance, full-stack web ecosystem engineered from the ground up to handle student directories, academic course enrollments, institutional departments, and campus library loan loops.

Rather than relying on bulk enterprise frameworks like Spring Boot or high-overhead Object-Relational Mapping (ORM) tools like Hibernate, this system is built entirely upon **Vanilla Java Core Standard APIs**, raw native **JDBC (PreparedStatements)**, and an asynchronous **ES6 Vanilla JavaScript Single-Page Frontend**. This deliberate architectural choice demonstrates a profound, granular understanding of foundational client-server networking stacks, explicit HTTP lifecycle routing, manual object serialization, and atomic database persistence patterns.

---

## 🚀 Key Features & Engineering Highlights

* **Advanced Relational Dashboard:** Seamlessly tracks multi-layered student matrices, department metrics, course profiles, and live library loans through a single, responsive user interface.
* **Highly Normalized Data Architecture:** Enforces clean data design practices across 7 interconnected database tables linked via relational foreign keys and precise operational constraints.
* **Transactional Consistency (AC ACID Compliance):** Critical database write endpoints manage operations atomically by disabling autocommit (`conn.setAutoCommit(false)`). Complex multi-table writes (such as creating a student, evaluating/instantiating a course row, and opening a library ledger entry) execute as a single transaction. Any partial structural failure or network interruption triggers an instant, protective `conn.rollback()` loop to eliminate orphan entries or split database states.
* **Automated Cascading Constraints:** Integrates automated engine triggers (`ON DELETE CASCADE` and `SET NULL`) directly into the relational schema. Deleting a student profile cleans up matching records inside the database dynamically, preventing data bloat and dead memory leaks without requiring redundant query sequences.
* **Sleek Single-Page Architecture (SPA):** Features a responsive dashboard frontend built entirely with semantic HTML5, CSS3 tokenized custom variables, and asynchronous JavaScript `Fetch` API routines, eliminating full-page browser refreshes during data mutations.

---

## 🛠️ Tech Stack Matrix

* **Backend Core:** Java SE 8+ (Standard Core APIs, `com.sun.net.httpserver.HttpServer`, `com.sun.net.httpserver.HttpExchange`)
* **Persistence Layer:** Native JDBC Engine (`java.sql.Connection`, `java.sql.PreparedStatement`, `java.sql.ResultSet`)
* **Database Engine:** MySQL Community Server (InnoDb Relational Storage Engine)
* **Database Driver:** MySQL Connector/J Drivers (`v9.7.0`)
* **Frontend Web Layout:** HTML5 (Semantic Design), CSS3 (Modern Flexbox/Grid systems, Variable Styling Tokens), Vanilla JavaScript (ES6, Asynchronous Fetch Promises, Dynamic DOM Manipulation)

---

## 📂 Project Repository Directory Tree

Your folder organization must match this structure precisely to ensure that classpath dependencies resolve accurately during local compilation and deployment routines:

```text
Student-Management-System/
├── .gitignore                         # Excludes compiled binary files (*.class) from remote tracking
├── README.md                          # Production-ready architectural guide & developer documentation
├── schema.sql                         # Complete relational schema configuration & initialization script
├── mysql-connector-j-9.7.0.jar        # Core JDBC Database Driver dependency component
├── src/
│   └── StudentManagementSystem.java   # Central Java Backend server, routing handlers, and transactional repository
└── static/
    ├── index.html                     # Unified single-page web panel layout
    ├── style.css                      # Tokenized visual dashboard theme stylesheets
    └── script.js                      # Asynchronous REST client data stream controller
