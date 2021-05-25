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
	{{#hasType commits type='feat'}}
### Features
		{{#commits}}
		{{#isType messageTitle type='feat'}}
{{printCommit .}}
		{{/isType}}
		{{/commits}}

	{{/hasType}}

	{{#hasType commits type='fix'}}
### Bug Fixes
		{{#commits}}
		{{#isType messageTitle type='fix'}}
{{printCommit .}}
		{{/isType}}
		{{/commits}}

	{{/hasType}}


	{{#hasType commits type='chore'}}
### Chores
		{{#commits}}
		{{#isType messageTitle type='chore'}}
{{printCommit .}}
		{{/isType}}
		{{/commits}}

	{{/hasType}}

{{/isReleaseTag}}
{{/tags}}
" \
--register-handlebars-helper "
Handlebars.registerHelper('hasType', function(commits, options) {
  const commitType = options.hash['type']
  for (let i = 0; i < commits.length; i++) {
    if (new RegExp('^' + commitType + '.*').test(commits[i].message)) {
      return options.fn(this);
    }
  }
  return options.inverse(this);
});

Handlebars.registerHelper('isType', function(messageTitle, options) {
	const commitType = options.hash['type']
	if (new RegExp('^' + commitType + '.*').test(messageTitle)) {
		return options.fn(this);
	} else {
		return options.inverse(this);
	}
});

Handlebars.registerHelper('isReleaseTag', function(tag, options) {
	const name = tag.name
	if (new RegExp('^v[0-9]+\\\\.[0-9]+\\\\.[0-9]+$').test(name)) {
		return options.fn(this);
	} else {
		return options.inverse(this);
	}
});

Handlebars.registerHelper('printCommit', function(commit, options) {
	let type = new RegExp('^([^:\(]+)').exec(commit.messageTitle)
	if (!type) {
		return commit.messageTitle;
	}
	type = type[1];

	let scope = '';
	let scopes = new RegExp('^[a-z]+\\\\\\(([^\\\\\\)]+)').exec(commit.messageTitle)
	if (scopes) {
		scopes = scopes[1]
		if (scopes.indexOf(':') == -1) {
			scope = '**' + scopes + '**';
		} else {
			const parts = scopes.split(':');
			for (let i = 0; i < parts.length; i++) {
				scope += ' **' + parts[i] + '**'
			}
		}
	}

	let subject = commit.messageTitle
	if (subject.indexOf('(refs ') != -1) {
		subject = subject.substring(0,subject.indexOf('(refs '))
	}
	if (subject.indexOf('(fixes ') != -1) {
		subject = subject.substring(0,subject.indexOf('(fixes '))
	}

	return ' - ' + scope + ' ' + subject + ' ([' + commit.hash + '](https://gitlab.com/html-validate/html-validate/commit/' + commit.hashFull + '))';
});
"
