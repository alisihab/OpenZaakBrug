#!/bin/sh

# error on unset variables, exit on error
set -eu
set +x

git_hash=${TRAVIS_COMMIT:-`git rev-parse HEAD`}

rm -f ./src/main/resources/application.properties
rm -f ./src/main/resources/config.json

# Login to Docker Hub
docker login -u "$DOCKER_USERNAME" --password "$DOCKER_PASSWORD"

# Echo script commands
set -x

REPO=sihab/brug
TAG=${1:-latest}

# Build the image
docker build \
    -t $REPO:$TAG \
    --build-arg COMMIT_HASH=$git_hash \
    --build-arg RELEASE=$TAG \
    .

# Push the image
docker push $REPO:$TAG