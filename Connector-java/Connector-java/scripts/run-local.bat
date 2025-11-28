@echo off
REM Build the jar and run via docker locally (Windows)
cd /d %~dp0\..
REM Build the jar
mvn -U -DskipTests clean package
REM Build the Docker image
docker build -t connector-java:local .
REM Copy docker.env.example to docker.env if not present
if not exist docker.env copy docker.env.example docker.env
REM Run
docker run --rm -p 8080:8080 --env-file docker.env connector-java:local
