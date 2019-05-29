#!/bin/bash
sudo rm -rf jdeploy-bundle
version=`awk 'NF>1{print $NF}' gradle.properties`
echo "Version is: \"$version\""
rm -rf package.json
cp package.json.orig package.json
sed -i "s/VERSION/$version/g" package.json
sudo jdeploy publish
