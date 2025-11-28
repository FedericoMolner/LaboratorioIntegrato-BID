#!/usr/bin/env bash
# Build and run the connector locally with Docker
set -e
cd "$(dirname "$0")/.."
# Build jar
mvn -U -DskipTests clean package
# Build image
docker build -t connector-java:local .
# Ensure docker.env exists
if [ ! -f docker.env ]; then
  cp docker.env.example docker.env
fi
# Run
docker run --rm -p 8080:8080 --env-file docker.env connector-java:local
