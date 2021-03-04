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

t_tag=$(git describe)

echo "$t_tag"

REPO=sihab/brug
TAG=${TRAVIS_TAG:-latest}

# Build the image
docker build -t $REPO:$TAG .

# Push the image
docker push $REPO:$TAG
