#!/bin/bash
./gradlew release || exit 1
./build.sh
git commit -a --amend --no-edit
git push -f

git checkout HEAD~1
jdeploy init
jdeploy publish
git pull
