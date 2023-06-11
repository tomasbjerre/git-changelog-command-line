#!/bin/bash

set -e

version=${1?param missing - version}

source ~/.bashrc
docker login -u tomasbjerre -p $dockerhub_token

to_file=docker/git-changelog-command-line.jar

echo Downloading $version to $to_file
rm -f $to_file
curl https://repo1.maven.org/maven2/se/bjurr/gitchangelog/git-changelog-command-line/$version/git-changelog-command-line-$version.jar \
 --output $to_file

echo Building Docker image
docker rmi -f tomasbjerre/git-changelog-command-line:$version \
 || echo No previous image existed
docker build \
 -t tomasbjerre/git-changelog-command-line:$version docker

echo Testing docker image
docker run \
 --mount src="$(pwd)",target=/usr/src/git-changelog-command-line,type=bind \
 tomasbjerre/git-changelog-command-line:$version \
 --stdout \
 --template-content "hello world"

echo Push image
docker images
docker push tomasbjerre/git-changelog-command-line:$version

echo Update README.md
docker run --rm -t \
-v $(pwd):/myvol \
-e DOCKER_USER='tomasbjerre' -e DOCKER_PASS=$dockerhub_token \
chko/docker-pushrm:1 \
--file /myvol/README.md \
--short "Changelog and versioning with conventional commits" \
--debug \
tomasbjerre/git-changelog-command-line
