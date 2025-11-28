#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/.."
IMAGE=ghcr.io/${GITHUB_REPOSITORY_OWNER:-${GITHUB_ACTOR}}/connector-java:latest
mvn -U -DskipTests=false clean package
docker build -t ${IMAGE} -f Dockerfile .
if [ -z "$GITHUB_TOKEN" ]; then
  echo "GITHUB_TOKEN not set. Please set the environment var to push to GHCR"
  exit 1
fi
echo "$GITHUB_TOKEN" | docker login ghcr.io -u ${GITHUB_ACTOR} --password-stdin
docker push ${IMAGE}
