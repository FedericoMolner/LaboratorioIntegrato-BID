@echo off
REM Build jar and Docker image, push to GHCR using GITHUB_TOKEN env var
cd /d %~dp0\..
mvn -U -DskipTests=false clean package
set IMAGE=ghcr.io/%GITHUB_REPOSITORY_OWNER%/connector-java:latest
docker build -t %IMAGE% -f Dockerfile .
if not defined GITHUB_TOKEN (
  echo GITHUB_TOKEN not set. Please set the environment variable with appropriate token.
  exit /b 1
)

echo Logging to ghcr
docker login ghcr.io -u %GITHUB_ACTOR% -p %GITHUB_TOKEN%
docker push %IMAGE%
