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
{{#isReleaseTag .}}
## [{{name}}](https://gitlab.com/html-validate/html-validate/compare/{{name}}) ({{tagTime}})
	{{#containsType commits type='feat'}}
### Features
		{{#commits}}
		{{#isType messageTitle type='feat'}}
{{messageTitle}}
		{{/isType}}
		{{/commits}}

	{{/containsType}}

	{{#containsType commits type='fix'}}
### Bug Fixes
		{{#commits}}
		{{#isType messageTitle type='fix'}}
{{messageTitle}}
		{{/isType}}
		{{/commits}}

	{{/containsType}}


	{{#containsType commits type='chore'}}
### Chores
		{{#commits}}
		{{#isType messageTitle type='chore'}}
{{messageTitle}}
		{{/isType}}
		{{/commits}}

	{{/containsType}}

{{/isReleaseTag}}
{{/tags}}
"
