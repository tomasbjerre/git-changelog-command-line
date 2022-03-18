package se.bjurr.gitchangelog.main;

import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_DATEFORMAT;
import static se.bjurr.gitchangelog.internal.settings.Settings.defaultSettings;
import static se.softhouse.jargo.Arguments.fileArgument;
import static se.softhouse.jargo.Arguments.helpArgument;
import static se.softhouse.jargo.Arguments.optionArgument;
import static se.softhouse.jargo.Arguments.stringArgument;
import static se.softhouse.jargo.CommandLineParser.withArguments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.api.GitChangelogApiConstants;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersion;
import se.bjurr.gitchangelog.internal.settings.Settings;
import se.softhouse.jargo.Argument;
import se.softhouse.jargo.ArgumentException;
import se.softhouse.jargo.ParsedArguments;

public class Main {
  private static final String PARAM_REGISTER_HANDLEBARS_HELPER = "-rhh";
  private static final String PARAM_PRINT_HIGHEST_VERSION = "-phv";
  private static final String PARAM_PRINT_HIGHEST_VERSION_TAG = "-phvt";
  private static final String PARAM_PRINT_NEXT_VERSION = "-pnv";
  private static final String PARAM_PATCH_VERSION_PATTERN = "-pavp";
  private static final String PARAM_MINOR_VERSION_PATTERN = "-mivp";
  private static final String PARAM_MAJOR_VERSION_PATTERN = "-mavp";
  private static final String PARAM_PREPEND_TO_FILE = "-ptf";
  public static final String PARAM_SETTINGS_FILE = "-sf";
  public static final String PARAM_OUTPUT_FILE = "-of";
  public static final String PARAM_OUTPUT_STDOUT = "-std";
  public static final String PARAM_TEMPLATE = "-t";
  public static final String PARAM_TEMPLATE_BASE_DIR = "-tbd";
  public static final String PARAM_TEMPLATE_PARTIAL_SUFFIX = "-tps";
  public static final String PARAM_REPO = "-r";
  public static final String PARAM_FROM_REF = "-fr";
  public static final String PARAM_TO_REF = "-tr";
  public static final String PARAM_FROM_COMMIT = "-fc";
  public static final String PARAM_TO_COMMIT = "-tc";
  public static final String PARAM_IGNORE_PATTERN = "-ip";
  public static final String PARAM_IGNORE_OLDER_PATTERN = "-iot";
  public static final String PARAM_IGNORE_TAG_PATTERN = "-itp";
  public static final String PARAM_JIRA_SERVER = "-js";
  public static final String PARAM_JIRA_ISSUE_PATTERN = "-jp";
  public static final String PARAM_JIRA_USERNAME = "-ju";
  public static final String PARAM_JIRA_PASSWORD = "-jpw";
  public static final String PARAM_JIRA_BASIC_AUTH = "-jba";
  public static final String PARAM_JIRA_BEARER = "-jbt";
  public static final String PARAM_REDMINE_SERVER = "-rms";
  public static final String PARAM_REDMINE_ISSUE_PATTERN = "-rmp";
  public static final String PARAM_REDMINE_USERNAME = "-rmu";
  public static final String PARAM_REDMINE_PASSWORD = "-rmpw";
  public static final String PARAM_REDMINE_TOKEN = "-rmt";
  public static final String PARAM_CUSTOM_ISSUE_NAME = "-cn";
  public static final String PARAM_CUSTOM_ISSUE_PATTERN = "-cp";
  public static final String PARAM_CUSTOM_ISSUE_LINK = "-cl";
  public static final String PARAM_CUSTOM_ISSUE_TITLE = "-ct";
  public static final String PARAM_UNTAGGED_TAG_NAME = "-ut";
  public static final String PARAM_TIMEZONE = "-tz";
  public static final String PARAM_DATEFORMAT = "-df";
  public static final String PARAM_NOISSUE = "-ni";
  public static final String PARAM_IGNORE_NOISSUE = "-ini";
  public static final String PARAM_READABLETAGNAME = "-rt";
  public static final String PARAM_REMOVEISSUE = "-ri";
  public static final String PARAM_GITHUBAPI = "-gapi";
  public static final String PARAM_GITHUBTOKEN = "-gtok";
  public static final String PARAM_EXTENDED_VARIABLES = "-ex";
  public static final String PARAM_EXTENDED_HEADERS = "-eh";
  public static final String PARAM_TEMPLATE_CONTENT = "-tec";
  public static final String PARAM_GITLABTOKEN = "-glt";
  public static final String PARAM_GITLABSERVER = "-gls";
  public static final String PARAM_GITLABPROJECTNAME = "-glpn";

  private static String systemOutPrintln;
  private static boolean recordSystemOutPrintln;

  public static void main(final String args[]) throws Exception {
    final Settings defaultSettings = defaultSettings();
    final Argument<?> helpArgument = helpArgument("-h", "--help");

    final Argument<String> settingsArgument =
        stringArgument(PARAM_SETTINGS_FILE, "--settings-file") //
            .description("Use settings from file.") //
            .defaultValue(null) //
            .build();
    final Argument<Boolean> outputStdoutArgument =
        optionArgument(PARAM_OUTPUT_STDOUT, "--stdout") //
            .description("Print builder to <STDOUT>.") //
            .build();
    final Argument<String> outputFileArgument =
        stringArgument(PARAM_OUTPUT_FILE, "--output-file") //
            .description("Write output to file.") //
            .build();

    final Argument<String> templatePathArgument =
        stringArgument(PARAM_TEMPLATE, "--template") //
            .description("Template to use. A default template will be used if not specified.") //
            .defaultValue(defaultSettings.getTemplatePath()) //
            .build();

    final Argument<String> templateBaseDirArgument =
        stringArgument(PARAM_TEMPLATE_BASE_DIR, "--template-base-dir") //
            .description("Base dir of templates.") //
            .defaultValue(defaultSettings.getTemplateBaseDir()) //
            .build();

    final Argument<String> templatePartialSuffixArgument =
        stringArgument(PARAM_TEMPLATE_PARTIAL_SUFFIX, "--template-partial-suffix") //
            .description("File ending for partials.") //
            .defaultValue(defaultSettings.getTemplateSuffix()) //
            .build();

    final Argument<String> untaggedTagNameArgument =
        stringArgument(PARAM_UNTAGGED_TAG_NAME, "--untagged-name") //
            .description(
                "When listing commits per tag, this will by the name of a virtual tag that contains commits not available in any git tag.") //
            .defaultValue(defaultSettings.getUntaggedName()) //
            .build();

    final Argument<String> fromRepoArgument =
        stringArgument(PARAM_REPO, "--repo") //
            .description("Repository.") //
            .defaultValue(defaultSettings.getFromRepo()) //
            .build();
    final Argument<String> fromRefArgument =
        stringArgument(PARAM_FROM_REF, "--from-ref") //
            .description("From ref.") //
            .defaultValue(defaultSettings.getFromRef().orElse(null)) //
            .build();
    final Argument<String> toRefArgument =
        stringArgument(PARAM_TO_REF, "--to-ref") //
            .description("To ref.") //
            .defaultValue(defaultSettings.getToRef().orElse(null)) //
            .build();
    final Argument<String> fromCommitArgument =
        stringArgument(PARAM_FROM_COMMIT, "--from-commit") //
            .description("From commit.") //
            .defaultValue(defaultSettings.getFromCommit().orElse(null)) //
            .build();
    final Argument<String> toCommitArgument =
        stringArgument(PARAM_TO_COMMIT, "--to-commit") //
            .description("To commit.") //
            .defaultValue(defaultSettings.getToCommit().orElse(null)) //
            .build();

    final Argument<String> ignoreCommitsIfMessageMatchesArgument =
        stringArgument(PARAM_IGNORE_PATTERN, "--ignore-pattern") //
            .description("Ignore commits where pattern matches message.") //
            .defaultValue(defaultSettings.getIgnoreCommitsIfMessageMatches()) //
            .build();

    final Argument<String> ignoreCommitsOlderThanArgument =
        stringArgument(PARAM_IGNORE_OLDER_PATTERN, "--ignore-older-than") //
            .description("Ignore commits older than " + DEFAULT_DATEFORMAT + ".") //
            .build();

    final Argument<String> ignoreTagsIfNameMatchesArgument =
        stringArgument(PARAM_IGNORE_TAG_PATTERN, "--ignore-tag-pattern") //
            .description(
                "Ignore tags that matches regular expression. Can be used to ignore release candidates and only include actual releases.") //
            .defaultValue(defaultSettings.getIgnoreTagsIfNameMatches().orElse(null)) //
            .build();

    final Argument<String> jiraServerArgument =
        stringArgument(PARAM_JIRA_SERVER, "--jiraServer", "--jira-server") //
            .description(
                "Jira server. When a Jira server is given, the title of the Jira issues can be used in the changelog.") //
            .defaultValue(defaultSettings.getJiraServer().orElse(null)) //
            .build();
    final Argument<String> jiraIssuePatternArgument =
        stringArgument(PARAM_JIRA_ISSUE_PATTERN, "--jira-pattern") //
            .description("Jira issue pattern.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final Argument<String> jiraUsernamePatternArgument =
        stringArgument(PARAM_JIRA_USERNAME, "--jira-username") //
            .description("Optional username to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final Argument<String> jiraPasswordPatternArgument =
        stringArgument(PARAM_JIRA_PASSWORD, "--jira-password") //
            .description("Optional password to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final Argument<String> jiraBasicAuthStringPatternArgument =
        stringArgument(PARAM_JIRA_BASIC_AUTH, "--jira-basic-auth") //
            .description("Optional token to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final Argument<String> jiraBearerArgument =
        stringArgument(PARAM_JIRA_BEARER, "--jira-bearer") //
            .description("Optional token to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();

    final Argument<String> redmineServerArgument =
        stringArgument(PARAM_REDMINE_SERVER, "--redmine-server") //
            .description(
                "Redmine server. When a Redmine server is given, the title of the Redmine issues can be used in the changelog.") //
            .defaultValue(defaultSettings.getRedmineServer().orElse(null)) //
            .build();
    final Argument<String> redmineIssuePatternArgument =
        stringArgument(PARAM_REDMINE_ISSUE_PATTERN, "--redmine-pattern") //
            .description("Redmine issue pattern.") //
            .defaultValue(defaultSettings.getRedmineIssuePattern()) //
            .build();
    final Argument<String> redmineUsernameArgument =
        stringArgument(PARAM_REDMINE_USERNAME, "--redmine-username") //
            .description("Optional username to authenticate with Redmine.") //
            .build();
    final Argument<String> redminePasswordArgument =
        stringArgument(PARAM_REDMINE_PASSWORD, "--redmine-password") //
            .description("Optional password to authenticate with Redmine.") //
            .build();
    final Argument<String> redmineTokenArgument =
        stringArgument(PARAM_REDMINE_TOKEN, "--redmine-token") //
            .description("Optional token/api-key to authenticate with Redmine.") //
            .build();

    final Argument<String> customIssueNameArgument =
        stringArgument(PARAM_CUSTOM_ISSUE_NAME, "--custom-issue-name") //
            .description("Custom issue name.") //
            .defaultValue(null) //
            .build();
    final Argument<String> customIssuePatternArgument =
        stringArgument(PARAM_CUSTOM_ISSUE_PATTERN, "--custom-issue-pattern") //
            .description("Custom issue pattern.") //
            .defaultValue(null) //
            .build();
    final Argument<String> customIssueLinkArgument =
        stringArgument(PARAM_CUSTOM_ISSUE_LINK, "--custom-issue-link") //
            .description(
                "Custom issue link. Supports variables like ${PATTERN_GROUP_1} to inject variables from pattern.") //
            .defaultValue(null) //
            .build();
    final Argument<String> customIssueTitleArgument =
        stringArgument(PARAM_CUSTOM_ISSUE_TITLE, "--custom-issue-title") //
            .description(
                "Custom issue title. Supports variables like ${PATTERN_GROUP_1} to inject variables from pattern.") //
            .defaultValue(null) //
            .build();

    final Argument<String> timeZoneArgument =
        stringArgument(PARAM_TIMEZONE, "--time-zone") //
            .description("TimeZone to use when printing dates.") //
            .defaultValue(defaultSettings.getTimeZone()) //
            .build();
    final Argument<String> dateFormatArgument =
        stringArgument(PARAM_DATEFORMAT, "--date-format") //
            .description("Format to use when printing dates.") //
            .defaultValue(defaultSettings.getDateFormat()) //
            .build();
    final Argument<String> noIssueArgument =
        stringArgument(PARAM_NOISSUE, "--no-issue-name") //
            .description(
                "Name of virtual issue that contains commits that has no issue associated.") //
            .defaultValue(defaultSettings.getNoIssueName()) //
            .build();
    final Argument<Boolean> ignoreCommitsWithoutIssueArgument =
        optionArgument(PARAM_IGNORE_NOISSUE, "--ignore-commits-without-issue") //
            .description("Ignore commits that is not included in any issue.") //
            .build();
    final Argument<String> readableTagNameArgument =
        stringArgument(PARAM_READABLETAGNAME, "--readable-tag-name") //
            .description("Pattern to extract readable part of tag.") //
            .defaultValue(defaultSettings.getReadableTagName()) //
            .build();
    final Argument<Boolean> removeIssueFromMessageArgument =
        optionArgument(PARAM_REMOVEISSUE, "--remove-issue-from-message") //
            .description("Dont print any issues in the messages of commits.") //
            .build();

    final Argument<String> gitHubApiArgument =
        stringArgument(PARAM_GITHUBAPI, "--github-api") //
            .description(
                "GitHub API. Like: https://api.github.com/repos/tomasbjerre/git-changelog-command-line/") //
            .defaultValue("") //
            .build();
    final Argument<String> gitHubTokenArgument =
        stringArgument(PARAM_GITHUBTOKEN, "--github-token") //
            .description(
                "GitHub API OAuth2 token. You can get it from: curl -u 'yourgithubuser' -d '{\"note\":\"Git Changelog Lib\"}' https://api.github.com/authorizations") //
            .defaultValue("") //
            .build();

    final Argument<String> extendedVariablesArgument =
        stringArgument(PARAM_EXTENDED_VARIABLES, "--extended-variables") //
            .description(
                "Extended variables that will be available as {{extended.*}}. "
                    + PARAM_EXTENDED_VARIABLES
                    + " \"{\\\"var1\\\": \\\"val1\\\"}\" will print out \"val1\" for a template like \"{{extended.var1}}\"") //
            .defaultValue("") //
            .build();

    final Argument<List<String>> extendedHeadersArgument =
        stringArgument(PARAM_EXTENDED_HEADERS, "--extended-headers") //
            .repeated()
            .description(
                "Extended headers that will send when access JIRA. e.g. "
                    + PARAM_EXTENDED_HEADERS
                    + " CF-Access-Client-ID:abcde12345xyz.access") //
            .build();

    final Argument<String> templateContentArgument =
        stringArgument(PARAM_TEMPLATE_CONTENT, "--template-content") //
            .description("String to use as template.") //
            .defaultValue("") //
            .build();

    final Argument<String> gitLabTokenArgument =
        stringArgument(PARAM_GITLABTOKEN, "--gitlab-token") //
            .description("GitLab API token.") //
            .defaultValue("") //
            .build();
    final Argument<String> gitLabServerArgument =
        stringArgument(PARAM_GITLABSERVER, "--gitlab-server") //
            .description("GitLab server, like https://gitlab.com/.") //
            .defaultValue("") //
            .build();
    final Argument<String> gitLabProjectNameArgument =
        stringArgument(PARAM_GITLABPROJECTNAME, "--gitlab-project-name") //
            .description("GitLab project name.") //
            .defaultValue("") //
            .build();

    final Argument<Boolean> printHighestVersion =
        optionArgument(PARAM_PRINT_HIGHEST_VERSION, "--print-highest-version") //
            .description("Print the highest version, determined by tags in repo, and exit.") //
            .defaultValue(false)
            .build();

    final Argument<Boolean> printHighestVersionTag =
        optionArgument(PARAM_PRINT_HIGHEST_VERSION_TAG, "--print-highest-version-tag") //
            .description("Print the tag corresponding to highest version, and exit.") //
            .defaultValue(false)
            .build();

    final Argument<Boolean> printNextVersion =
        optionArgument(PARAM_PRINT_NEXT_VERSION, "--print-next-version") //
            .description(
                "Print the next version, determined by commits since highest version, and exit.") //
            .defaultValue(false)
            .build();

    final Argument<String> registerHandlebarsHelper =
        stringArgument(PARAM_REGISTER_HANDLEBARS_HELPER, "--register-handlebars-helper") //
            .description(
                "Handlebar helpers, https://handlebarsjs.com/guide/block-helpers.html, to register and use in given template.") //
            .defaultValue("")
            .build();

    final Argument<File> handlebarsHelperFile =
        fileArgument("-handlebars-helper-file", "-hhf")
            .description("Can be used to add extra helpers.")
            .build();

    final Argument<String> prependToFile =
        stringArgument(PARAM_PREPEND_TO_FILE, "--prepend-to-file") //
            .description("Add the changelog to top of given file.") //
            .defaultValue(null)
            .build();

    final Argument<String> majorVersionPattern =
        stringArgument(PARAM_MAJOR_VERSION_PATTERN, "--major-version-pattern") //
            .description(
                "Commit messages matching this, optional, regular expression will trigger new major version.") //
            .defaultValue(null)
            .build();

    final Argument<String> minorVersionPattern =
        stringArgument(PARAM_MINOR_VERSION_PATTERN, "--minor-version-pattern") //
            .description(
                "Commit messages matching this, optional, regular expression will trigger new minor version.") //
            .defaultValue(GitChangelogApiConstants.DEFAULT_MINOR_PATTERN)
            .build();

    final Argument<String> patchVersionPattern =
        stringArgument(PARAM_PATCH_VERSION_PATTERN, "--patch-version-pattern") //
            .description(
                "Commit messages matching this, optional, regular expression will trigger new patch version.") //
            .defaultValue(GitChangelogApiConstants.DEFAULT_PATCH_PATTERN)
            .build();

    final Argument<Boolean> showDebugInfo =
        optionArgument("--show-debug-info")
            .description(
                "Please run your command with this parameter and supply output when reporting bugs.")
            .build();

    final Argument<Boolean> jiraEnabledArgument =
        optionArgument("-je", "--jira-enabled") //
            .description("Enable parsing for Jira issues.") //
            .build();

    final Argument<Boolean> githubEnabledArgument =
        optionArgument("-ge", "--github-enabled") //
            .description("Enable parsing for GitHub issues.") //
            .build();

    final Argument<Boolean> gitlabEnabledArgument =
        optionArgument("-gl", "--gitlab-enabled") //
            .description("Enable parsing for GitLab issues.") //
            .build();

    final Argument<Boolean> redmineEnabledArgument =
        optionArgument("-re", "--redmine-enabled") //
            .description("Enable parsing for Redmine issues.") //
            .build();

    final Argument<Boolean> useIntegrationsArgument =
        optionArgument("-ui", "--use-integrations") //
            .description("Use integrations to get more details on commits.") //
            .build();

    try {
      final ParsedArguments arg =
          withArguments(
                  helpArgument,
                  settingsArgument,
                  outputStdoutArgument,
                  outputFileArgument,
                  templatePathArgument,
                  templateBaseDirArgument,
                  templatePartialSuffixArgument,
                  fromCommitArgument,
                  fromRefArgument,
                  fromRepoArgument,
                  toCommitArgument,
                  toRefArgument,
                  untaggedTagNameArgument,
                  jiraIssuePatternArgument,
                  jiraServerArgument,
                  redmineIssuePatternArgument,
                  redmineServerArgument,
                  ignoreCommitsIfMessageMatchesArgument,
                  ignoreCommitsOlderThanArgument,
                  customIssueLinkArgument,
                  customIssueTitleArgument,
                  customIssueNameArgument,
                  customIssuePatternArgument,
                  timeZoneArgument,
                  dateFormatArgument,
                  noIssueArgument,
                  readableTagNameArgument,
                  removeIssueFromMessageArgument,
                  gitHubApiArgument,
                  jiraUsernamePatternArgument,
                  jiraPasswordPatternArgument,
                  jiraBasicAuthStringPatternArgument,
                  jiraBearerArgument,
                  redmineUsernameArgument,
                  redminePasswordArgument,
                  redmineTokenArgument,
                  extendedVariablesArgument,
                  extendedHeadersArgument,
                  templateContentArgument,
                  gitHubTokenArgument,
                  ignoreCommitsWithoutIssueArgument,
                  ignoreTagsIfNameMatchesArgument,
                  gitLabTokenArgument,
                  gitLabServerArgument,
                  gitLabProjectNameArgument,
                  printHighestVersion,
                  printHighestVersionTag,
                  printNextVersion,
                  registerHandlebarsHelper,
                  prependToFile,
                  majorVersionPattern,
                  minorVersionPattern,
                  patchVersionPattern,
                  showDebugInfo,
                  handlebarsHelperFile,
                  jiraEnabledArgument,
                  githubEnabledArgument,
                  gitlabEnabledArgument,
                  redmineEnabledArgument,
                  useIntegrationsArgument) //
              .parse(args);

      final GitChangelogApi changelogApiBuilder =
          gitChangelogApiBuilder()
              .withUseIntegrations(arg.wasGiven(useIntegrationsArgument))
              .withJiraEnabled(arg.wasGiven(jiraEnabledArgument))
              .withRedmineEnabled(arg.wasGiven(redmineEnabledArgument))
              .withGitHubEnabled(arg.wasGiven(githubEnabledArgument))
              .withGitLabEnabled(arg.wasGiven(gitlabEnabledArgument));

      if (!arg.get(registerHandlebarsHelper).trim().isEmpty()) {
        changelogApiBuilder.withHandlebarsHelper(arg.get(registerHandlebarsHelper));
      }

      if (arg.wasGiven(handlebarsHelperFile)) {
        final byte[] content = Files.readAllBytes(arg.get(handlebarsHelperFile).toPath());
        final String contentString = new String(content, StandardCharsets.UTF_8);
        changelogApiBuilder.withHandlebarsHelper(contentString);
      }

      if (arg.wasGiven(settingsArgument)) {
        changelogApiBuilder.withSettings(new File(arg.get(settingsArgument)).toURI().toURL());
      }

      if (arg.wasGiven(removeIssueFromMessageArgument)) {
        changelogApiBuilder.withRemoveIssueFromMessageArgument(true);
      }
      if (arg.wasGiven(ignoreCommitsWithoutIssueArgument)) {
        changelogApiBuilder.withIgnoreCommitsWithoutIssue(true);
      }

      if (arg.wasGiven(extendedVariablesArgument)) {
        final String jsonString = arg.get(extendedVariablesArgument);
        final Gson gson = new Gson();
        final Type type = new TypeToken<Map<String, Object>>() {}.getType();
        final Object jsonObject = gson.fromJson(jsonString, type);
        final Map<String, Object> extendedVariables = new HashMap<>();
        extendedVariables.put("extended", jsonObject);
        changelogApiBuilder.withExtendedVariables(extendedVariables);
      }

      if (arg.wasGiven(extendedHeadersArgument)) {
        final List<String> extendedHeaders = arg.get(extendedHeadersArgument);
        final Map<String, String> headers = new HashMap<>();
        for (final String extendedHeader : extendedHeaders) {
          final String[] splitted = extendedHeader.split(":");
          if (splitted.length != 2) {
            throw new RuntimeException("Headers should be on format \"headername:headervalue\"");
          }
          final String key = splitted[0].trim();
          final String value = splitted[1].trim();
          headers.put(key, value);
        }
        changelogApiBuilder.withExtendedHeaders(headers);
      }

      if (arg.wasGiven(templateContentArgument)) {
        changelogApiBuilder.withTemplateContent(arg.get(templateContentArgument));
      }

      if (arg.wasGiven(templateBaseDirArgument)) {
        changelogApiBuilder.withTemplateBaseDir(arg.get(templateBaseDirArgument));
      }

      if (arg.wasGiven(templatePartialSuffixArgument)) {
        changelogApiBuilder.withTemplateSuffix(arg.get(templatePartialSuffixArgument));
      }

      if (arg.wasGiven(fromRepoArgument)) {
        changelogApiBuilder.withFromRepo(arg.get(fromRepoArgument));
      }
      if (arg.wasGiven(untaggedTagNameArgument)) {
        changelogApiBuilder.withUntaggedName(arg.get(untaggedTagNameArgument));
      }
      if (arg.wasGiven(ignoreCommitsIfMessageMatchesArgument)) {
        changelogApiBuilder.withIgnoreCommitsWithMessage(
            arg.get(ignoreCommitsIfMessageMatchesArgument));
      }
      if (arg.wasGiven(ignoreCommitsOlderThanArgument)) {
        final Date date =
            new SimpleDateFormat(DEFAULT_DATEFORMAT).parse(arg.get(ignoreCommitsOlderThanArgument));
        changelogApiBuilder.withIgnoreCommitsOlderThan(date);
      }
      if (arg.wasGiven(ignoreTagsIfNameMatchesArgument)) {
        changelogApiBuilder.withIgnoreTagsIfNameMatches(arg.get(ignoreTagsIfNameMatchesArgument));
      }
      if (arg.wasGiven(templatePathArgument)) {
        changelogApiBuilder.withTemplatePath(arg.get(templatePathArgument));
      }
      if (arg.wasGiven(jiraIssuePatternArgument)) {
        changelogApiBuilder.withJiraIssuePattern(arg.get(jiraIssuePatternArgument));
      }
      if (arg.wasGiven(jiraServerArgument)) {
        changelogApiBuilder.withJiraServer(arg.get(jiraServerArgument));
      }
      if (arg.wasGiven(jiraUsernamePatternArgument)) {
        changelogApiBuilder.withJiraUsername(arg.get(jiraUsernamePatternArgument));
      }
      if (arg.wasGiven(jiraPasswordPatternArgument)) {
        changelogApiBuilder.withJiraPassword(arg.get(jiraPasswordPatternArgument));
      }
      if (arg.wasGiven(jiraBasicAuthStringPatternArgument)) {
        changelogApiBuilder.withJiraBasicAuthString(arg.get(jiraBasicAuthStringPatternArgument));
      }
      if (arg.wasGiven(jiraBearerArgument)) {
        changelogApiBuilder.withJiraBearer(arg.get(jiraBearerArgument));
      }
      if (arg.wasGiven(redmineIssuePatternArgument)) {
        changelogApiBuilder.withRedmineIssuePattern(arg.get(redmineIssuePatternArgument));
      }
      if (arg.wasGiven(redmineServerArgument)) {
        changelogApiBuilder.withRedmineServer(arg.get(redmineServerArgument));
      }
      if (arg.wasGiven(redmineUsernameArgument)) {
        changelogApiBuilder.withRedmineUsername(arg.get(redmineUsernameArgument));
      }
      if (arg.wasGiven(redminePasswordArgument)) {
        changelogApiBuilder.withRedminePassword(arg.get(redminePasswordArgument));
      }
      if (arg.wasGiven(redmineTokenArgument)) {
        changelogApiBuilder.withRedmineToken(arg.get(redmineTokenArgument));
      }
      if (arg.wasGiven(timeZoneArgument)) {
        changelogApiBuilder.withTimeZone(arg.get(timeZoneArgument));
      }
      if (arg.wasGiven(dateFormatArgument)) {
        changelogApiBuilder.withDateFormat(arg.get(dateFormatArgument));
      }
      if (arg.wasGiven(noIssueArgument)) {
        changelogApiBuilder.withNoIssueName(arg.get(noIssueArgument));
      }
      if (arg.wasGiven(readableTagNameArgument)) {
        changelogApiBuilder.withReadableTagName(arg.get(readableTagNameArgument));
      }

      if (arg.wasGiven(fromCommitArgument)) {
        changelogApiBuilder.withFromCommit(arg.get(fromCommitArgument));
        changelogApiBuilder.withFromRef(null);
      }
      if (arg.wasGiven(fromRefArgument)) {
        changelogApiBuilder.withFromCommit(null);
        changelogApiBuilder.withFromRef(arg.get(fromRefArgument));
      }
      if (arg.wasGiven(toCommitArgument)) {
        changelogApiBuilder.withToCommit(arg.get(toCommitArgument));
        changelogApiBuilder.withToRef(null);
      }
      if (arg.wasGiven(toRefArgument)) {
        changelogApiBuilder.withToCommit(null);
        changelogApiBuilder.withToRef(arg.get(toRefArgument));
      }
      if (arg.wasGiven(gitHubApiArgument)) {
        changelogApiBuilder.withGitHubApi(arg.get(gitHubApiArgument));
      }
      if (arg.wasGiven(gitHubTokenArgument)) {
        changelogApiBuilder.withGitHubToken(arg.get(gitHubTokenArgument));
      }

      if (arg.wasGiven(gitLabServerArgument)) {
        changelogApiBuilder.withGitLabServer(arg.get(gitLabServerArgument));
      }
      if (arg.wasGiven(gitLabProjectNameArgument)) {
        changelogApiBuilder.withGitLabProjectName(arg.get(gitLabProjectNameArgument));
      }
      if (arg.wasGiven(gitLabTokenArgument)) {
        changelogApiBuilder.withGitLabToken(arg.get(gitLabTokenArgument));
      }

      if ( //
      arg.wasGiven(customIssueNameArgument)
          && //
          arg.wasGiven(customIssuePatternArgument)) {
        String title = null;
        if (arg.wasGiven(customIssueTitleArgument)) {
          title = arg.get(customIssueTitleArgument);
        }
        String link = null;
        if (arg.wasGiven(customIssueLinkArgument)) {
          link = arg.get(customIssueLinkArgument);
        }
        changelogApiBuilder.withCustomIssue( //
            arg.get(customIssueNameArgument), //
            arg.get(customIssuePatternArgument), //
            link, //
            title);
      }

      checkArgument( //
          arg.wasGiven(outputStdoutArgument)
              || arg.wasGiven(outputFileArgument)
              || arg.wasGiven(prependToFile)
              || arg.wasGiven(printHighestVersion)
              || arg.wasGiven(printHighestVersionTag)
              || arg.wasGiven(printNextVersion), //
          "You must supply an output, "
              + PARAM_OUTPUT_FILE
              + " <filename>, "
              + PARAM_OUTPUT_STDOUT
              + ", "
              + PARAM_PREPEND_TO_FILE
              + " <filename>, "
              + PARAM_PRINT_HIGHEST_VERSION
              + ", "
              + PARAM_PRINT_NEXT_VERSION);

      if (arg.wasGiven(outputStdoutArgument)) {
        systemOutPrintln(changelogApiBuilder.render());
      }

      if (arg.wasGiven(outputFileArgument)) {
        final String filePath = arg.get(outputFileArgument);
        changelogApiBuilder.toFile(new File(filePath));
      }

      if (arg.wasGiven(majorVersionPattern)) {
        final String major = arg.get(majorVersionPattern);
        changelogApiBuilder.withSemanticMajorVersionPattern(major);
      }

      if (arg.wasGiven(minorVersionPattern)) {
        final String minor = arg.get(minorVersionPattern);
        changelogApiBuilder.withSemanticMinorVersionPattern(minor);
      }

      if (arg.wasGiven(patchVersionPattern)) {
        final String patch = arg.get(patchVersionPattern);
        changelogApiBuilder.withSemanticPatchVersionPattern(patch);
      }

      if (arg.wasGiven(prependToFile)) {
        final String filePath = arg.get(prependToFile);
        changelogApiBuilder.prependToFile(new File(filePath));
      }

      if (arg.wasGiven(showDebugInfo)) {
        System.out.println(
            "Settings:\n"
                + new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(changelogApiBuilder.getSettings()));
        System.out.println("Template:\n\n" + changelogApiBuilder.getTemplateString() + "\n\n");
        final byte[] template =
            changelogApiBuilder.getTemplateString().getBytes(StandardCharsets.UTF_8);
        for (final byte element : template) {
          System.out.format("%02X ", element);
        }
        System.out.println();
      }

      if (arg.wasGiven(printHighestVersion)) {
        final String version = changelogApiBuilder.getHighestSemanticVersion().toString();
        System.out.println(version);
        System.exit(0);
      }

      if (arg.wasGiven(printHighestVersionTag)) {
        final SemanticVersion highestSemanticVersion =
            changelogApiBuilder.getHighestSemanticVersion();
        final String tag = highestSemanticVersion.findTag().orElse("");
        System.out.println(tag);
        System.exit(0);
      }

      if (arg.wasGiven(printNextVersion)) {
        final String version = changelogApiBuilder.getNextSemanticVersion().toString();
        System.out.println(version);
        System.exit(0);
      }

    } catch (final ArgumentException exception) {
      System.out.println(exception.getMessageAndUsage());
      System.exit(1);
    }
  }

  private static void checkArgument(final boolean b, final String string) {
    if (!b) {
      throw new IllegalStateException(string);
    }
  }

  public static String getSystemOutPrintln() {
    return Main.systemOutPrintln;
  }

  public static void recordSystemOutPrintln() {
    Main.recordSystemOutPrintln = true;
  }

  private static void systemOutPrintln(final String systemOutPrintln) {
    if (Main.recordSystemOutPrintln) {
      Main.systemOutPrintln = systemOutPrintln;
    } else {
      System.out.println(systemOutPrintln);
    }
  }
}
