#!/bin/bash

source ~/.bashrc
source gradle.properties

npx runnable-jar-to-docker@latest \
 --docker-username tomasbjerre \
 --docker-password $dockerhub_token \
 --maven-group $group \
 --maven-artifact ${PWD##*/}  \
 --maven-version $(npx git-changelog-command-line --print-highest-version) \
 --repository-url "file://$HOME/.m2/repository"
