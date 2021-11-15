# Git Changelog Command Line

[![Build Status](https://travis-ci.org/tomasbjerre/git-changelog-command-line.svg?branch=master)](https://travis-ci.org/tomasbjerre/git-changelog-command-line)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-command-line/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-command-line)
[![NPM](https://img.shields.io/npm/v/git-changelog-command-line.svg?style=flat-square)](https://www.npmjs.com/package/git-changelog-command-line)

This is a command line tool for generating a changelog, or releasenotes, from a GIT repository. It uses the [Git Changelog Lib](https://github.com/tomasbjerre/git-changelog-lib).

This is a Java application (runnable `jar`) packaged into an `NPM` package for convenience. The runnable `jar` can be found in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-command-line%22) and used like `java -jar git-changelog-command-line-*.jar ....`. The `npm` package can be found in [NPM](https://www.npmjs.com/package/git-changelog-command-line).

## Example - Simple

A changelog can be created like this.

```shell
npx git-changelog-command-line -std -tec "
# Changelog

Changelog.

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

## Example - Semantic versioning from conventional commits

If you are using [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/):

```shell
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

The highest version can be determined with:

```shell

highestTag=$(npx git-changelog-command-line \
 --print-highest-version-tag)

echo Last release detected as $highestTag
```

Next version to release can be determined with:

```shell
nextVersion=$(npx git-changelog-command-line \
 --print-next-version)

echo Next release based on commits is: $nextVersion
```

A changelog can be rendered (using [Helpers](https://github.com/tomasbjerre/git-changelog-lib#Helpers)) like this:

```shell
npx git-changelog-command-line \
 --to-ref HEAD \
 --stdout \
 --template-content "
# Changelog

{{#tags}}
{{#ifReleaseTag .}}
## [{{name}}](https://gitlab.com/html-validate/html-validate/compare/{{name}}) ({{tagDate .}})

  {{#ifContainsType commits type='feat'}}
### Features

    {{#commits}}
      {{#ifCommitType . type='feat'}}
 - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://gitlab.com/html-validate/html-validate/commit/{{hashFull}}))
      {{/ifCommitType}}
    {{/commits}}
  {{/ifContainsType}}

  {{#ifContainsType commits type='fix'}}
### Bug Fixes

    {{#commits}}
      {{#ifCommitType . type='fix'}}
 - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://gitlab.com/html-validate/html-validate/commit/{{hashFull}}))
      {{/ifCommitType}}
    {{/commits}}
  {{/ifContainsType}}

{{/ifReleaseTag}}
{{/tags}}
"
```

## Example - custom helpers

You can supply your own helpers and use them in the template.

```shell
npx git-changelog-command-line \
 --to-ref HEAD \
 --stdout \
 --template-content "
{{#commits}}
  {{#startsWith messageTitle s='feat'}}
    Starts with feat: "{{messageTitle}}"
    first 10 letters of hash is: {{firstLetters hash number='10'}}
  {{/startsWith}}
{{/commits}}
" \
--register-handlebars-helper "
Handlebars.registerHelper('firstLetters', function(input, options) {
  const number = parseInt(options.hash['number'] || "0")
  return input.substring(0,number);
});

Handlebars.registerHelper('startsWith', function(from, options) {
  const s = options.hash['s']
  if (new RegExp('^' + s + '.*').test(from)) {
    return options.fn(this);
  } else {
    return options.inverse(this);
  }
});
"
```

# Usage

Or from command line:

```shell
-cl, --custom-issue-link <string>              Custom issue link. Supports 
                                               variables like ${PATTERN_GROUP_1} to 
                                               inject variables from pattern.
                                               <string>: any string
                                               Default: null
-cn, --custom-issue-name <string>              Custom issue name.
                                               <string>: any string
                                               Default: null
-cp, --custom-issue-pattern <string>           Custom issue pattern.
                                               <string>: any string
                                               Default: null
-ct, --custom-issue-title <string>             Custom issue title. Supports 
                                               variables like ${PATTERN_GROUP_1} to 
                                               inject variables from pattern.
                                               <string>: any string
                                               Default: null
-df, --date-format <string>                    Format to use when printing 
                                               dates.
                                               <string>: any string
                                               Default: YYYY-MM-dd HH:mm:ss
-eh, --extended-headers <string>               Extended headers that will send 
                                               when access JIRA. e.g. -eh CF-Access-
                                               Client-ID:abcde12345xyz.access [Supports Multiple occurrences]
                                               <string>: any string
                                               Default: Empty list
-ex, --extended-variables <string>             Extended variables that will be 
                                               available as {{extended.*}}. -ex "{\"
                                               var1\": \"val1\"}" will print out 
                                               "val1" for a template like "{{extended.
                                               var1}}"
                                               <string>: any string
                                               Default: 
-fc, --from-commit <string>                    From commit.
                                               <string>: any string
                                               Default: 0000000000000000000000000000000000000000
-fr, --from-ref <string>                       From ref.
                                               <string>: any string
                                               Default: null
-gapi, --github-api <string>                   GitHub API. Like: https://api.
                                               github.com/repos/tomasbjerre/git-
                                               changelog-command-line/
                                               <string>: any string
                                               Default: 
-glpn, --gitlab-project-name <string>          GitLab project name.
                                               <string>: any string
                                               Default: 
-gls, --gitlab-server <string>                 GitLab server, like https:
                                               //gitlab.com/.
                                               <string>: any string
                                               Default: 
-glt, --gitlab-token <string>                  GitLab API token.
                                               <string>: any string
                                               Default: 
-gtok, --github-token <string>                 GitHub API OAuth2 token. You 
                                               can get it from: curl -u 
                                               'yourgithubuser' -d '{"note":"Git Changelog 
                                               Lib"}' https://api.github.
                                               com/authorizations
                                               <string>: any string
                                               Default: 
-h, --help <argument-to-print-help-for>        <argument-to-print-help-for>: an argument to print help for
                                               Default: If no specific parameter is given the whole usage text is given
-ini, --ignore-commits-without-issue           Ignore commits that is not 
                                               included in any issue.
                                               Default: disabled
-iot, --ignore-older-than <string>             Ignore commits older than YYYY-
                                               MM-dd HH:mm:ss.
                                               <string>: any string
                                               Default: 
-ip, --ignore-pattern <string>                 Ignore commits where pattern 
                                               matches message.
                                               <string>: any string
                                               Default: ^Merge.*
-itp, --ignore-tag-pattern <string>            Ignore tags that matches 
                                               regular expression. Can be used to 
                                               ignore release candidates and only 
                                               include actual releases.
                                               <string>: any string
                                               Default: null
-jba, --jira-basic-auth <string>               Optional token to authenticate 
                                               with Jira.
                                               <string>: any string
                                               Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-jp, --jira-pattern <string>                   Jira issue pattern.
                                               <string>: any string
                                               Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-jpw, --jira-password <string>                 Optional password to 
                                               authenticate with Jira.
                                               <string>: any string
                                               Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-js, --jiraServer <string>                     Jira server. When a Jira server 
                                               is given, the title of the Jira 
                                               issues can be used in the changelog.
                                               <string>: any string
                                               Default: null
-ju, --jira-username <string>                  Optional username to 
                                               authenticate with Jira.
                                               <string>: any string
                                               Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-mavp, --major-version-pattern <string>        Commit messages matching this, 
                                               optional, regular expression will trigger 
                                               new major version.
                                               <string>: any string
                                               Default: null
-mivp, --minor-version-pattern <string>        Commit messages matching this, 
                                               optional, regular expression will trigger 
                                               new minor version.
                                               <string>: any string
                                               Default: ^[Ff]eat.*
-ni, --no-issue-name <string>                  Name of virtual issue that 
                                               contains commits that has no issue 
                                               associated.
                                               <string>: any string
                                               Default: No issue
-of, --output-file <string>                    Write output to file.
                                               <string>: any string
                                               Default: 
-phv, --print-highest-version                  Print the highest version, 
                                               determined by tags in repo, and exit.
                                               Default: disabled
-phvt, --print-highest-version-tag             Print the tag corresponding to 
                                               highest version, and exit.
                                               Default: disabled
-pnv, --print-next-version                     Print the next version, 
                                               determined by commits since highest 
                                               version, and exit.
                                               Default: disabled
-ptf, --prepend-to-file <string>               Add the changelog to top of 
                                               given file.
                                               <string>: any string
                                               Default: null
-r, --repo <string>                            Repository.
                                               <string>: any string
                                               Default: .
-rhh, --register-handlebars-helper <string>    Handlebar helpers, https:
                                               //handlebarsjs.com/guide/block-helpers.html, 
                                               to register and use in given 
                                               template.
                                               <string>: any string
                                               Default: 
-ri, --remove-issue-from-message               Dont print any issues in the 
                                               messages of commits.
                                               Default: disabled
-rmp, --redmine-pattern <string>               Redmine issue pattern.
                                               <string>: any string
                                               Default: #([0-9]+)
-rmpw, --redmine-password <string>             Optional password to 
                                               authenticate with Redmine.
                                               <string>: any string
                                               Default: 
-rms, --redmine-server <string>                Redmine server. When a Redmine 
                                               server is given, the title of the 
                                               Redmine issues can be used in the 
                                               changelog.
                                               <string>: any string
                                               Default: null
-rmt, --redmine-token <string>                 Optional token/api-key to 
                                               authenticate with Redmine.
                                               <string>: any string
                                               Default: 
-rmu, --redmine-username <string>              Optional username to 
                                               authenticate with Redmine.
                                               <string>: any string
                                               Default: 
-rt, --readable-tag-name <string>              Pattern to extract readable 
                                               part of tag.
                                               <string>: any string
                                               Default: /([^/]+?)$
-sf, --settings-file <string>                  Use settings from file.
                                               <string>: any string
                                               Default: null
--show-debug-info                              Please run your command with 
                                               this parameter and supply output 
                                               when reporting bugs.
                                               Default: disabled
-std, --stdout                                 Print builder to <STDOUT>.
                                               Default: disabled
-t, --template <string>                        Template to use. A default 
                                               template will be used if not specified.
                                               <string>: any string
                                               Default: changelog.mustache
-tc, --to-commit <string>                      To commit.
                                               <string>: any string
                                               Default: null
-tec, --template-content <string>              String to use as template.
                                               <string>: any string
                                               Default: 
-tr, --to-ref <string>                         To ref.
                                               <string>: any string
                                               Default: refs/heads/master
-tz, --time-zone <string>                      TimeZone to use when printing 
                                               dates.
                                               <string>: any string
                                               Default: UTC
-ut, --untagged-name <string>                  When listing commits per tag, 
                                               this will by the name of a virtual 
                                               tag that contains commits not 
                                               available in any git tag.
                                               <string>: any string
                                               Default: No tag
```

Checkout the [Git Changelog Lib](https://github.com/tomasbjerre/git-changelog-lib) for more documentation.
