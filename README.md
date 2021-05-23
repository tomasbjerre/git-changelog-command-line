# Git Changelog Command Line
[![Build Status](https://travis-ci.org/tomasbjerre/git-changelog-command-line.svg?branch=master)](https://travis-ci.org/tomasbjerre/git-changelog-command-line)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-command-line/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-command-line)
[![NPM](https://img.shields.io/npm/v/git-changelog-command-line.svg?style=flat-square) ](https://www.npmjs.com/package/git-changelog-command-line)

This is a command line tool for generating a changelog, or releasenotes, from a GIT repository. It uses the [Git Changelog Lib](https://github.com/tomasbjerre/git-changelog-lib).

There are some screenshots [here](https://github.com/tomasbjerre/git-changelog-lib/tree/screenshots/sandbox).

The runnable can be found in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-command-line%22) or [NPM](https://www.npmjs.com/package/git-changelog-command-line).

A changelog can be created like this.
```shell
npx git-changelog-command-line -std -tec "
# Changelog

Changelog for {{ownerName}} {{repoName}}.

{{#tags}}
## {{name}}
 {{#issues}}
  {{#hasIssue}}
   {{#hasLink}}
### {{name}} [{{issue}}]({{link}}) {{title}} {{#hasIssueType}} *{{issueType}}* {{/hasIssueType}} {{#hasLabels}} {{#labels}} *{{.}}* {{/labels}} {{/hasLabels}}
   {{/hasLink}}
   {{^hasLink}}
### {{name}} {{issue}} {{title}} {{#hasIssueType}} *{{issueType}}* {{/hasIssueType}} {{#hasLabels}} {{#labels}} *{{.}}* {{/labels}} {{/hasLabels}}
   {{/hasLink}}
  {{/hasIssue}}
  {{^hasIssue}}
### {{name}}
  {{/hasIssue}}

  {{#commits}}
**{{{messageTitle}}}**

{{#messageBodyItems}}
 * {{.}} 
{{/messageBodyItems}}

[{{hash}}](https://github.com/{{ownerName}}/{{repoName}}/commit/{{hash}}) {{authorName}} *{{commitTime}}*

  {{/commits}}

 {{/issues}}
{{/tags}}
"
```

Or, with the *JAR* from Central, do `java -jar git-changelog-command-line-*.jar ....`

There are more examples [here](https://github.com/tomasbjerre/git-changelog-command-line/blob/master/generate_changelog.sh).

# Usage
Or from command line:
```shell
-cl, --custom-issue-link <string>          Custom issue link. Supports 
                                           variables like ${PATTERN_GROUP_1} to inject 
                                           variables from pattern.
                                           <string>: any string
                                           Default: null
-cn, --custom-issue-name <string>          Custom issue name.
                                           <string>: any string
                                           Default: null
-cp, --custom-issue-pattern <string>       Custom issue pattern.
                                           <string>: any string
                                           Default: null
-ct, --custom-issue-title <string>         Custom issue title. Supports 
                                           variables like ${PATTERN_GROUP_1} to inject 
                                           variables from pattern.
                                           <string>: any string
                                           Default: null
-df, --date-format <string>                Format to use when printing dates.
                                           <string>: any string
                                           Default: YYYY-MM-dd HH:mm:ss
-eh, --extended-headers <string>           Extended headers that will send 
                                           when access JIRA. e.g. -eh CF-Access-
                                           Client-ID:abcde12345xyz.access [Supports Multiple occurrences]
                                           <string>: any string
                                           Default: Empty list
-ex, --extended-variables <string>         Extended variables that will be 
                                           available as {{extended.*}}. -ex "{\"var1\": 
                                           \"val1\"}" will print out "val1" for 
                                           a template like "{{extended.var1}}"
                                           <string>: any string
                                           Default: 
-fc, --from-commit <string>                From commit.
                                           <string>: any string
                                           Default: 0000000000000000000000000000000000000000
-fr, --from-ref <string>                   From ref.
                                           <string>: any string
                                           Default: null
-gapi, --github-api <string>               GitHub API. Like: https://api.
                                           github.com/repos/tomasbjerre/git-changelog-
                                           command-line/
                                           <string>: any string
                                           Default: 
-glpn, --gitlab-project-name <string>      GitLab project name.
                                           <string>: any string
                                           Default: 
-gls, --gitlab-server <string>             GitLab server, like https://gitlab.
                                           com/.
                                           <string>: any string
                                           Default: 
-glt, --gitlab-token <string>              GitLab API token.
                                           <string>: any string
                                           Default: 
-gtok, --github-token <string>             GitHub API OAuth2 token. You can 
                                           get it from: curl -u 'yourgithubuser' -
                                           d '{"note":"Git Changelog Lib"}' 
                                           https://api.github.com/authorizations
                                           <string>: any string
                                           Default: 
-h, --help <argument-to-print-help-for>    <argument-to-print-help-for>: an argument to print help for
                                           Default: If no specific parameter is given the whole usage text is given
-ini, --ignore-commits-without-issue       Ignore commits that is not included 
                                           in any issue.
                                           Default: disabled
-iot, --ignore-older-than <string>         Ignore commits older than YYYY-MM-
                                           dd HH:mm:ss.
                                           <string>: any string
                                           Default: 
-ip, --ignore-pattern <string>             Ignore commits where pattern 
                                           matches message.
                                           <string>: any string
                                           Default: ^\[maven-release-plugin\].*|^\[Gradle Release Plugin\].*|^Merge.*
-itp, --ignore-tag-pattern <string>        Ignore tags that matches regular 
                                           expression. Can be used to ignore release 
                                           candidates and only include actual releases.
                                           <string>: any string
                                           Default: null
-jba, --jira-basic-auth <string>           Optional token to authenticate with 
                                           Jira.
                                           <string>: any string
                                           Default: \b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\b
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
-mavp, --major-version-pattern <string>    Commit messages matching this 
                                           regular expression will trigger new major 
                                           version.
                                           <string>: any string
                                           Default: null
-mivp, --minor-version-pattern <string>    Commit messages matching this 
                                           regular expression will trigger new minor 
                                           version.
                                           <string>: any string
                                           Default: null
-ni, --no-issue-name <string>              Name of virtual issue that contains 
                                           commits that has no issue associated.
                                           <string>: any string
                                           Default: No issue
-of, --output-file <string>                Write output to file.
                                           <string>: any string
                                           Default: 
-phv, --print-highest-version              Print the highest version, 
                                           determined by tags in repo, and exit.
                                           Default: disabled
-phvt, --print-highest-version-tag         Print the tag corresponding to 
                                           highest version, and exit.
                                           Default: disabled
-pnv, --print-next-version                 Print the next version, determined 
                                           by commits since highest version, and 
                                           exit.
                                           Default: disabled
-ptf, --prepend-to-file <string>           Add the changelog to top of given 
                                           file.
                                           <string>: any string
                                           Default: null
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
-tec, --template-content <string>          String to use as template.
                                           <string>: any string
                                           Default: 
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

Checkout the [Git Changelog Lib](https://github.com/tomasbjerre/git-changelog-lib) for more documentation.

# Developer instructions

To build the code, have a look at `.travis.yml`.

To do a release you need to do `./gradlew release` and release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
