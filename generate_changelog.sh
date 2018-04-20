#/bin/bash
ROOT_FOLDER=`pwd`
cd build/distributions
rm *shadow*
unzip *T.zip
cd *T

./bin/git-changelog-command-line -h

#
#Actual changelog to be used in root of repo
#

./bin/git-changelog-command-line -gapi https://api.github.com/repos/tomasbjerre/git-changelog-command-line/ -gtok $GITHUB_OAUTH2TOKEN -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/CHANGELOG.md

#./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog_mediawiki.mustache -sf $ROOT_FOLDER/changelog.json -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -gapi https://api.github.com/repos/tomasbjerre/git-changelog-lib

#
#test cases
#

## Print to stdout
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json --stdout -tc 224cad580426bc03027b77c1036306253cbba973

## Write to file
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/src/test/resources/testThatJsonCanBeUsedActual.md -tc 224cad580426bc03027b77c1036306253cbba973

## Write to file
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/src/test/resources/testThatCommitsCanBeIgnoredIfNoIssue.md -ini

## Ignore tags example, ignoring all 1.1x
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -sf $ROOT_FOLDER/changelog.json -of $ROOT_FOLDER/src/test/resources/testThatTagsCanBeIgnored.md -itp ".*[0-9]{2}$"

## Create MediaWiki page
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog_mediawiki.mustache -sf $ROOT_FOLDER/changelog.json -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -tc 224cad580426bc03027b77c1036306253cbba973

## Use variables from command line
./bin/git-changelog-command-line -ex "{\"var1\":\"value1\"}" -tec "extended variable: {{extended.var1}}" -of $ROOT_FOLDER/src/test/resources/testThatVariablesCanBeUsed.md

## Use GitLab issues
./bin/git-changelog-command-line -fc 67b9976 -tc e281256 -gls https://gitlab.com/ -glt $GITLAB_TOKEN -glpn violations-test -t $ROOT_FOLDER/changelog.mustache -of $ROOT_FOLDER/src/test/resources/gitLabChangelog.md

## Ignore tags older
./bin/git-changelog-command-line -t $ROOT_FOLDER/changelog.mustache -of $ROOT_FOLDER/src/test/resources/testThatCommitsCanBeIgnoredByTime.md -iot "2017-04-01 00:00:00"
