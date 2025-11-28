@echo off
setlocal enabledelayedexpansion

cd /d "c:\Users\federico.molner\Desktop\LaboratorioIntegrato\LaboratorioIntegrato-BID\Connector-java\Connector-java"

echo Starting Spring Boot Server...
echo Port: 8080
echo.

:: Avvia Maven Spring Boot in background senza interazioni
start /B /WAIT cmd /C "mvn -q spring-boot:run 2>&1"

pause
