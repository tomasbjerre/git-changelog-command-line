# git clone https://gitlab.com/html-validate/html-validate.git
# cd html-validate

highestTag=$(npx git-changelog-command-line \
 --print-highest-version-tag)

echo Last release detected as $highestTag

nextVersion=$(npx git-changelog-command-line \
 --major-version-pattern "^[Bb]reaking.*" \
 --minor-version-pattern "^[Ff]eat.*" \
 --print-next-version)

echo Next release based on commits is: $nextVersion

echo Changelog:

npx git-changelog-command-line \
 --to-ref HEAD \
 --stdout \
 --template-content "
# Changelog

{{#tags}}
{{#ifReleaseTag .}}
## [{{name}}](https://gitlab.com/html-validate/html-validate/compare/{{name}}) ({{tagTime}})

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
