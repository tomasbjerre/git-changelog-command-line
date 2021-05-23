# git clone https://gitlab.com/html-validate/html-validate.git
# cd html-validate

highestTag=$(npx git-changelog-command-line \
 --major-version-pattern "^[Bb]reaking.*" \
 --minor-version-pattern "^[Ff]eat.*" \
 --print-highest-version-tag)

echo Last release detected as $highestTag

nextVersion=$(npx git-changelog-command-line \
 --major-version-pattern "^[Bb]reaking.*" \
 --minor-version-pattern "^[Ff]eat.*" \
 --print-next-version)

echo Next release based on commits is: $nextVersion

echo Changelog would be prepended with:

npx git-changelog-command-line \
 --from-ref $highestTag \
 --to-ref HEAD \
 --stdout \
 --template-content "

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

Handlebars.registerHelper('printCommit', function(commit, options) {
  const message = commit.messageTitle.split(':')[1].trim()
  return ' - ' + message + ' ([' + commit.hash + '](https://gitlab.com/html-validate/html-validate/commit/' + commit.hashFull + '))';
});
"
