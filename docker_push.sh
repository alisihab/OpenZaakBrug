#!/bin/sh

# error on unset variables, exit on error
set -eu
set +x

rm -f ./src/main/resources/application.properties
rm -f ./src/main/resources/config.json

mvn clean install -Dmaven.javadoc.skip=true -B -V -DskipTests

# Login to Docker Hub
docker login -u "$DOCKER_USERNAME" --password "$DOCKER_PASSWORD"

# Echo script commands
set -x

git_tag=$(git tag --sort=-creatordate | head -1)

echo "$git_tag"

REPO=sihab/brug
TAG=${git_tag:-latest}

# Build the image
docker build -t $REPO:$TAG .

# Push the image
docker push $REPO:$TAG
