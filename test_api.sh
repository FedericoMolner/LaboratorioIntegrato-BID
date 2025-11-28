#!/bin/bash
# Script di test per gli endpoint REST

BASE_URL="http://localhost:8080"

echo "===================================="
echo "TEST STUDENT API"
echo "===================================="

# Test 1: GET all students (list vuota)
echo -e "\n1. GET all students (inizialmente vuoto)"
curl -X GET "$BASE_URL/students" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 2: POST - Create a new student
echo -e "\n\n2. POST - Create student 1"
curl -X POST "$BASE_URL/students" \
  -H "Content-Type: application/json" \
  -d '{"name":"Marco","surname":"Rossi","email":"marco.rossi@its.it"}' \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 3: POST - Create another student
echo -e "\n\n3. POST - Create student 2"
curl -X POST "$BASE_URL/students" \
  -H "Content-Type: application/json" \
  -d '{"name":"Giulia","surname":"Bianchi","email":"giulia.bianchi@its.it"}' \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 4: GET all students
echo -e "\n\n4. GET all students (dopo inserimento)"
curl -X GET "$BASE_URL/students" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 5: GET student by ID
echo -e "\n\n5. GET student by ID (ID=1)"
curl -X GET "$BASE_URL/students/1" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 6: PUT - Update student
echo -e "\n\n6. PUT - Update student 1"
curl -X PUT "$BASE_URL/students/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Marco","surname":"Rossi UPDATED","email":"marco.updated@its.it"}' \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 7: GET updated student
echo -e "\n\n7. GET updated student"
curl -X GET "$BASE_URL/students/1" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 8: DELETE student
echo -e "\n\n8. DELETE student 2"
curl -X DELETE "$BASE_URL/students/2" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 9: GET all students (dopo delete)
echo -e "\n\n9. GET all students (dopo cancellazione)"
curl -X GET "$BASE_URL/students" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

# Test 10: GET non-existent student (404)
echo -e "\n\n10. GET non-existent student (should be 404)"
curl -X GET "$BASE_URL/students/999" \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n" 2>/dev/null

echo -e "\n\n===================================="
echo "TEST COMPLETATO"
echo "===================================="
