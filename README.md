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

```

Checkout the [Git Changelog Lib](https://github.com/tomasbjerre/git-changelog-lib) for more documentation.

# Developer instructions

To build the code, have a look at `.travis.yml`.

To do a release you need to do `./gradlew release` and release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
