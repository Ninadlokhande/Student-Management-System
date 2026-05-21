/**
 * Student Management System - Frontend Business Logic
 */

// 1. Navigation Controller (Switches layout view instances cleanly)
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

// 2. READ: Stream array instances directly out of Backend pipeline
function fetchStudents() {
    fetch('/students')
        .then(res => res.json())
        .then(data => {
            const body = document.getElementById('studentBody');
            
            if (!data || data.length === 0) {
                body.innerHTML = '<tr><td colspan="6" style="text-align:center; color: var(--text-muted); padding: 40px;">No students found in the database.</td></tr>';
                return;
            }

            body.innerHTML = data.map(s => {
                // Ensure status safely maps to lower-case values for badging CSS targets
                const cleanStatus = s.status ? s.status.toLowerCase().trim() : 'borrowed';
                
                return `
                    <tr>
                        <td style="font-weight: 600; color: var(--text-main);">${s.fname} ${s.lname || ''}</td>
                        <td>${s.dept || '-'}</td>
                        <td>${s.course || '-'}</td>
                        <td style="font-style: italic;">${s.book || '-'}</td>
                        <td><span class="badge ${cleanStatus}">${cleanStatus}</span></td>
                        <td>
                            <div class="actions-cell">
                                <button class="btn-inline-edit" onclick="editStudent(${s.id})">⚙️ Edit</button>
                                <button class="btn-inline-delete" onclick="deleteStudent(${s.id})">🗑️ Delete</button>
                            </div>
                        </td>
                    </tr>
                `;
            }).join('');
        })
        .catch(error => {
            console.error("Critical error reading operational stack:", error);
            document.getElementById('studentBody').innerHTML = '<tr><td colspan="6" style="text-align:center; color: var(--danger);">Failed to stream server logs. Verify local connectivity.</td></tr>';
        });
}

// 3. CREATE/UPDATE: Route form entries to appropriate database contexts
function saveData() {
    const id = document.getElementById('studentId').value;
    
    const studentData = {
        id: id,
        fname: document.getElementById('fname').value,
        lname: document.getElementById('lname').value,
        dob: document.getElementById('dob').value,
        dept: document.getElementById('dept').value,
        course: document.getElementById('course').value,
        grade: document.getElementById('grade').value,
        book: document.getElementById('book').value,
        borrowDate: document.getElementById('borrowDate').value,
        dueDate: document.getElementById('dueDate').value,
        status: document.getElementById('status').value
    };

    // Form confirmation check
    if (!studentData.fname || !studentData.lname) {
        alert("Please complete required Name tracking configurations.");
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
            alert(id ? "Record changes successfully saved." : "New profile added successfully.");
            document.getElementById('studentForm').reset();
            document.getElementById('studentId').value = '';
            showPage('view'); 
        } else {
            alert("Transactional write sequence dropped by server context.");
        }
    })
    .catch(error => console.error("Error writing data matrix back:", error));
}

// 4. EDIT: Request index schema by single target parameter ID
function editStudent(id) {
    fetch(`/get-student?id=${id}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('studentId').value = data.id;
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
        .catch(error => console.error("Could not trace targeted index metadata:", error));
}

// 5. DELETE: Permanently remove the targeted record
function deleteStudent(id) {
    if (confirm("Are you sure you want to delete this student permanently? All linked loan data will cascade clear.")) {
        fetch(`/delete-student?id=${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    fetchStudents(); 
                } else {
                    alert("Unable to process drop request.");
                }
            })
            .catch(error => console.error("Error issuing delete call:", error));
    }
}

// 6. Reset form elements into clear state
function openAddForm() {
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    document.getElementById('formTitle').innerText = "Enroll New Student Profile";
    showPage('form');
}

// 7. Core Lifecycle initialization script
document.addEventListener("DOMContentLoaded", () => {
    showPage('view');
});