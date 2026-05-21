import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class StudentManagementSystem {
    
    // --- DATABASE CREDENTIALS ---
    private static final String URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String USER = "root";
    private static final String PASS = "Ninad1010"; // Your password

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/", StudentManagementSystem::handleFile);
        server.createContext("/students", StudentManagementSystem::handleStudents);
        server.createContext("/add-student", StudentManagementSystem::handleAdd);
        server.createContext("/delete-student", StudentManagementSystem::handleDelete);
        server.createContext("/get-student", StudentManagementSystem::handleGetById);
        server.createContext("/update-student", StudentManagementSystem::handleUpdate);

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
        System.out.println("Serving static files from the 'static' folder.");
    }

    // --- 1. SERVE STATIC FILES ---
    private static void handleFile(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath().equals("/") ? "/index.html" : ex.getRequestURI().getPath();
        Path file = Paths.get("static" + path);
        
        if (Files.exists(file) && !Files.isDirectory(file)) {
            if (path.endsWith(".css")) ex.getResponseHeaders().set("Content-Type", "text/css");
            else if (path.endsWith(".js")) ex.getResponseHeaders().set("Content-Type", "application/javascript");
            else ex.getResponseHeaders().set("Content-Type", "text/html");
            
            ex.sendResponseHeaders(200, Files.size(file));
            Files.copy(file, ex.getResponseBody());
        } else {
            String error = "404 File Not Found";
            ex.sendResponseHeaders(404, error.length());
            ex.getResponseBody().write(error.getBytes());
        }
        ex.close();
    }

    // --- 2. GET ALL STUDENTS (Using JOIN) ---
    private static void handleStudents(HttpExchange ex) throws IOException {
        String sql = "SELECT s.student_id, s.first_name, s.last_name, s.date_of_birth, " +
                     "d.department_name, c.course_name, e.grade, b.title AS book_title, " +
                     "bl.borrow_date, bl.due_date, bl.status " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.department_id = d.department_id " +
                     "LEFT JOIN enrollments e ON s.student_id = e.student_id " +
                     "LEFT JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN book_loans bl ON s.student_id = bl.student_id " +
                     "LEFT JOIN books b ON bl.book_id = b.book_id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                json.append(formatStudentJson(rs));
                first = false;
            }
            sendJSON(ex, json.append("]").toString());
        } catch (SQLException e) { 
            e.printStackTrace(); 
            sendJSON(ex, "[]");
        }
    }

    // --- 3. GET STUDENT BY ID (Using JOIN) ---
    private static void handleGetById(HttpExchange ex) throws IOException {
        int id = Integer.parseInt(ex.getRequestURI().getQuery().split("=")[1]);
        String sql = "SELECT s.student_id, s.first_name, s.last_name, s.date_of_birth, " +
                     "d.department_name, c.course_name, e.grade, b.title AS book_title, " +
                     "bl.borrow_date, bl.due_date, bl.status " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.department_id = d.department_id " +
                     "LEFT JOIN enrollments e ON s.student_id = e.student_id " +
                     "LEFT JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN book_loans bl ON s.student_id = bl.student_id " +
                     "LEFT JOIN books b ON bl.book_id = b.book_id " +
                     "WHERE s.student_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sendJSON(ex, formatStudentJson(rs));
            } else {
                sendJSON(ex, "{}");
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
            sendJSON(ex, "{}");
        }
    }

    // --- 4. ADD NEW STUDENT (Database Transaction) ---
    private static void handleAdd(HttpExchange ex) throws IOException {
        String body = new BufferedReader(new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining());
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false); // Start Transaction

            try {
                // 1. Get or Create Department
                Integer deptId = getOrCreateDepartment(conn, getJsonValue(body, "dept"));
                
                // 2. Insert Student
                String insertStudent = "INSERT INTO students (first_name, last_name, date_of_birth, enrollment_date, department_id) VALUES (?, ?, ?, CURDATE(), ?)";
                int studentId = 0;
                try (PreparedStatement ps = conn.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, getJsonValue(body, "fname"));
                    ps.setString(2, getJsonValue(body, "lname"));
                    ps.setString(3, getJsonValue(body, "dob"));
                    if (deptId != null) ps.setInt(4, deptId); else ps.setNull(4, Types.INTEGER);
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) studentId = rs.getInt(1);
                }

                // 3. Insert Enrollment
                String courseName = getJsonValue(body, "course");
                if (!courseName.isEmpty()) {
                    Integer courseId = getOrCreateCourse(conn, courseName, deptId);
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO enrollments (student_id, course_id, grade) VALUES (?, ?, ?)")) {
                        ps.setInt(1, studentId);
                        ps.setInt(2, courseId);
                        ps.setString(3, getJsonValue(body, "grade"));
                        ps.executeUpdate();
                    }
                }

                // 4. Insert Book Loan
                String bookTitle = getJsonValue(body, "book");
                if (!bookTitle.isEmpty()) {
                    Integer bookId = getOrCreateBook(conn, bookTitle);
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO book_loans (student_id, book_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?)")) {
                        ps.setInt(1, studentId);
                        ps.setInt(2, bookId);
                        ps.setString(3, getJsonValue(body, "borrowDate"));
                        ps.setString(4, getJsonValue(body, "dueDate"));
                        ps.setString(5, getJsonValue(body, "status"));
                        ps.executeUpdate();
                    }
                }

                conn.commit(); // Save all changes
                ex.sendResponseHeaders(200, 0);
            } catch (SQLException e) {
                conn.rollback(); // Undo if error occurs
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ex.sendResponseHeaders(500, 0);
        }
        ex.close();
    }

    // --- 5. UPDATE STUDENT (Transaction: Wipe old links, insert new ones) ---
    private static void handleUpdate(HttpExchange ex) throws IOException {
        String body = new BufferedReader(new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining());
        int studentId = Integer.parseInt(getJsonValue(body, "id"));

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false); 
            try {
                // 1. Update Student Table
                Integer deptId = getOrCreateDepartment(conn, getJsonValue(body, "dept"));
                String updateStudent = "UPDATE students SET first_name=?, last_name=?, date_of_birth=?, department_id=? WHERE student_id=?";
                try (PreparedStatement ps = conn.prepareStatement(updateStudent)) {
                    ps.setString(1, getJsonValue(body, "fname"));
                    ps.setString(2, getJsonValue(body, "lname"));
                    ps.setString(3, getJsonValue(body, "dob"));
                    if (deptId != null) ps.setInt(4, deptId); else ps.setNull(4, Types.INTEGER);
                    ps.setInt(5, studentId);
                    ps.executeUpdate();
                }

                // 2. Clear old relationships
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM enrollments WHERE student_id=?")) { ps.setInt(1, studentId); ps.executeUpdate(); }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM book_loans WHERE student_id=?")) { ps.setInt(1, studentId); ps.executeUpdate(); }

                // 3. Re-insert Enrollment
                String courseName = getJsonValue(body, "course");
                if (!courseName.isEmpty()) {
                    Integer courseId = getOrCreateCourse(conn, courseName, deptId);
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO enrollments (student_id, course_id, grade) VALUES (?, ?, ?)")) {
                        ps.setInt(1, studentId); ps.setInt(2, courseId); ps.setString(3, getJsonValue(body, "grade"));
                        ps.executeUpdate();
                    }
                }

                // 4. Re-insert Book Loan
                String bookTitle = getJsonValue(body, "book");
                if (!bookTitle.isEmpty()) {
                    Integer bookId = getOrCreateBook(conn, bookTitle);
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO book_loans (student_id, book_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?)")) {
                        ps.setInt(1, studentId); ps.setInt(2, bookId); 
                        ps.setString(3, getJsonValue(body, "borrowDate")); ps.setString(4, getJsonValue(body, "dueDate")); ps.setString(5, getJsonValue(body, "status"));
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                ex.sendResponseHeaders(200, 0);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace(); ex.sendResponseHeaders(500, 0);
        }
        ex.close();
    }

    // --- 6. DELETE STUDENT ---
    private static void handleDelete(HttpExchange ex) throws IOException {
        int id = Integer.parseInt(ex.getRequestURI().getQuery().split("=")[1]);
        // Thanks to "ON DELETE CASCADE" in your SQL, deleting the student automatically cleans up enrollments and book loans!
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            ex.sendResponseHeaders(200, 0);
        } catch (SQLException e) { 
            e.printStackTrace(); ex.sendResponseHeaders(500, 0);
        }
        ex.close();
    }

    // ==========================================
    // --- HELPER METHODS FOR SQL RELATIONSHIPS ---
    // ==========================================

    private static Integer getOrCreateDepartment(Connection conn, String name) throws SQLException {
        if (name == null || name.isEmpty()) return null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT department_id FROM departments WHERE department_name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO departments (department_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return null;
    }

    private static Integer getOrCreateCourse(Connection conn, String name, Integer deptId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT course_id FROM courses WHERE course_name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO courses (course_name, credits, department_id) VALUES (?, 3, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            if (deptId != null) ps.setInt(2, deptId); else ps.setNull(2, Types.INTEGER);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return null;
    }

    private static Integer getOrCreateBook(Connection conn, String title) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT book_id FROM books WHERE title = ?")) {
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO books (title, author, total_copies, available_copies) VALUES (?, 'Unknown', 1, 1)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title); ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return null;
    }

    private static String formatStudentJson(ResultSet rs) throws SQLException {
        String dept = rs.getString("department_name") != null ? rs.getString("department_name") : "";
        String course = rs.getString("course_name") != null ? rs.getString("course_name") : "";
        String book = rs.getString("book_title") != null ? rs.getString("book_title") : "";
        String status = rs.getString("status") != null ? rs.getString("status") : "";
        String dob = rs.getString("date_of_birth") != null ? rs.getString("date_of_birth") : "";
        String bDate = rs.getString("borrow_date") != null ? rs.getString("borrow_date") : "";
        String dDate = rs.getString("due_date") != null ? rs.getString("due_date") : "";
        String grade = rs.getString("grade") != null ? rs.getString("grade") : "";

        return String.format("{\"id\":%d, \"fname\":\"%s\", \"lname\":\"%s\", \"dob\":\"%s\", \"dept\":\"%s\", \"course\":\"%s\", \"grade\":\"%s\", \"book\":\"%s\", \"borrowDate\":\"%s\", \"dueDate\":\"%s\", \"status\":\"%s\"}",
            rs.getInt("student_id"), rs.getString("first_name"), rs.getString("last_name"), dob, dept, course, grade, book, bDate, dDate, status);
    }

    private static void sendJSON(HttpExchange ex, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(200, response.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(response); }
    }

    private static String getJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            return end != -1 ? json.substring(start, end) : "";
        } else {
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            String val = json.substring(start, end).trim();
            return val.equals("null") ? "" : val;
        }
    }
}