# Git Changelog Command Line [![Build Status](https://travis-ci.org/tomasbjerre/git-changelog-command-line.svg?branch=master)](https://travis-ci.org/tomasbjerre/git-changelog-command-line)

This is a command line tool for generating a changelog, or releasenotes, from a GIT repository. It uses the [Git Changelog Lib](https://github.com/tomasbjerre/git-changelog-lib).

There are some screenshots [here](https://github.com/tomasbjerre/git-changelog-lib/tree/screenshots/sandbox).

The runnable can be found in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-lib%22) (the zip file). Unpack it and you will find the bin-folder.

# Usage
Or from command line:
```
-cl, --custom-issue-link <string>          Custom issue link.
                                           <string>: any string
                                           Default: null
-cn, --custom-issue-name <string>          Custom issue name.
                                           <string>: any string
                                           Default: null
-cp, --custom-issue-pattern <string>       Custom issue pattern.
                                           <string>: any string
                                           Default: null
-df, --date-format <string>                Format to use when printing dates.
                                           <string>: any string
                                           Default: YYYY-MM-dd HH:mm:ss
-fc, --from-commit <string>                From commit.
                                           <string>: any string
                                           Default: 0000000000000000000000000000000000000000
-fr, --from-ref <string>                   From ref.
                                           <string>: any string
                                           Default: null
-gapi, --github-api <string>               GitHub API.
                                           <string>: any string
                                           Default: 
-h, --help <argument-to-print-help-for>    <argument-to-print-help-for>: an argument to print help for
                                           Default: If no specific parameter is given the whole usage text is given
-ip, --ignore-pattern <string>             Ignore commits where pattern 
                                           matches message.
                                           <string>: any string
                                           Default: ^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*
-jp, --jira-pattern <string>               Jira issue pattern.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
-jpw, --jira-password <string>             Optional password to authenticate 
                                           with Jira.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
-js, --jiraServer <string>                 Jira server. When a Jira server is 
                                           given, the title of the Jira issues can be 
                                           used in the changelog.
                                           <string>: any string
                                           Default: 
-ju, --jira-username <string>              Optional username to authenticate 
                                           with Jira.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
-mp, --mediawiki-password <string>         Password to authenticate with 
                                           MediaWiki.
                                           <string>: any string
                                           Default: 
-mt, --mediawiki-title <string>            Title of MediaWiki page.
                                           <string>: any string
                                           Default: null
-mu, --mediawiki-user <string>             User to authenticate with MediaWiki.
                                           <string>: any string
                                           Default: 
-murl, --mediawiki-url <string>            Base URL of MediaWiki.
                                           <string>: any string
                                           Default: 
-ni, --no-issue-name <string>              Name of virtual issue that contains 
                                           commits that has no issue associated.
                                           <string>: any string
                                           Default: No issue
-of, --output-file <string>                Write output to file.
                                           <string>: any string
                                           Default: 
-r, --repo <string>                        Repository.
                                           <string>: any string
                                           Default: .
-ri, --remove-issue-from-message           Dont print any issues in the 
                                           messages of commits.
                                           Default: disabled
-rt, --readable-tag-name <string>          Pattern to extract readable part of 
                                           tag.
                                           <string>: any string
                                           Default: /([^/]+?)$
-sf, --settings-file <string>              Use settings from file.
                                           <string>: any string
                                           Default: null
-std, --stdout                             Print builder to <STDOUT>.
                                           Default: disabled
-t, --template <string>                    Template to use. A default template 
                                           will be used if not specified.
                                           <string>: any string
                                           Default: git-changelog-template.mustache
-tc, --to-commit <string>                  To commit.
                                           <string>: any string
                                           Default: null
-tr, --to-ref <string>                     To ref.
                                           <string>: any string
                                           Default: refs/heads/master
-tz, --time-zone <string>                  TimeZone to use when printing dates.
                                           <string>: any string
                                           Default: UTC
-ut, --untagged-name <string>              When listing commits per tag, this 
                                           will by the name of a virtual tag that 
                                           contains commits not available in any git 
                                           tag.
                                           <string>: any string
                                           Default: No tag
```

Creating a MediaWiki page can be done like this.
```
./git-changelog-command-line -murl http://localhost/mediawiki -mu tomas -mp tomaskod -mt "Tomas Title" -t /home/bjerre/workspace/git-changelog-lib/changelog_mediawiki.mustache -ut "Next release"
```

# Developer instructions

To build the code, have a look at `.travis.yml`.

To do a release you need to do `./gradlew release` and release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
