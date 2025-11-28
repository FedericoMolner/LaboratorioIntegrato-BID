@echo off
REM Package the JAR and README into a ZIP for SharePoint
cd /d %~dp0\..
REM Ensure the target JAR exists
if not exist target\connector-java-0.0.1-SNAPSHOT.jar (
  echo JAR not found in target\. Please run: mvn -U -DskipTests clean package
  pause
  exit /b 1
)
REM Ensure README exists
if not exist deploy\share_to_sharepoint\README_FOR_SHAREPOINT.txt (
  echo README not found at deploy\share_to_sharepoint\README_FOR_SHAREPOINT.txt
  pause
  exit /b 1
)
set ZIPNAME=connector-java-shareable.zip

REM If the jar is in use (Docker or Java), we copy it to a temporary file and archive the copy to avoid file lock errors
set TMPJAR=%TEMP%\connector-java-temp.jar
echo Copying JAR to temporary file: %TMPJAR%
copy /Y target\connector-java-0.0.1-SNAPSHOT.jar %TMPJAR% >nul

powershell -Command "Compress-Archive -Path '%TMPJAR%','deploy\share_to_sharepoint\README_FOR_SHAREPOINT.txt' -DestinationPath '%ZIPNAME%' -Force"
if %ERRORLEVEL% NEQ 0 (
  echo Failed to create %ZIPNAME%
  del /F /Q "%TMPJAR%" >nul 2>&1
  exit /b 1
)
del /F /Q "%TMPJAR%" >nul

echo Created %ZIPNAME% in %CD%\%ZIPNAME%
echo Upload this ZIP to SharePoint and provide the link to your team.
pause
