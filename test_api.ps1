# Script di test per gli endpoint REST

$BaseURL = "http://localhost:8080"

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "TEST STUDENT API" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan

# Test 1: GET all students (list vuota)
Write-Host "`n1. GET all students (inizialmente vuoto)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students" -Method GET -ContentType "application/json"
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: POST - Create a new student
Write-Host "`n2. POST - Create student 1" -ForegroundColor Yellow
$body1 = @{
    name    = "Marco"
    surname = "Rossi"
    email   = "marco.rossi@its.it"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students" -Method POST -ContentType "application/json" -Body $body1
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: POST - Create another student
Write-Host "`n3. POST - Create student 2" -ForegroundColor Yellow
$body2 = @{
    name    = "Giulia"
    surname = "Bianchi"
    email   = "giulia.bianchi@its.it"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students" -Method POST -ContentType "application/json" -Body $body2
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: GET all students
Write-Host "`n4. GET all students (dopo inserimento)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students" -Method GET -ContentType "application/json"
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: GET student by ID
Write-Host "`n5. GET student by ID (ID=1)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students/1" -Method GET -ContentType "application/json"
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: PUT - Update student
Write-Host "`n6. PUT - Update student 1" -ForegroundColor Yellow
$bodyUpdate = @{
    name    = "Marco"
    surname = "Rossi UPDATED"
    email   = "marco.updated@its.it"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students/1" -Method PUT -ContentType "application/json" -Body $bodyUpdate
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: GET updated student
Write-Host "`n7. GET updated student" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students/1" -Method GET -ContentType "application/json"
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 8: DELETE student
Write-Host "`n8. DELETE student 2" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students/2" -Method DELETE -ContentType "application/json"
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 9: GET all students (dopo delete)
Write-Host "`n9. GET all students (dopo cancellazione)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students" -Method GET -ContentType "application/json"
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 10: GET non-existent student (404)
Write-Host "`n10. GET non-existent student (should be 404)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseURL/students/999" -Method GET -ContentType "application/json"
    Write-Host "Response: " -NoNewline
    Write-Host $response.Content -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Error (Expected): $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
}

Write-Host "`n====================================" -ForegroundColor Cyan
Write-Host "TEST COMPLETATO" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
