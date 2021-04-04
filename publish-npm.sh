#!/bin/bash
sudo rm -rf jdeploy-bundle
version=`cat gradle.properties | grep version | cut -d'=' -f2`
echo "Version is: \"$version\""
rm -rf package.json
cp package.json.orig package.json
sed -i "s/VERSION/$version/g" package.json
sudo jdeploy publish
