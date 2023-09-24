# Git Changelog Command Line

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-command-line/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.gitchangelog/git-changelog-command-line)
[![NPM](https://img.shields.io/npm/v/git-changelog-command-line.svg?style=flat-square)](https://www.npmjs.com/package/git-changelog-command-line)
[![NPM Downloads](https://img.shields.io/npm/dm/git-changelog-command-line.svg?style=flat)](https://www.npmjs.com/package/git-changelog-command-line)
[![Docker Pulls](https://badgen.net/docker/pulls/tomasbjerre/git-changelog-command-line?icon=docker&label=pulls)](https://hub.docker.com/r/tomasbjerre/git-changelog-command-line/)

This is a command line tool for generating a changelog, or releasenotes, from a GIT repository. It uses the [Git Changelog Lib](https://github.com/tomasbjerre/git-changelog-lib).

This is a Java application (runnable `jar`) packaged into an `NPM` package for convenience.

- The runnable `jar` can be found in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22git-changelog-command-line%22) and used like `java -jar git-changelog-command-line-*.jar ....`.
- The `npm` package can be found in [NPM](https://www.npmjs.com/package/git-changelog-command-line).
- The `Docker` image can be found in [Dockerhub](https://hub.docker.com/r/tomasbjerre/git-changelog-command-line) and used like `docker run --mount src="$(pwd)",target=/usr/src/git-changelog-command-line,type=bind tomasbjerre/git-changelog-command-line:X --stdout`.

## Example - Simple

A changelog can be created like this.

```shell
npx git-changelog-command-line -std
```

Or, you can specify a template:

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

You can also get the tag with `--print-highest-version-tag`. Highest version may be `1.2.3` and highest tag may be `v1.2.3`.

Next version to release can be determined with:

```shell
nextVersion=$(npx git-changelog-command-line \
 --print-next-version)

echo Next release based on commits is: $nextVersion
```

There are default patterns, but you can specify the patterns with:

- `--major-version-pattern REGEXP`
- `--minor-version-pattern REGEXP`
- `--patch-version-pattern REGEXP`

By default it will match anything as patch, like `chore: whatever` and not only `fix: whatever`. You can change that with:

```shell
highestTag=$(npx git-changelog-command-line \
 --print-highest-version-tag)

nextVersion=$(npx git-changelog-command-line \
 --patch-version-pattern "fix:.*" \
 --print-next-version)

if [ "$nextVersion" == "$highestTag" ]; then
    echo "No changes made that can be released"
else
    echo "Changes detected and a new $nextVersion release can be made"
fi
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
## [{{name}}](https://github.com/tomasbjerre/someproject/compare/{{name}}) ({{tagDate .}})

  {{#ifContainsType commits type='feat'}}
### Features

    {{#commits}}
      {{#ifCommitType . type='feat'}}
 - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://github.com/tomasbjerre/someproject/commit/{{hashFull}}))
      {{/ifCommitType}}
    {{/commits}}
  {{/ifContainsType}}

  {{#ifContainsType commits type='fix'}}
### Bug Fixes

    {{#commits}}
      {{#ifCommitType . type='fix'}}
 - {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://github.com/tomasbjerre/someproject/commit/{{hashFull}}))
      {{/ifCommitType}}
    {{/commits}}
  {{/ifContainsType}}

{{/ifReleaseTag}}
{{/tags}}
"
```

Or you can prepend to the current changelog. You may get `$nextVersion` from `--print-next-version` and `$highestTag` from `--print-highest-version-tag`. Somehting like this:

```shell
npx git-changelog-command-line \
 --from-ref $highestTag \
 --to-ref HEAD \
 --prepend-to-file CHANGELOG.md \
 --template-content "
## $nextVersion

{{#ifContainsType commits type='feat'}}
## Features
  {{#commits}}
    {{#ifCommitType . type='feat'}}
  {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://github.com/tomasbjerre/someproject/commit/{{hashFull}}))
    {{/ifCommitType}}
  {{/commits}}
{{/ifContainsType}}

{{#ifContainsType commits type='fix'}}
## Bug Fixes
  {{#commits}}
    {{#ifCommitType . type='fix'}}
  {{#eachCommitScope .}} **{{.}}** {{/eachCommitScope}} {{{commitDescription .}}} ([{{hash}}](https://github.com/tomasbjerre/someproject/commit/{{hashFull}}))
    {{/ifCommitType}}
  {{/commits}}
{{/ifContainsType}}
"
```

### Example NPM and `package.json`

If you are using NPM, you may want to add this to your `package.json`:

```json
{
  "scripts": {
    "build": "echo build it...",
    "release": "npm run set-version; npm run build && npm publish && npm run changelog",
    "set-version": "npm version $(npx git-changelog-command-line --print-next-version)",
    "changelog": "npx git-changelog-command-line -of CHANGELOG.md && git commit -a -m 'chore: changelog' && git push --follow-tags"
  }
}
```

And if you do `npm run release` it will:

- Set version in `package.json`
- Build the repo
- Publish it
- Update CHANGELOG.md

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
-cl, --custom-issue-link <string>                       Custom issue link. 
                                                        Supports variables like 
                                                        ${PATTERN_GROUP_1} to inject variables 
                                                        from pattern.
                                                        <string>: any string
                                                        Default: null
-cn, --custom-issue-name <string>                       Custom issue name.
                                                        <string>: any string
                                                        Default: null
-cp, --custom-issue-pattern <string>                    Custom issue pattern.
                                                        <string>: any string
                                                        Default: null
-ct, --custom-issue-title <string>                      Custom issue title. 
                                                        Supports variables like 
                                                        ${PATTERN_GROUP_1} to inject variables 
                                                        from pattern.
                                                        <string>: any string
                                                        Default: null
-df, --date-format <string>                             Format to use when 
                                                        printing dates.
                                                        <string>: any string
                                                        Default: YYYY-MM-dd HH:mm:ss
-eh, --extended-headers <string>                        Extended headers that 
                                                        will send when access JIRA. 
                                                        e.g. -eh CF-Access-
                                                        Client-ID:abcde12345xyz.access [Supports Multiple occurrences]
                                                        <string>: any string
                                                        Default: Empty list
-en, --encoding <string>                                Encoding to use when 
                                                        writing content.
                                                        <string>: any string
                                                        Default: UTF-8
-ex, --extended-variables <string>                      Extended variables 
                                                        that will be available as 
                                                        {{extended.*}}. -ex "{\"var1\": \"
                                                        val1\"}" will print out "val1" 
                                                        for a template like 
                                                        "{{extended.var1}}"
                                                        <string>: any string
                                                        Default: 
-fre, --from-revision <string>                          From revision.
                                                        <string>: any string
                                                        Default: 0000000000000000000000000000000000000000
-frei, --from-revision-inclusiveness                    Include, or exclude, 
<InclusivenessStrategy>                                 specified revision.
                                                        <InclusivenessStrategy>: {INCLUSIVE | EXCLUSIVE | DEFAULT}
                                                        Default: DEFAULT
-gapi, --github-api <string>                            GitHub API. Like: 
                                                        https://api.github.
                                                        com/repos/tomasbjerre/git-changelog-command-line/
                                                        <string>: any string
                                                        Default: 
-ge, --github-enabled                                   Enable parsing for 
                                                        GitHub issues.
                                                        Default: disabled
-gl, --gitlab-enabled                                   Enable parsing for 
                                                        GitLab issues.
                                                        Default: disabled
-glpn, --gitlab-project-name <string>                   GitLab project name.
                                                        <string>: any string
                                                        Default: 
-gls, --gitlab-server <string>                          GitLab server, like 
                                                        https://gitlab.com/.
                                                        <string>: any string
                                                        Default: 
-glt, --gitlab-token <string>                           GitLab API token.
                                                        <string>: any string
                                                        Default: 
-gtok, --github-token <string>                          GitHub API OAuth2 
                                                        token. You can get it from: 
                                                        curl -u 'yourgithubuser' -d 
                                                        '{"note":"Git Changelog Lib"}' 
                                                        https://api.github.
                                                        com/authorizations
                                                        <string>: any string
                                                        Default: 
-h, --help <argument-to-print-help-for>                 <argument-to-print-help-for>: an argument to print help for
                                                        Default: If no specific parameter is given the whole usage text is given
-handlebars-helper-file, -hhf <path>                    Can be used to add 
                                                        extra helpers.
                                                        <path>: a file path
                                                        Default: /home/bjerre/workspace/git-changelog/git-changelog-command-line/.
-ini, --ignore-commits-without-issue                    Ignore commits that is 
                                                        not included in any issue.
                                                        Default: disabled
-iot, --ignore-older-than <string>                      Ignore commits older 
                                                        than yyyy-MM-dd HH:mm:ss.
                                                        <string>: any string
                                                        Default: 
-ip, --ignore-pattern <string>                          Ignore commits where 
                                                        pattern matches message.
                                                        <string>: any string
                                                        Default: ^Merge.*
-itp, --ignore-tag-pattern <string>                     Ignore tags that 
                                                        matches regular expression. 
                                                        Can be used to ignore 
                                                        release candidates and only 
                                                        include actual releases.
                                                        <string>: any string
                                                        Default: null
-jaf, --jira-additional-field <string>                  Adds an additional 
                                                        field for Jira. When 
                                                        configured, we will return from 
                                                        Jira the result of this 
                                                        field, if it exists. [Supports Multiple occurrences]
                                                        <string>: any string
                                                        Default: Empty list
-jba, --jira-basic-auth <string>                        Optional token to 
                                                        authenticate with Jira.
                                                        <string>: any string
                                                        Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-jbt, --jira-bearer <string>                            Optional token to 
                                                        authenticate with Jira.
                                                        <string>: any string
                                                        Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-je, --jira-enabled                                     Enable parsing for 
                                                        Jira issues.
                                                        Default: disabled
-jp, --jira-pattern <string>                            Jira issue pattern.
                                                        <string>: any string
                                                        Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-jpw, --jira-password <string>                          Optional password to 
                                                        authenticate with Jira.
                                                        <string>: any string
                                                        Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-js, --jiraServer, --jira-server <string>               Jira server. When a 
                                                        Jira server is given, the 
                                                        title of the Jira issues can 
                                                        be used in the changelog.
                                                        <string>: any string
                                                        Default: null
-ju, --jira-username <string>                           Optional username to 
                                                        authenticate with Jira.
                                                        <string>: any string
                                                        Default: \\b[a-zA-Z]([a-zA-Z]+)-([0-9]+)\\b
-mavp, --major-version-pattern <string>                 Commit messages 
                                                        matching this, optional, 
                                                        regular expression will 
                                                        trigger new major version.
                                                        <string>: any string
                                                        Default: null
-mivp, --minor-version-pattern <string>                 Commit messages 
                                                        matching this, optional, 
                                                        regular expression will 
                                                        trigger new minor version.
                                                        <string>: any string
                                                        Default: ^[Ff]eat.*
-ni, --no-issue-name <string>                           Name of virtual issue 
                                                        that contains commits that 
                                                        has no issue associated.
                                                        <string>: any string
                                                        Default: No issue
-of, --output-file <string>                             Write output to file.
                                                        <string>: any string
                                                        Default: 
-pavp, --patch-version-pattern <string>                 Commit messages 
                                                        matching this, optional, 
                                                        regular expression will 
                                                        trigger new patch version.
                                                        <string>: any string
                                                        Default: null
-pcv, --print-current-version                           Like --print-next-
                                                        version unless the current 
                                                        commit is tagged with a 
                                                        version, if so it will print 
                                                        that version.
                                                        Default: disabled
-phv, --print-highest-version                           Print the highest 
                                                        version, determined by tags in 
                                                        repo, and exit.
                                                        Default: disabled
-phvt, --print-highest-version-tag                      Print the tag 
                                                        corresponding to highest version, 
                                                        and exit.
                                                        Default: disabled
-pnv, --print-next-version                              Print the next 
                                                        version, determined by commits 
                                                        since highest version, and 
                                                        exit.
                                                        Default: disabled
-ptf, --prepend-to-file <string>                        Add the changelog to 
                                                        top of given file.
                                                        <string>: any string
                                                        Default: null
-r, --repo <string>                                     Repository.
                                                        <string>: any string
                                                        Default: .
-re, --redmine-enabled                                  Enable parsing for 
                                                        Redmine issues.
                                                        Default: disabled
-rhh, --register-handlebars-helper <string>             Handlebar helpers, 
                                                        https://handlebarsjs.
                                                        com/guide/block-helpers.html, to 
                                                        register and use in given 
                                                        template.
                                                        <string>: any string
                                                        Default: 
-ri, --remove-issue-from-message                        Dont print any issues 
                                                        in the messages of 
                                                        commits.
                                                        Default: disabled
-rmp, --redmine-pattern <string>                        Redmine issue pattern.
                                                        <string>: any string
                                                        Default: #([0-9]+)
-rmpw, --redmine-password <string>                      Optional password to 
                                                        authenticate with Redmine.
                                                        <string>: any string
                                                        Default: 
-rms, --redmine-server <string>                         Redmine server. When a 
                                                        Redmine server is given, the 
                                                        title of the Redmine issues 
                                                        can be used in the 
                                                        changelog.
                                                        <string>: any string
                                                        Default: null
-rmt, --redmine-token <string>                          Optional token/api-key 
                                                        to authenticate with 
                                                        Redmine.
                                                        <string>: any string
                                                        Default: 
-rmu, --redmine-username <string>                       Optional username to 
                                                        authenticate with Redmine.
                                                        <string>: any string
                                                        Default: 
-rt, --readable-tag-name <string>                       Pattern to extract 
                                                        readable part of tag.
                                                        <string>: any string
                                                        Default: /([^/]+?)$
-sf, --settings-file <string>                           Use settings from file.
                                                        <string>: any string
                                                        Default: null
--show-debug-info                                       Please run your 
                                                        command with this parameter 
                                                        and supply output when 
                                                        reporting bugs.
                                                        Default: disabled
-std, --stdout                                          Print builder to 
                                                        <STDOUT>.
                                                        Default: disabled
-t, --template <string>                                 Template to use. A 
                                                        default template will be used 
                                                        if not specified.
                                                        <string>: any string
                                                        Default: changelog.mustache
-tbd, --template-base-dir <string>                      Base dir of templates.
                                                        <string>: any string
                                                        Default: null
-tec, --template-content <string>                       String to use as 
                                                        template.
                                                        <string>: any string
                                                        Default: 
-tps, --template-partial-suffix <string>                File ending for 
                                                        partials.
                                                        <string>: any string
                                                        Default: .hbs
-tre, --to-revision <string>                            To revision.
                                                        <string>: any string
                                                        Default: refs/heads/master
-trei, --to-revision-inclusiveness                      Include, or exclude, 
<InclusivenessStrategy>                                 specified revision.
                                                        <InclusivenessStrategy>: {INCLUSIVE | EXCLUSIVE | DEFAULT}
                                                        Default: DEFAULT
-tz, --time-zone <string>                               TimeZone to use when 
                                                        printing dates.
                                                        <string>: any string
                                                        Default: UTC
-ui, --use-integrations                                 Use integrations to 
                                                        get more details on 
                                                        commits.
                                                        Default: disabled
-ut, --untagged-name <string>                           When listing commits 
                                                        per tag, this will by the 
                                                        name of a virtual tag that 
                                                        contains commits not available 
                                                        in any git tag.
                                                        <string>: any string
                                                        Default: No tag
```

## Usage - template base dir

You can use [partials](http://jknack.github.io/handlebars.java/reuse.html) in your templates.

`/dir/changelog.hbs`

```hbs
{{#commits}}
{{> commit}}
{{/commits}}
```

`/dir/base/commit.partial`

```hbs
## {{authorName}} - {{commitTime}}
[{{hashFull}}](https://server/{{hash}})
{{{message}}}
```

This is configured like:

```sh
npx git-changelog-command-line -std \
 --template-base-dir /dir/base \
 --template /dir/changelog.hbs
```

If partials have a different ending, you can specify that with `--template-partial-suffix`.
