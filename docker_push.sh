#!/bin/sh

# error on unset variables, exit on error
set -eu
set +x

rm -f ./src/main/resources/application.properties
rm -f ./src/main/resources/config.json

mvn install -Dmaven.javadoc.skip=true -B -V -DskipTests

# Echo script commands
set -x

if [[ ${BRANCH_NAME} == release-* ]]; then
	version = $(echo $BRANCH_NAME| cut -d'-' -f 2)
	if [[ ${EVENT_TYPE} == push ]]; then
		version = "${version}-latest"
	fi
fi

REPO=sihab/brug
TAG=${version:-latest}

# Build the image
docker build -t $REPO:$TAG .

# Push the image
docker push $REPO:$TAG
