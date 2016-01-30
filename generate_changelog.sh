#/bin/bash
ROOT_FOLDER=`pwd`
cd build/distributions
unzip *T.zip
cd *T

#
#Actual changelog to be used in root of repo
#

./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/CHANGELOG.md

#./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog_mediawiki.mustache -sf $ROOT_FOLDER/changelog.json -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -gapi https://api.github.com/repos/tomasbjerre/git-changelog-lib


#
#test cases
#

## Print to stdout
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json --stdout -tc 224cad580426bc03027b77c1036306253cbba973

## Write to file
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/src/test/resources/testThatJsonCanBeUsedActual.md -tc 224cad580426bc03027b77c1036306253cbba973

## Create MediaWiki page
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog_mediawiki.mustache -sf $ROOT_FOLDER/changelog.json -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -tc 224cad580426bc03027b77c1036306253cbba973
