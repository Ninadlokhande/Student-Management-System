Here is your complete, comprehensive, and copy-pasteable All-In-One README.md file.Since you don't have image assets ready to upload to GitHub, I have built highly professional, clean ASCII text diagrams directly into the Markdown layout. These will display perfectly as beautiful visual maps directly on your repository page to explain your data flows and database schema design to recruiters.Copy and paste the entire text block below into a file named exactly README.md in your root folder:Markdown# 🎓 EduStream SMS - Native Full-Stack Student Management System

EduStream SMS is a high-performance, single-file backend web ecosystem engineered from the ground up to handle student directories, academic course enrollments, institutional departments, and campus library metrics.

Rather than relying on bulk enterprise frameworks like Spring Boot or heavy Object-Relational Mapping (ORM) tools like Hibernate, this application is written entirely using **Vanilla Java Core APIs**, raw native **JDBC (PreparedStatements)**, and an asynchronous **ES6 Vanilla JavaScript Frontend**. This specific architectural choice demonstrates a profound, low-level understanding of core client-server networking sockets, explicit HTTP lifecycle routing, manual stream serialization, and data persistence pipelines.

---

## 🗺️ System Architecture & Data Flow Matrix

Below is the diagrammatic representation of how data moves asynchronously through your application stack without requiring third-party libraries:

```text
+---------------------------------------------------------------------------------------+
|                                    CLIENT SUBSYSTEM                                   |
|                                                                                       |
|   +-----------------------+                    +----------------------------------+   |
|   |   Semantic UI Page    |                    |       Asynchronous ES6           |   |
|   |  (HTML5 / CSS3 Grid)  | <--- (DOM Bind) -- |       Fetch API Engine           |   |
|   +-----------------------+                    +----------------------------------+   |
+------------------------------------------------------------------|--------------------+
                                                                   |
                                                      (HTTP POST / GET / DELETE)
                                                                   |
                                                                   v
+------------------------------------------------------------------|--------------------+
|                                    BACKEND CORE                                       |
|                                                                   |                   |
|   +---------------------------------------------------------------+                   |
|   |  Native Java HttpServer Socket Router (com.sun.net.httpserver on Port 8080)       |
|   +---------------------------------------------------------------|                   |
|                                                                   v                   |
|   +--------------------------------------------------------------------------------+  |
|   |  Atomic Transaction & Persistence Manager (java.sql.PreparedStatement Core)   |  |
|   +---------------------------------------------------------------|----------------+  |
+-------------------------------------------------------------------|-------------------+
                                                                    |
                                                          (Native JDBC Driver Protocol)
                                                                    |
                                                                    v
+-------------------------------------------------------------------|-------------------+
|                                 PERSISTENCE REPOSITORY LAYER                         |
|                                                                                       |
|                      +------------------------------------------+                     |
|                      |   MySQL Local DB Server Engine (InnoDb)  |                     |
|                      +------------------------------------------+                     |
+---------------------------------------------------------------------------------------+
🚀 Key Features & Architectural HighlightsAdvanced Relational Dashboard: Tracks complex multi-layered student profiles, departments, active course enrollments, and live library loans within a single, highly interactive single-page dashboard.Strict ACID Transaction Handling: Core write operations run under manual transactional blocks (conn.setAutoCommit(false)). Multi-stage data mutations (such as logging a student profile, cross-checking/instantiating a missing course row, and opening a live library loan ledger entry) execute as a single, atomic operation. Any system failure or network drop triggers an immediate conn.rollback() routine, protecting the persistence layers from structural corruption and orphaned records.Manual Data Aggregation: Data fragments scattered across a deeply normalized database are assembled on-the-fly using advanced SQL LEFT JOIN execution matrices, completely bypassing framework latency.Flawless Referential Integrity: Relational structures are hardcoded with foreign key triggers (ON DELETE CASCADE and SET NULL) to automate systemic cleaning loops, eliminating database bloat.Tokenized UI Design Palette: Designed using semantic modern markup grids, utility class structures, custom CSS variable tokens, smooth micro-interactions, and color-coded status badges (Borrowed, Returned, Overdue).📂 Project Repository Directory TreeYour directory layout must look exactly like this to ensure that runtime paths and classpath compilation arguments find files correctly:Plaintexts_m_s_backend/
├── .gitignore                         # Excludes temporary compiler binaries (*.class)
├── README.md                          # Production-ready architectural guide & developer documentation
├── schema.sql                         # Complete relational schema configuration & initialization script
├── mysql-connector-j-9.7.0.jar        # Core JDBC Database Driver dependency component
├── src/
│   └── StudentManagementSystem.java   # Central Java Backend server, routing handlers, and transactional repository
└── static/
    ├── index.html                     # Unified single-page web panel layout
    ├── style.css                      # Tokenized visual dashboard theme stylesheets
    └── script.js                      # Asynchronous REST client data stream controller
📊 Database Blueprint & Normalized SchemaThe system interfaces directly with a 7-table deeply normalized schema engineered to eliminate data redundancy, enforce data domain parameters, and establish explicit logical relationships:Plaintext       +-----------------------+              +-----------------------+
       |         users         |              |      departments      |
       +-----------------------+              +-----------------------+
       | PK | user_id          | <--------+   | PK | department_id    |
       |    | username         |          |   |    | department_name  |
       |    | password_hash    |          |   | FK | head_of_dept_id  |
       |    | role (Enum)      |          |   +-----------------------+
       +-----------------------+          |               ^
                   ^                      |               |
                   |                      +-------+       |
                   |                              |       |
       +-----------------------+                  |       |
       |       students        | -----------------+       |
       +-----------------------+                          |
       | PK | student_id       | <--------+               |
       |    | first_name       |          |               |
       |    | last_name        |          |               |
       |    | date_of_birth    |          |               |
       |    | enrollment_date  |          |               |
       | FK | user_id          |          |               |
       | FK | department_id    |          |               |
       +-----------------------+          |               |
         |                   |            |               |
         |                   v            |               |
         |     +-----------------------+  |   +-----------------------+
         |     |      book_loans       |  |   |        courses        |
         |     +-----------------------+  |   +-----------------------+
         |     | PK | loan_id          |  |   | PK | course_id        |
         |     | FK | book_id          |  |   |    | course_name      |
         |     | FK | student_id       |  |   | FK | teacher_id       |
         |     |    | borrow_date      |  |   |    | credits          |
         |     |    | return_date      |  |   | FK | department_id    |
         |     |    | due_date         |  |   +-----------------------+
         |     |    | status (Enum)    |  |               ^
         |     +-----------------------+  |               |
         |                 |              |               |
         v                 v              |               |
       +-----------------------+          |               |
       |      enrollments      | ---------+---------------+
       +-----------------------+          |
       | PK | enrollment_id    |          |
       | FK | student_id       |          |
       | FK | course_id        |          |
       |    | grade            |          |
       +-----------------------+          |
                                          |
       +-----------------------+          |
       |         books         |          |
       +-----------------------+          |
       | PK | book_id          | <--------+
       |    | title            |
       |    | author           |
       |    | isbn             |
       |    | total_copies     |
       |    | available_copies |
       +-----------------------+

💻 Required Systems Environment & Dev Tools1. Mandated System ProgramsJava Development Kit (JDK 8 or Higher): Necessary to compile syntax trees and manage the server process threads.MySQL Community Server (v8.0+): Local relational database infrastructure running on system port 3306 to house the storage layer.Web Browser Engine: (e.g., Google Chrome, Brave, Edge, or Firefox) with updated JavaScript runtime engines for optimal layout rendering.2. Recommended IDE Tooling (VS Code Productivity Extensions)Extension Pack for Java (by Microsoft): Automatically flags syntax bugs and resolves compiler paths for standalone class files.Prettier - Code Formatter: Automatically structures your HTML indents, formats Javascript blocks, and tracks uniform padding rules inside CSS blocks on save.MySQL (by Weijan Chen): Allows you to interact with database records and run SQL queries inside tabs within your code editor without opening separate external tools.🚀 Environment Setup & Execution ManualFollow these comprehensive initialization steps sequentially to compile, link, and deploy the application locally:Step 1: Execute Database SetupLog into your local MySQL terminal command line interface or system workbench tool, and execute your schema initialization migrations:SQLCREATE DATABASE student_management;
USE student_management;

-- Run the query tables defined inside the `schema.sql` file.
Step 2: Configure System Connectivity CredentialsOpen src/StudentManagementSystem.java inside your editor, locate the system credentials block on lines 11-13, and update the data values to mirror your local environmental properties precisely:Javaprivate static final String URL = "jdbc:mysql://localhost:3306/student_management";
private static final String USER = "root";
private static final String PASS = "YOUR_PERSONAL_LOCAL_MYSQL_PASSWORD"; // <-- Update this parameter string!
Step 3: Compile the Full-Stack Application ArchitectureOpen a command prompt terminal directly inside the Main Project Root Directory (s_m_s_backend/ folder where your static asset folder lives, not from inside the src/ directory). Execute the Java compiler while passing the driver jar into your classpath argument mapping configurations:Windows Command Line:Bashjavac -cp ".;mysql-connector-j-9.7.0.jar" src/StudentManagementSystem.java
macOS / Linux Terminal:Bashjavac -cp ".:mysql-connector-j-9.7.0.jar" src/StudentManagementSystem.java
Step 4: Launch the Live Backend Web EngineBoot up the multi-threaded network router directly out of your main directory workspace folder by passing the compiled path boundaries along with the class library dependency configuration jar directly to the virtual machine instruction thread:Windows Command Line:Bashjava -cp "src;mysql-connector-j-9.7.0.jar" StudentManagementSystem
macOS / Linux Terminal:Bashjava -cp "src:mysql-connector-j-9.7.0.jar" StudentManagementSystem
Upon a successful server lifecycle launch, your console terminal will log:PlaintextServer started on http://localhost:8080
Serving static files from the 'static' folder.
Step 5: Render the Live Client Dashboard PanelOpen your modern browser window and navigate to the local environment address loop: http://localhost:8080📊 Comprehensive Application Use Cases & System LifecyclesThis full-stack system manages complete, asynchronous data processing cycles across highly decoupled architectural boundaries:Use Case 1: Hydrating the Main Dashboard Data Grid (READ)Client Network Request Execution: When an operator lands on the dashboard or navigates back to the "View Records" layout, the script triggers an asynchronous ES6 fetch() call targeted at the /students endpoint.Backend Database Engine Behavior: The Java server captures the incoming connection socket and initiates a multi-table LEFT JOIN statement combining metrics across students, departments, enrollments, courses, book_loans, and books down a single optimized access pipeline.Stream Serialization & UI Injection: The server scrubs empty parameters resulting from unmatched data nodes, maps values into a custom JSON text array stream, and pushes it back down the connection socket. The JavaScript runtime catches the data array and converts parameters into a secure grid, dynamically assigning colored HTML status pill badges based on real-time database loan states.Use Case 2: Multi-Table Atomic Profile Logging (CREATE)Client Network Request Execution: The operator inputs details into the profile configuration input sheet and clicks "Commit Changes". JavaScript catches the structural array inputs and issues a POST request containing a stringified JSON payload straight to /add-student.Backend Database Engine Behavior (ACID Transaction Safety): To secure operations spanning multiple independent target tables, Java locks the execution thread using conn.setAutoCommit(false):It checks the departments table for the input department name. If missing, it inserts the department on the fly and retrieves the generated key.It writes primary name strings and dates into the core students table, capturing the unique, newly assigned student_id.It verifies whether the input course matches an existing curriculum record. If the course is missing, Java creates it dynamically and logs the relationship within the enrollments table alongside terminal grades.It mirrors this tracking look-up sequence to insert library accountability boundaries inside the book_loans ledger.Finally, conn.commit() applies the data updates across all tables simultaneously. If a failure occurs midway, conn.rollback() executes, reverting the entire operation to prevent orphaned records.Use Case 3: Targeted Profile Modification (UPDATE)Client Network Request Execution: Clicking ⚙️ Edit targets a row's primary reference ID, issuing an immediate query parameter path request to /get-student?id={id} to populate the input form. Once modified, the payload routes to /update-student.Backend Database Engine Behavior: The persistence module initializes a manual transaction block. It executes a clear wipe of the student's existing relationship bindings inside the enrollments and book_loans bridge tables, applies the new parameters directly to the central master students row, and re-inserts fresh structural mapping references down the relational schema.Use Case 4: Cascading Structural Profile Removal (DELETE)Client Network Request Execution: Clicking 🗑️ Delete launches a confirmation alert modal. Upon user approval, an explicit HTTP DELETE network call routes to /delete-student?id={id}.Backend Database Engine Behavior: Thanks to the strict ON DELETE CASCADE constraints embedded inside your tables, dropping the central profile automatically clears the corresponding tracking records across the enrollments and book_loans tables, ensuring the system remains clean and bloat-free without requiring manual sub-query cleanups.🎯 REST API Routing Architecture Reference MatrixHTTP MethodOperational EndpointRequired Parameters / Payload ShapeTarget Server Lifecycle Reaction & Processing ExecutionGET/NoneDiscovers resource file configurations and copies static client files out of the stream.GET/studentsNoneRuns optimized multi-table SQL JOIN statements and outputs a unified clean JSON text array.GET/get-studentQuery Parameter: ?id={student_id}Traces a distinct student record profile by matching primary indices and returns a populated JSON dataset.POST/add-studentJSON Request Object BodyDecodes incoming JSON parameter nodes and launches multi-stage atomic ACID writes across separate tables.POST/update-studentJSON Request Object BodyDeletes historical relationship bridge tables and applies updated records to the target profile.DELETE/delete-studentQuery Parameter: ?id={student_id}Drops the targeted index row from disk and triggers an automated cascading relational cleanup.🛠️ Developer Playbook: Where to Make CustomizationsTo scale up this system, append modern interface modules, or expand input boundaries to match specific portfolio tracking milestones, use this comprehensive code modification guide:1. Editing Database Queries & Mapping Core Column SchemasTarget Code File: src/StudentManagementSystem.javaCustomization Guide: Locate the formatStudentJson() wrapper method to expand variable mappings, update specific SQL parameter array adjustments inside handleUpdate() or handleAdd(), or write completely new endpoint matching scenarios within the server's main() routing loop block.2. Introducing Form Inputs or Adding Grid Data Table ColumnsTarget Code File: static/index.htmlCustomization Guide: Search for the structural form container <form id="studentForm"> to inject text fields, custom select dropdowns, or date pickers. To introduce new database metrics into the visual interface, append new column headings inside the table element's structural <thead> component.3. Adjusting CSS Layout Themes and Visual Token PalettesTarget Code File: static/style.cssCustomization Guide: Customize the entire application theme instantly by modifying the design token definitions mapped under the :root pseudo-class configuration block at the very top of the stylesheet:CSS--primary-color: #4f46e5;    /* Primary accent theme color applied to headers and commit button blocks */
--nav-bg: #0f172a;           /* Visual tint bar theme applied to the global header element */
--bg-primary: #f8fafc;       /* Body canvas background color theme used for data dashboard components */
4. Injecting JavaScript Object Mapping Properties & Form Data PayloadsTarget Code File: static/script.jsCustomization Guide: Navigate to the saveData() method block. When introducing a new input element to your HTML file, read its selector ID value into the studentData JSON schema map so it is bundled correctly into the outbound request payload.
