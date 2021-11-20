#!/bin/bash
./gradlew releaseMinor && \
    npm run release
