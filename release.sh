#!/bin/bash
./gradlew release || exit 1

git reset --hard HEAD~1
./publish-npm.sh
git pull

./build.sh
git commit -a --amend --no-edit
git push -f

