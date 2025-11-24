document.addEventListener('DOMContentLoaded', () => {
    const studentForm = document.getElementById('studentForm');
    const studentId = document.getElementById('studentId');
    const nameInput = document.getElementById('name');
    const surnameInput = document.getElementById('surname');
    const emailInput = document.getElementById('email');
    const studentTableBody = document.getElementById('studentTableBody');
    const clearFormButton = document.getElementById('clearForm');

    const API_URL = '/api/students';

    // Function to fetch and display students
    async function fetchStudents() {
        const response = await fetch(API_URL);
        const students = await response.json();
        studentTableBody.innerHTML = '';
        students.forEach(student => {
            const row = studentTableBody.insertRow();
            row.innerHTML = `
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.surname}</td>
                <td>${student.email}</td>
                <td>
                    <button onclick="editStudent(${student.id}, '${student.name}', '${student.surname}', '${student.email}')">Edit</button>
                    <button class="delete" onclick="deleteStudent(${student.id})">Delete</button>
                </td>
            `;
        });
    }

    // Function to add or update a student
    studentForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const student = {
            name: nameInput.value,
            surname: surnameInput.value,
            email: emailInput.value
        };

        if (studentId.value) {
            // Update existing student
            await fetch(`${API_URL}/${studentId.value}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(student)
            });
        } else {
            // Add new student
            await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(student)
            });
        }
        clearForm();
        fetchStudents();
    });

    // Function to clear the form
    clearFormButton.addEventListener('click', clearForm);

    function clearForm() {
        studentId.value = '';
        nameInput.value = '';
        surnameInput.value = '';
        emailInput.value = '';
    }

    // Function to edit a student (populates the form)
    window.editStudent = (id, name, surname, email) => {
        studentId.value = id;
        nameInput.value = name;
        surnameInput.value = surname;
        emailInput.value = email;
    };

    // Function to delete a student
    window.deleteStudent = async (id) => {
        await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });
        fetchStudents();
    };

    // Initial fetch of students when the page loads
    fetchStudents();
});