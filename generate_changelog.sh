#/bin/bash
ROOT_FOLDER=`pwd`
cd build/libs

VERSION=1.100.2

java -jar git-changelog-command-line-$VERSION.jar -h

#
#Actual changelog to be used in root of repo
#

java -jar git-changelog-command-line-$VERSION.jar -gapi https://api.github.com/repos/tomasbjerre/git-changelog-command-line/ -gtok $GITHUB_OAUTH2TOKEN -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/CHANGELOG.md

#java -jar git-changelog-command-line-$VERSION.jar -t $ROOT_FOLDER/changelog_mediawiki.mustache -sf $ROOT_FOLDER/changelog.json -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -gapi https://api.github.com/repos/tomasbjerre/git-changelog-lib

#
#test cases
#

## Print to stdout
java -jar git-changelog-command-line-$VERSION.jar -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json --stdout -tc 224cad580426bc03027b77c1036306253cbba973

## Write to file
java -jar git-changelog-command-line-$VERSION.jar -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/src/test/resources/testThatJsonCanBeUsedActual.md -tc 224cad580426bc03027b77c1036306253cbba973

## Write to file
java -jar git-changelog-command-line-$VERSION.jar -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/src/test/resources/testThatCommitsCanBeIgnoredIfNoIssue.md -ini

## Ignore tags example, ignoring all 1.1x
java -jar git-changelog-command-line-$VERSION.jar -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/src/test/resources/testThatTagsCanBeIgnored.md -itp ".*[0-9]{2}$"

## Create MediaWiki page
java -jar git-changelog-command-line-$VERSION.jar -t $ROOT_FOLDER/changelog_mediawiki.mustache -sf $ROOT_FOLDER/changelog.json -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -tc 224cad580426bc03027b77c1036306253cbba973

## Use variables from command line
java -jar git-changelog-command-line-$VERSION.jar -ex "{\"var1\":\"value1\"}" -tec "extended variable: {{extended.var1}}" -of $ROOT_FOLDER/src/test/resources/testThatVariablesCanBeUsed.md

## Use GitLab issues
java -jar git-changelog-command-line-$VERSION.jar -fc 67b9976 -tc e281256 -gls https://gitlab.com/ -glt $GITLAB_TOKEN -glpn violations-test -t $ROOT_FOLDER/changelog.mustache -of $ROOT_FOLDER/src/test/resources/gitLabChangelog.md

## Ignore tags older
java -jar git-changelog-command-line-$VERSION.jar -t $ROOT_FOLDER/changelog.mustache -of $ROOT_FOLDER/src/test/resources/testThatCommitsCanBeIgnoredByTime.md -iot "2017-04-01 00:00:00"
