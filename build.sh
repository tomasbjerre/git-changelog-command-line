#!/bin/bash
./gradlew clean build || exit 1
./generate_changelog.sh
