/**
 * Student Management System - Frontend Business Logic
 * Compatible 100% with Java backend endpoints and JSON schema
 */

// 1. Navigation Controller
function showPage(page) {
    document.querySelectorAll('.page-card').forEach(p => p.style.display = 'none');
    document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
    
    if (page === 'view') {
        document.getElementById('viewPage').style.display = 'block';
        document.getElementById('nav-view').classList.add('active');
        fetchStudents(); 
    } else {
        document.getElementById('formPage').style.display = 'block';
        document.getElementById('nav-form').classList.add('active');
    }
}

// 2. READ: Stream records from Java Backend
function fetchStudents() {
    const body = document.getElementById('studentBody');
    body.innerHTML = '<tr><td colspan="6" style="text-align:center; color: var(--text-muted); padding: 30px;">Loading records from server...</td></tr>';

    fetch('/students')
        .then(res => res.json())
        .then(data => {
            if (!data || data.length === 0) {
                body.innerHTML = '<tr><td colspan="6" style="text-align:center; color: var(--text-muted); padding: 40px;">No students found in the database.</td></tr>';
                return;
            }

            body.innerHTML = data.map(s => {
                const cleanStatus = (s.status ? s.status.toLowerCase().trim() : 'borrowed');
                
                return `
                    <tr>
                        <td style="font-weight: 600; color: #fff;">${escapeHtml(s.fname)} ${escapeHtml(s.lname || '')}</td>
                        <td>${escapeHtml(s.dept || '-')}</td>
                        <td>${escapeHtml(s.course || '-')}</td>
                        <td style="font-style: italic; color: var(--text-muted);">${escapeHtml(s.book || '-')}</td>
                        <td><span class="badge ${cleanStatus}">${cleanStatus}</span></td>
                        <td>
                            <div class="actions-cell">
                                <button class="btn-inline-edit" onclick="editStudent(${s.id})">Edit</button>
                                <button class="btn-inline-delete" onclick="deleteStudent(${s.id})">Delete</button>
                            </div>
                        </td>
                    </tr>
                `;
            }).join('');
        })
        .catch(error => {
            console.error("Critical error reading operational stack:", error);
            body.innerHTML = '<tr><td colspan="6" style="text-align:center; color: var(--badge-overdue-color); padding: 30px;">Unable to fetch student records. Ensure the Java server is running.</td></tr>';
        });
}

// 3. CREATE/UPDATE: Route form entries to Java Controllers
function saveData() {
    const id = document.getElementById('studentId').value;
    
    const studentData = {
        id: id ? id : null,
        fname: document.getElementById('fname').value.trim(),
        lname: document.getElementById('lname').value.trim(),
        dob: document.getElementById('dob').value,
        dept: document.getElementById('dept').value.trim(),
        course: document.getElementById('course').value.trim(),
        grade: document.getElementById('grade').value.trim(),
        book: document.getElementById('book').value.trim(),
        borrowDate: document.getElementById('borrowDate').value,
        dueDate: document.getElementById('dueDate').value,
        status: document.getElementById('status').value
    };

    if (!studentData.fname || !studentData.lname) {
        alert("Please provide both First Name and Last Name.");
        return;
    }

    const url = id ? '/update-student' : '/add-student';

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(studentData)
    })
    .then(res => {
        if (res.ok) {
            document.getElementById('studentForm').reset();
            document.getElementById('studentId').value = '';
            showPage('view'); 
        } else {
            alert("Transactional write failed on Java backend.");
        }
    })
    .catch(error => console.error("Error writing data back to Java:", error));
}

// 4. EDIT: Populate form from Java endpoint
function editStudent(id) {
    fetch(`/get-student?id=${id}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('studentId').value = data.id || '';
            document.getElementById('fname').value = data.fname || '';
            document.getElementById('lname').value = data.lname || '';
            document.getElementById('dob').value = data.dob || '';
            document.getElementById('dept').value = data.dept || '';
            document.getElementById('course').value = data.course || '';
            document.getElementById('grade').value = data.grade || '';
            document.getElementById('book').value = data.book || '';
            document.getElementById('borrowDate').value = data.borrowDate || '';
            document.getElementById('dueDate').value = data.dueDate || '';
            document.getElementById('status').value = data.status || 'borrowed';
            
            document.getElementById('formTitle').innerText = "Modify Active Student Record";
            showPage('form');
        })
        .catch(error => console.error("Could not trace student metadata:", error));
}

// 5. DELETE: Remove record via Java endpoint
function deleteStudent(id) {
    if (confirm("Are you sure you want to permanently delete this record?")) {
        fetch(`/delete-student?id=${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    fetchStudents(); 
                } else {
                    alert("Unable to delete record on Java server.");
                }
            })
            .catch(error => console.error("Error issuing delete request:", error));
    }
}

// 6. Form reset for adding a new student
function openAddForm() {
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    document.getElementById('formTitle').innerText = "Student Record Sheet";
    showPage('form');
}

// Simple HTML sanitizer helper
function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

// 7. Initialization
document.addEventListener("DOMContentLoaded", () => {
    showPage('view');
});
