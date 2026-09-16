package se.bjurr.gitchangelog.main;

import static se.bjurr.gitchangelog.api.GitChangelogApi.gitChangelogApiBuilder;
import static se.bjurr.gitchangelog.api.GitChangelogApiConstants.DEFAULT_DATEFORMAT;
import static se.bjurr.gitchangelog.internal.settings.Settings.defaultSettings;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;
import se.bjurr.gitchangelog.api.GitChangelogApi;
import se.bjurr.gitchangelog.api.GitChangelogApiConstants;
import se.bjurr.gitchangelog.api.InclusivenessStrategy;
import se.bjurr.gitchangelog.internal.semantic.SemanticVersion;
import se.bjurr.gitchangelog.internal.settings.Settings;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

public class Main {
  private static final String PARAM_REGISTER_HANDLEBARS_HELPER = "-rhh";
  private static final String PARAM_PRINT_HIGHEST_VERSION = "-phv";
  private static final String PARAM_PRINT_HIGHEST_VERSION_TAG = "-phvt";
  private static final String PARAM_PRINT_NEXT_VERSION = "-pnv";
  private static final String PARAM_PRINT_CURRENT_VERSION = "-pcv";
  private static final String PARAM_PATCH_VERSION_PATTERN = "-pavp";
  private static final String PARAM_MINOR_VERSION_PATTERN = "-mivp";
  private static final String PARAM_MAJOR_VERSION_PATTERN = "-mavp";
  private static final String PARAM_PREPEND_TO_FILE = "-ptf";
  public static final String PARAM_SETTINGS_FILE = "-sf";
  public static final String PARAM_OUTPUT_FILE = "-of";
  public static final String PARAM_OUTPUT_STDOUT = "-std";
  public static final String PARAM_TEMPLATE = "-t";
  public static final String PARAM_PREPEND_TEMPLATE = "-pt";
  public static final String PARAM_TEMPLATE_BASE_DIR = "-tbd";
  public static final String PARAM_TEMPLATE_PARTIAL_SUFFIX = "-tps";
  public static final String PARAM_REPO = "-r";
  public static final String PARAM_FROM_REF = "-fr";
  public static final String PARAM_TO_REF = "-tr";
  public static final String PARAM_FROM_REV = "-fre";
  public static final String PARAM_FROM_REV_INCLUDE = "-frei";
  public static final String PARAM_TO_REV = "-tre";
  public static final String PARAM_TO_REV_INCLUDE = "-trei";
  public static final String PARAM_FROM_COMMIT = "-fc";
  public static final String PARAM_TO_COMMIT = "-tc";
  public static final String PARAM_IGNORE_PATTERN = "-ip";
  public static final String PARAM_IGNORE_OLDER_PATTERN = "-iot";
  public static final String PARAM_IGNORE_TAG_PATTERN = "-itp";
  public static final String PARAM_JIRA_SERVER = "-js";
  public static final String PARAM_JIRA_REST_BASE_PATH = "-jrbp";
  public static final String PARAM_JIRA_ISSUE_PATTERN = "-jp";
  public static final String PARAM_JIRA_USERNAME = "-ju";
  public static final String PARAM_JIRA_PASSWORD = "-jpw";
  public static final String PARAM_JIRA_BASIC_AUTH = "-jba";
  public static final String PARAM_JIRA_BEARER = "-jbt";
  public static final String PARAM_JIRA_ADDITIONAL_FIELD = "-jaf";
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
  public static final String PARAM_GITLABISSUEPATTERN = "-glp";
  public static final String PARAM_COMMIT_COUNT = "-cc";

  private static String systemOutPrintln;
  private static boolean recordSystemOutPrintln;

  private static OptionSpec.Builder stringOption(final String... names) {
    return OptionSpec.builder(names).type(String.class);
  }

  private static OptionSpec.Builder flagOption(final String... names) {
    return OptionSpec.builder(names).type(Boolean.class).arity("0");
  }

  private static OptionSpec.Builder repeatedStringOption(final String... names) {
    return OptionSpec.builder(names).type(List.class).auxiliaryTypes(String.class);
  }

  public static void main(final String args[]) throws Exception {
    final Settings defaultSettings = defaultSettings();

    final OptionSpec helpArgument =
        OptionSpec.builder("-h", "--help") //
            .usageHelp(true) //
            .description("Show this help message and exit.") //
            .build();

    final OptionSpec settingsArgument =
        stringOption(PARAM_SETTINGS_FILE, "--settings-file") //
            .description("Use settings from file.") //
            .build();
    final OptionSpec outputStdoutArgument =
        flagOption(PARAM_OUTPUT_STDOUT, "--stdout") //
            .description("Print builder to <STDOUT>.") //
            .build();
    final OptionSpec outputFileArgument =
        stringOption(PARAM_OUTPUT_FILE, "--output-file") //
            .description("Write output to file.") //
            .build();

    final OptionSpec templatePathArgument =
        stringOption(PARAM_TEMPLATE, "--template") //
            .description("Template to use. A default template will be used if not specified.") //
            .defaultValue(defaultSettings.getTemplatePath()) //
            .build();

    final OptionSpec prependTemplatePathArgument =
        stringOption(PARAM_PREPEND_TEMPLATE, "--prepend-template") //
            .description(
                "Template to use when prepending. A default template will be used if not specified.") //
            .defaultValue(defaultSettings.getPrependTemplatePath()) //
            .build();

    final OptionSpec templateBaseDirArgument =
        stringOption(PARAM_TEMPLATE_BASE_DIR, "--template-base-dir") //
            .description("Base dir of templates.") //
            .defaultValue(defaultSettings.getTemplateBaseDir()) //
            .build();

    final OptionSpec templatePartialSuffixArgument =
        stringOption(PARAM_TEMPLATE_PARTIAL_SUFFIX, "--template-partial-suffix") //
            .description("File ending for partials.") //
            .defaultValue(defaultSettings.getTemplateSuffix()) //
            .build();

    final OptionSpec untaggedTagNameArgument =
        stringOption(PARAM_UNTAGGED_TAG_NAME, "--untagged-name") //
            .description(
                "When listing commits per tag, this will by the name of a virtual tag that contains commits not available in any git tag.") //
            .defaultValue(defaultSettings.getUntaggedName()) //
            .build();

    final OptionSpec fromRepoArgument =
        stringOption(PARAM_REPO, "--repo") //
            .description("Repository.") //
            .defaultValue(defaultSettings.getFromRepo()) //
            .build();
    final OptionSpec fromRevArgument =
        stringOption(PARAM_FROM_REV, "--from-revision") //
            .description("From revision.") //
            .defaultValue(defaultSettings.getFromRevision().orElse(null)) //
            .build();
    final OptionSpec fromRevInclusivenessStrategyArgument =
        OptionSpec.builder(PARAM_FROM_REV_INCLUDE, "--from-revision-inclusiveness") //
            .type(InclusivenessStrategy.class) //
            .description("Include, or exclude, specified revision.") //
            .defaultValue(defaultSettings.getFromRevisionStrategy().name()) //
            .build();
    final OptionSpec toRevArgument =
        stringOption(PARAM_TO_REV, "--to-revision") //
            .description("To revision.") //
            .defaultValue(defaultSettings.getToRevision().orElse(null)) //
            .build();
    final OptionSpec toRevInclusivenessStrategyArgument =
        OptionSpec.builder(PARAM_TO_REV_INCLUDE, "--to-revision-inclusiveness") //
            .type(InclusivenessStrategy.class) //
            .description("Include, or exclude, specified revision.") //
            .defaultValue(defaultSettings.getFromRevisionStrategy().name()) //
            .build();
    final OptionSpec fromRefArgument =
        stringOption(PARAM_FROM_REF, "--from-ref") //
            .description("From ref.") //
            .defaultValue(defaultSettings.getFromRevision().orElse(null)) //
            .hidden(true) //
            .build();
    final OptionSpec toRefArgument =
        stringOption(PARAM_TO_REF, "--to-ref") //
            .description("To ref.") //
            .defaultValue(defaultSettings.getToRevision().orElse(null)) //
            .hidden(true) //
            .build();
    final OptionSpec fromCommitArgument =
        stringOption(PARAM_FROM_COMMIT, "--from-commit") //
            .description("From commit.") //
            .defaultValue(defaultSettings.getFromRevision().orElse(null)) //
            .hidden(true) //
            .build();
    final OptionSpec toCommitArgument =
        stringOption(PARAM_TO_COMMIT, "--to-commit") //
            .description("To commit.") //
            .defaultValue(defaultSettings.getToRevision().orElse(null)) //
            .hidden(true) //
            .build();

    final OptionSpec ignoreCommitsIfMessageMatchesArgument =
        stringOption(PARAM_IGNORE_PATTERN, "--ignore-pattern") //
            .description("Ignore commits where pattern matches message.") //
            .defaultValue(defaultSettings.getIgnoreCommitsIfMessageMatches()) //
            .build();

    final OptionSpec ignoreCommitsOlderThanArgument =
        stringOption(PARAM_IGNORE_OLDER_PATTERN, "--ignore-older-than") //
            .description("Ignore commits older than " + DEFAULT_DATEFORMAT + ".") //
            .build();

    final OptionSpec ignoreTagsIfNameMatchesArgument =
        stringOption(PARAM_IGNORE_TAG_PATTERN, "--ignore-tag-pattern") //
            .description(
                "Ignore tags that matches regular expression. Can be used to ignore release candidates and only include actual releases.") //
            .defaultValue(defaultSettings.getIgnoreTagsIfNameMatches().orElse(null)) //
            .build();

    final OptionSpec jiraServerArgument =
        stringOption(PARAM_JIRA_SERVER, "--jiraServer", "--jira-server") //
            .description(
                "Jira server. When a Jira server is given, the title of the Jira issues can be used in the changelog.") //
            .defaultValue(defaultSettings.getJiraServer().orElse(null)) //
            .build();
    final OptionSpec jiraRestBasePathArgument =
        stringOption(PARAM_JIRA_REST_BASE_PATH, "--jira-rest-base-path") //
            .description(
                "REST API base path, appended to the Jira server, used to reach the issue"
                    + " endpoint. Defaults to /rest/api/2 when not set. Some Jira-compatible"
                    + " servers use a different structure, e.g. /rest/api/latest.") //
            .defaultValue(defaultSettings.getJiraRestBasePath().orElse(null)) //
            .build();
    final OptionSpec jiraIssuePatternArgument =
        stringOption(PARAM_JIRA_ISSUE_PATTERN, "--jira-pattern") //
            .description("Jira issue pattern.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final OptionSpec jiraUsernamePatternArgument =
        stringOption(PARAM_JIRA_USERNAME, "--jira-username") //
            .description("Optional username to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final OptionSpec jiraPasswordPatternArgument =
        stringOption(PARAM_JIRA_PASSWORD, "--jira-password") //
            .description("Optional password to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final OptionSpec jiraBasicAuthStringPatternArgument =
        stringOption(PARAM_JIRA_BASIC_AUTH, "--jira-basic-auth") //
            .description("Optional token to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final OptionSpec jiraBearerArgument =
        stringOption(PARAM_JIRA_BEARER, "--jira-bearer") //
            .description("Optional token to authenticate with Jira.") //
            .defaultValue(defaultSettings.getJiraIssuePattern()) //
            .build();
    final OptionSpec jiraAdditionalFieldArgument =
        repeatedStringOption(PARAM_JIRA_ADDITIONAL_FIELD, "--jira-additional-field") //
            .description(
                "Adds an additional field for Jira. When configured, we will return from Jira the result of this field, if it exists.") //
            .build();

    final OptionSpec redmineServerArgument =
        stringOption(PARAM_REDMINE_SERVER, "--redmine-server") //
            .description(
                "Redmine server. When a Redmine server is given, the title of the Redmine issues can be used in the changelog.") //
            .defaultValue(defaultSettings.getRedmineServer().orElse(null)) //
            .build();
    final OptionSpec redmineIssuePatternArgument =
        stringOption(PARAM_REDMINE_ISSUE_PATTERN, "--redmine-pattern") //
            .description("Redmine issue pattern.") //
            .defaultValue(defaultSettings.getRedmineIssuePattern()) //
            .build();
    final OptionSpec redmineUsernameArgument =
        stringOption(PARAM_REDMINE_USERNAME, "--redmine-username") //
            .description("Optional username to authenticate with Redmine.") //
            .build();
    final OptionSpec redminePasswordArgument =
        stringOption(PARAM_REDMINE_PASSWORD, "--redmine-password") //
            .description("Optional password to authenticate with Redmine.") //
            .build();
    final OptionSpec redmineTokenArgument =
        stringOption(PARAM_REDMINE_TOKEN, "--redmine-token") //
            .description("Optional token/api-key to authenticate with Redmine.") //
            .build();

    final OptionSpec customIssueNameArgument =
        stringOption(PARAM_CUSTOM_ISSUE_NAME, "--custom-issue-name") //
            .description("Custom issue name.") //
            .build();
    final OptionSpec customIssuePatternArgument =
        stringOption(PARAM_CUSTOM_ISSUE_PATTERN, "--custom-issue-pattern") //
            .description("Custom issue pattern.") //
            .build();
    final OptionSpec customIssueLinkArgument =
        stringOption(PARAM_CUSTOM_ISSUE_LINK, "--custom-issue-link") //
            .description(
                "Custom issue link. Supports variables like $${PATTERN_GROUP_1} to inject variables from pattern.") //
            .build();
    final OptionSpec customIssueTitleArgument =
        stringOption(PARAM_CUSTOM_ISSUE_TITLE, "--custom-issue-title") //
            .description(
                "Custom issue title. Supports variables like $${PATTERN_GROUP_1} to inject variables from pattern.") //
            .build();

    final OptionSpec timeZoneArgument =
        stringOption(PARAM_TIMEZONE, "--time-zone") //
            .description("TimeZone to use when printing dates.") //
            .defaultValue(defaultSettings.getTimeZone()) //
            .build();
    final OptionSpec dateFormatArgument =
        stringOption(PARAM_DATEFORMAT, "--date-format") //
            .description("Format to use when printing dates.") //
            .defaultValue(defaultSettings.getDateFormat()) //
            .build();
    final OptionSpec noIssueArgument =
        stringOption(PARAM_NOISSUE, "--no-issue-name") //
            .description(
                "Name of virtual issue that contains commits that has no issue associated.") //
            .defaultValue(defaultSettings.getNoIssueName()) //
            .build();
    final OptionSpec ignoreCommitsWithoutIssueArgument =
        flagOption(PARAM_IGNORE_NOISSUE, "--ignore-commits-without-issue") //
            .description("Ignore commits that is not included in any issue.") //
            .build();
    final OptionSpec readableTagNameArgument =
        stringOption(PARAM_READABLETAGNAME, "--readable-tag-name") //
            .description("Pattern to extract readable part of tag.") //
            .defaultValue(defaultSettings.getReadableTagName()) //
            .build();
    final OptionSpec removeIssueFromMessageArgument =
        flagOption(PARAM_REMOVEISSUE, "--remove-issue-from-message") //
            .description("Dont print any issues in the messages of commits.") //
            .build();

    final OptionSpec gitHubApiArgument =
        stringOption(PARAM_GITHUBAPI, "--github-api") //
            .description(
                "GitHub API. Like: https://api.github.com/repos/tomasbjerre/git-changelog-command-line/") //
            .defaultValue("") //
            .build();
    final OptionSpec gitHubTokenArgument =
        stringOption(PARAM_GITHUBTOKEN, "--github-token") //
            .description(
                "GitHub API OAuth2 token. You can get it from: curl -u 'yourgithubuser' -d '{\"note\":\"Git Changelog Lib\"}' https://api.github.com/authorizations") //
            .defaultValue("") //
            .build();

    final OptionSpec extendedVariablesArgument =
        stringOption(PARAM_EXTENDED_VARIABLES, "--extended-variables") //
            .description(
                "Extended variables that will be available as {{extended.*}}. "
                    + PARAM_EXTENDED_VARIABLES
                    + " \"{\\\"var1\\\": \\\"val1\\\"}\" will print out \"val1\" for a template like \"{{extended.var1}}\"") //
            .defaultValue("") //
            .build();

    final OptionSpec extendedHeadersArgument =
        repeatedStringOption(PARAM_EXTENDED_HEADERS, "--extended-headers") //
            .description(
                "Extended headers that will send when access JIRA. e.g. "
                    + PARAM_EXTENDED_HEADERS
                    + " CF-Access-Client-ID:abcde12345xyz.access") //
            .build();

    final OptionSpec templateContentArgument =
        stringOption(PARAM_TEMPLATE_CONTENT, "--template-content") //
            .description("String to use as template.") //
            .defaultValue("") //
            .build();

    final OptionSpec gitLabTokenArgument =
        stringOption(PARAM_GITLABTOKEN, "--gitlab-token") //
            .description("GitLab API token.") //
            .defaultValue("") //
            .build();
    final OptionSpec gitLabServerArgument =
        stringOption(PARAM_GITLABSERVER, "--gitlab-server") //
            .description("GitLab server, like https://gitlab.com/.") //
            .defaultValue("") //
            .build();
    final OptionSpec gitLabProjectNameArgument =
        stringOption(PARAM_GITLABPROJECTNAME, "--gitlab-project-name") //
            .description("GitLab project name.") //
            .defaultValue("") //
            .build();
    final OptionSpec gitLabProjectIssuePattern =
        stringOption(PARAM_GITLABISSUEPATTERN, "--gitlab-issue-pattern") //
            .description("GitLab issue pattern.") //
            .defaultValue("") //
            .build();

    final OptionSpec printHighestVersion =
        flagOption(PARAM_PRINT_HIGHEST_VERSION, "--print-highest-version") //
            .description("Print the highest version, determined by tags in repo, and exit.") //
            .defaultValue("false")
            .build();

    final OptionSpec printHighestVersionTag =
        flagOption(PARAM_PRINT_HIGHEST_VERSION_TAG, "--print-highest-version-tag") //
            .description("Print the tag corresponding to highest version, and exit.") //
            .defaultValue("false")
            .build();

    final OptionSpec printNextVersion =
        flagOption(PARAM_PRINT_NEXT_VERSION, "--print-next-version") //
            .description(
                "Print the next version, determined by commits since highest version, and exit.") //
            .defaultValue("false")
            .build();

    final OptionSpec printCurrentVersion =
        flagOption(PARAM_PRINT_CURRENT_VERSION, "--print-current-version") //
            .description(
                "Like --print-next-version unless the current commit is tagged with a version, if so it will print that version.") //
            .defaultValue("false")
            .build();

    final OptionSpec registerHandlebarsHelper =
        stringOption(PARAM_REGISTER_HANDLEBARS_HELPER, "--register-handlebars-helper") //
            .description(
                "Handlebar helpers, https://handlebarsjs.com/guide/block-helpers.html, to register and use in given template.") //
            .defaultValue("")
            .build();

    final OptionSpec handlebarsHelperFile =
        OptionSpec.builder("-handlebars-helper-file", "-hhf") //
            .type(File.class) //
            .description("Can be used to add extra helpers.") //
            .build();

    final OptionSpec prependToFile =
        stringOption(PARAM_PREPEND_TO_FILE, "--prepend-to-file") //
            .description("Add the changelog to top of given file.") //
            .build();

    final OptionSpec majorVersionPattern =
        stringOption(PARAM_MAJOR_VERSION_PATTERN, "--major-version-pattern") //
            .description(
                "Commit messages matching this, optional, regular expression will trigger new major version.") //
            .build();

    final OptionSpec minorVersionPattern =
        stringOption(PARAM_MINOR_VERSION_PATTERN, "--minor-version-pattern") //
            .description(
                "Commit messages matching this, optional, regular expression will trigger new minor version.") //
            .defaultValue(GitChangelogApiConstants.DEFAULT_MINOR_PATTERN)
            .build();

    final OptionSpec patchVersionPattern =
        stringOption(PARAM_PATCH_VERSION_PATTERN, "--patch-version-pattern") //
            .description(
                "Commit messages matching this, optional, regular expression will trigger new patch version.") //
            .defaultValue(GitChangelogApiConstants.DEFAULT_PATCH_PATTERN)
            .build();

    final OptionSpec showDebugInfo =
        flagOption("--show-debug-info")
            .description(
                "Please run your command with this parameter and supply output when reporting bugs.")
            .build();

    final OptionSpec jiraEnabledArgument =
        flagOption("-je", "--jira-enabled") //
            .description("Enable parsing for Jira issues.") //
            .build();

    final OptionSpec githubEnabledArgument =
        flagOption("-ge", "--github-enabled") //
            .description("Enable parsing for GitHub issues.") //
            .build();

    final OptionSpec gitlabEnabledArgument =
        flagOption("-gl", "--gitlab-enabled") //
            .description("Enable parsing for GitLab issues.") //
            .build();

    final OptionSpec redmineEnabledArgument =
        flagOption("-re", "--redmine-enabled") //
            .description("Enable parsing for Redmine issues.") //
            .build();

    final OptionSpec useIntegrationsArgument =
        flagOption("-ui", "--use-integrations") //
            .description("Use integrations to get more details on commits.") //
            .build();

    final OptionSpec encodingArgument =
        stringOption("-en", "--encoding") //
            .description("Encoding to use when writing content.") //
            .defaultValue(StandardCharsets.UTF_8.name())
            .build();

    final OptionSpec pathsArgument =
        repeatedStringOption("-pf", "--path-filters") //
            .description("Paths on the filesystem to filter on.") //
            .build();

    final OptionSpec commitCountArgument =
        flagOption(PARAM_COMMIT_COUNT, "--commit-count") //
            .description(
                "Compute each commit's ancestor count (equivalent to \"git rev-list --count"
                    + " <hash>\"), exposed to templates as {{commitCount}}. Off by default: it is"
                    + " O(depth) per commit and can be slow on large histories.") //
            .build();

    final CommandSpec spec = CommandSpec.create().name("git-changelog-command-line");
    spec.addOption(helpArgument);
    spec.addOption(settingsArgument);
    spec.addOption(outputStdoutArgument);
    spec.addOption(outputFileArgument);
    spec.addOption(templatePathArgument);
    spec.addOption(prependTemplatePathArgument);
    spec.addOption(templateBaseDirArgument);
    spec.addOption(templatePartialSuffixArgument);
    spec.addOption(fromCommitArgument);
    spec.addOption(fromRevArgument);
    spec.addOption(toRevArgument);
    spec.addOption(toRevInclusivenessStrategyArgument);
    spec.addOption(fromRevInclusivenessStrategyArgument);
    spec.addOption(fromRefArgument);
    spec.addOption(fromRepoArgument);
    spec.addOption(toCommitArgument);
    spec.addOption(toRefArgument);
    spec.addOption(untaggedTagNameArgument);
    spec.addOption(jiraIssuePatternArgument);
    spec.addOption(jiraServerArgument);
    spec.addOption(jiraRestBasePathArgument);
    spec.addOption(redmineIssuePatternArgument);
    spec.addOption(redmineServerArgument);
    spec.addOption(ignoreCommitsIfMessageMatchesArgument);
    spec.addOption(ignoreCommitsOlderThanArgument);
    spec.addOption(customIssueLinkArgument);
    spec.addOption(customIssueTitleArgument);
    spec.addOption(customIssueNameArgument);
    spec.addOption(customIssuePatternArgument);
    spec.addOption(timeZoneArgument);
    spec.addOption(dateFormatArgument);
    spec.addOption(noIssueArgument);
    spec.addOption(readableTagNameArgument);
    spec.addOption(removeIssueFromMessageArgument);
    spec.addOption(gitHubApiArgument);
    spec.addOption(jiraUsernamePatternArgument);
    spec.addOption(jiraPasswordPatternArgument);
    spec.addOption(jiraBasicAuthStringPatternArgument);
    spec.addOption(jiraBearerArgument);
    spec.addOption(jiraAdditionalFieldArgument);
    spec.addOption(redmineUsernameArgument);
    spec.addOption(redminePasswordArgument);
    spec.addOption(redmineTokenArgument);
    spec.addOption(extendedVariablesArgument);
    spec.addOption(extendedHeadersArgument);
    spec.addOption(templateContentArgument);
    spec.addOption(gitHubTokenArgument);
    spec.addOption(ignoreCommitsWithoutIssueArgument);
    spec.addOption(ignoreTagsIfNameMatchesArgument);
    spec.addOption(gitLabTokenArgument);
    spec.addOption(gitLabServerArgument);
    spec.addOption(gitLabProjectNameArgument);
    spec.addOption(gitLabProjectIssuePattern);
    spec.addOption(printHighestVersion);
    spec.addOption(printHighestVersionTag);
    spec.addOption(printNextVersion);
    spec.addOption(printCurrentVersion);
    spec.addOption(registerHandlebarsHelper);
    spec.addOption(prependToFile);
    spec.addOption(majorVersionPattern);
    spec.addOption(minorVersionPattern);
    spec.addOption(patchVersionPattern);
    spec.addOption(showDebugInfo);
    spec.addOption(handlebarsHelperFile);
    spec.addOption(jiraEnabledArgument);
    spec.addOption(githubEnabledArgument);
    spec.addOption(gitlabEnabledArgument);
    spec.addOption(redmineEnabledArgument);
    spec.addOption(useIntegrationsArgument);
    spec.addOption(encodingArgument);
    spec.addOption(pathsArgument);
    spec.addOption(commitCountArgument);

    final CommandLine commandLine = new CommandLine(spec);

    try {
      final ParseResult arg = commandLine.parseArgs(args);

      if (arg.isUsageHelpRequested()) {
        commandLine.usage(System.out); // NOPMD
        System.exit(0);
      }

      final GitChangelogApi changelogApiBuilder =
          gitChangelogApiBuilder()
              .withUseIntegrations(arg.hasMatchedOption(useIntegrationsArgument))
              .withJiraEnabled(arg.hasMatchedOption(jiraEnabledArgument))
              .withRedmineEnabled(arg.hasMatchedOption(redmineEnabledArgument))
              .withGitHubEnabled(arg.hasMatchedOption(githubEnabledArgument))
              .withGitLabEnabled(arg.hasMatchedOption(gitlabEnabledArgument))
              .withCommitCount(arg.hasMatchedOption(commitCountArgument))
              .withEncoding(Charset.forName(encodingArgument.getValue()));

      final List<String> pathFilters = pathsArgument.getValue();
      if (pathFilters != null) {
        changelogApiBuilder.withPathFilters(pathFilters.toArray(new String[0]));
      }

      final String registerHandlebarsHelperValue = registerHandlebarsHelper.getValue();
      if (!registerHandlebarsHelperValue.trim().isEmpty()) {
        changelogApiBuilder.withHandlebarsHelper(registerHandlebarsHelperValue);
      }

      if (arg.hasMatchedOption(handlebarsHelperFile)) {
        final File helperFile = handlebarsHelperFile.getValue();
        final byte[] content = Files.readAllBytes(helperFile.toPath());
        final String contentString = new String(content, StandardCharsets.UTF_8);
        changelogApiBuilder.withHandlebarsHelper(contentString);
      }

      if (arg.hasMatchedOption(settingsArgument)) {
        final String settingsFile = settingsArgument.getValue();
        changelogApiBuilder.withSettings(new File(settingsFile).toURI().toURL());
      }

      if (arg.hasMatchedOption(removeIssueFromMessageArgument)) {
        changelogApiBuilder.withRemoveIssueFromMessageArgument(true);
      }
      if (arg.hasMatchedOption(ignoreCommitsWithoutIssueArgument)) {
        changelogApiBuilder.withIgnoreCommitsWithoutIssue(true);
      }

      if (arg.hasMatchedOption(extendedVariablesArgument)) {
        final String jsonString = extendedVariablesArgument.getValue();
        final JsonMapper jsonMapper = JsonMapper.builder().build();
        final Map<String, Object> jsonObject =
            jsonMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        final Map<String, Object> extendedVariables = new HashMap<>();
        extendedVariables.put("extended", jsonObject);
        changelogApiBuilder.withExtendedVariables(extendedVariables);
      }

      if (arg.hasMatchedOption(extendedHeadersArgument)) {
        final List<String> extendedHeaders = extendedHeadersArgument.getValue();
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

      if (arg.hasMatchedOption(templateContentArgument)) {
        changelogApiBuilder.withTemplateContent(templateContentArgument.getValue());
      }

      if (arg.hasMatchedOption(templateBaseDirArgument)) {
        changelogApiBuilder.withTemplateBaseDir(templateBaseDirArgument.getValue());
      }

      if (arg.hasMatchedOption(templatePartialSuffixArgument)) {
        changelogApiBuilder.withTemplateSuffix(templatePartialSuffixArgument.getValue());
      }

      if (arg.hasMatchedOption(fromRepoArgument)) {
        final String fromRepo = fromRepoArgument.getValue();
        changelogApiBuilder.withFromRepo(fromRepo);
      }
      if (arg.hasMatchedOption(untaggedTagNameArgument)) {
        changelogApiBuilder.withUntaggedName(untaggedTagNameArgument.getValue());
      }
      if (arg.hasMatchedOption(ignoreCommitsIfMessageMatchesArgument)) {
        changelogApiBuilder.withIgnoreCommitsWithMessage(
            ignoreCommitsIfMessageMatchesArgument.getValue());
      }
      if (arg.hasMatchedOption(ignoreCommitsOlderThanArgument)) {
        final Date date =
            new SimpleDateFormat(DEFAULT_DATEFORMAT) // NOPMD
                .parse(ignoreCommitsOlderThanArgument.getValue());
        changelogApiBuilder.withIgnoreCommitsOlderThan(date);
      }
      if (arg.hasMatchedOption(ignoreTagsIfNameMatchesArgument)) {
        changelogApiBuilder.withIgnoreTagsIfNameMatches(ignoreTagsIfNameMatchesArgument.getValue());
      }
      if (arg.hasMatchedOption(templatePathArgument)) {
        changelogApiBuilder.withTemplatePath(templatePathArgument.getValue());
      }
      if (arg.hasMatchedOption(prependTemplatePathArgument)) {
        changelogApiBuilder.withPrependTemplatePath(prependTemplatePathArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraIssuePatternArgument)) {
        changelogApiBuilder.withJiraIssuePattern(jiraIssuePatternArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraServerArgument)) {
        changelogApiBuilder.withJiraServer(jiraServerArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraRestBasePathArgument)) {
        changelogApiBuilder.withJiraRestBasePath(jiraRestBasePathArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraUsernamePatternArgument)) {
        changelogApiBuilder.withJiraUsername(jiraUsernamePatternArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraPasswordPatternArgument)) {
        changelogApiBuilder.withJiraPassword(jiraPasswordPatternArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraBasicAuthStringPatternArgument)) {
        changelogApiBuilder.withJiraBasicAuthString(jiraBasicAuthStringPatternArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraBearerArgument)) {
        changelogApiBuilder.withJiraBearer(jiraBearerArgument.getValue());
      }
      if (arg.hasMatchedOption(jiraAdditionalFieldArgument)) {
        final List<String> jiraAdditionalFields = jiraAdditionalFieldArgument.getValue();
        jiraAdditionalFields.forEach(changelogApiBuilder::withJiraIssueAdditionalField);
      }
      if (arg.hasMatchedOption(redmineIssuePatternArgument)) {
        changelogApiBuilder.withRedmineIssuePattern(redmineIssuePatternArgument.getValue());
      }
      if (arg.hasMatchedOption(redmineServerArgument)) {
        changelogApiBuilder.withRedmineServer(redmineServerArgument.getValue());
      }
      if (arg.hasMatchedOption(redmineUsernameArgument)) {
        changelogApiBuilder.withRedmineUsername(redmineUsernameArgument.getValue());
      }
      if (arg.hasMatchedOption(redminePasswordArgument)) {
        changelogApiBuilder.withRedminePassword(redminePasswordArgument.getValue());
      }
      if (arg.hasMatchedOption(redmineTokenArgument)) {
        changelogApiBuilder.withRedmineToken(redmineTokenArgument.getValue());
      }
      if (arg.hasMatchedOption(timeZoneArgument)) {
        changelogApiBuilder.withTimeZone(timeZoneArgument.getValue());
      }
      if (arg.hasMatchedOption(dateFormatArgument)) {
        changelogApiBuilder.withDateFormat(dateFormatArgument.getValue());
      }
      if (arg.hasMatchedOption(noIssueArgument)) {
        changelogApiBuilder.withNoIssueName(noIssueArgument.getValue());
      }
      if (arg.hasMatchedOption(readableTagNameArgument)) {
        changelogApiBuilder.withReadableTagName(readableTagNameArgument.getValue());
      }

      if (arg.hasMatchedOption(fromRevArgument)) {
        if (arg.hasMatchedOption(fromRevInclusivenessStrategyArgument)) {
          changelogApiBuilder.withFromRevision(
              fromRevArgument.getValue(), fromRevInclusivenessStrategyArgument.getValue());
        } else {
          changelogApiBuilder.withFromRevision(fromRevArgument.getValue());
        }
      }
      if (arg.hasMatchedOption(toRevArgument)) {
        if (arg.hasMatchedOption(toRevInclusivenessStrategyArgument)) {
          changelogApiBuilder.withToRevision(
              toRevArgument.getValue(), toRevInclusivenessStrategyArgument.getValue());
        } else {
          changelogApiBuilder.withToRevision(toRevArgument.getValue());
        }
      }
      if (arg.hasMatchedOption(fromCommitArgument)) {
        changelogApiBuilder.withFromCommit(fromCommitArgument.getValue());
      }
      if (arg.hasMatchedOption(fromRefArgument)) {
        changelogApiBuilder.withFromRef(fromRefArgument.getValue());
      }
      if (arg.hasMatchedOption(toCommitArgument)) {
        changelogApiBuilder.withToCommit(toCommitArgument.getValue());
      }
      if (arg.hasMatchedOption(toRefArgument)) {
        changelogApiBuilder.withToRef(toRefArgument.getValue());
      }
      if (arg.hasMatchedOption(gitHubApiArgument)) {
        changelogApiBuilder.withGitHubApi(gitHubApiArgument.getValue());
      }
      if (arg.hasMatchedOption(gitHubTokenArgument)) {
        changelogApiBuilder.withGitHubToken(gitHubTokenArgument.getValue());
      }

      if (arg.hasMatchedOption(gitLabServerArgument)) {
        changelogApiBuilder.withGitLabServer(gitLabServerArgument.getValue());
      }
      if (arg.hasMatchedOption(gitLabProjectNameArgument)) {
        changelogApiBuilder.withGitLabProjectName(gitLabProjectNameArgument.getValue());
      }
      if (arg.hasMatchedOption(gitLabTokenArgument)) {
        changelogApiBuilder.withGitLabToken(gitLabTokenArgument.getValue());
      }
      if (arg.hasMatchedOption(gitLabProjectIssuePattern)) {
        changelogApiBuilder.withGitLabIssuePattern(gitLabProjectIssuePattern.getValue());
      }

      if ( //
      arg.hasMatchedOption(customIssueNameArgument)
          && //
          arg.hasMatchedOption(customIssuePatternArgument)) {
        String title = null;
        if (arg.hasMatchedOption(customIssueTitleArgument)) {
          title = customIssueTitleArgument.getValue();
        }
        String link = null;
        if (arg.hasMatchedOption(customIssueLinkArgument)) {
          link = customIssueLinkArgument.getValue();
        }
        changelogApiBuilder.withCustomIssue( //
            customIssueNameArgument.getValue(), //
            customIssuePatternArgument.getValue(), //
            link, //
            title);
      }

      checkArgument( //
          arg.hasMatchedOption(outputStdoutArgument)
              || arg.hasMatchedOption(outputFileArgument)
              || arg.hasMatchedOption(prependToFile)
              || arg.hasMatchedOption(printHighestVersion)
              || arg.hasMatchedOption(printHighestVersionTag)
              || arg.hasMatchedOption(printNextVersion)
              || arg.hasMatchedOption(printCurrentVersion), //
          "You must supply an output, "
              + PARAM_OUTPUT_FILE
              + " <filename>, "
              + PARAM_OUTPUT_STDOUT
              + ", "
              + PARAM_PREPEND_TO_FILE
              + " <filename>, "
              + PARAM_PRINT_HIGHEST_VERSION
              + ", "
              + PARAM_PRINT_NEXT_VERSION
              + ", "
              + PARAM_PRINT_CURRENT_VERSION);

      if (arg.hasMatchedOption(outputStdoutArgument)) {
        systemOutPrintln(changelogApiBuilder.render());
      }

      if (arg.hasMatchedOption(outputFileArgument)) {
        final String filePath = outputFileArgument.getValue();
        changelogApiBuilder.toFile(new File(filePath));
      }

      if (arg.hasMatchedOption(majorVersionPattern)) {
        final String major = majorVersionPattern.getValue();
        changelogApiBuilder.withSemanticMajorVersionPattern(major);
      }

      if (arg.hasMatchedOption(minorVersionPattern)) {
        final String minor = minorVersionPattern.getValue();
        changelogApiBuilder.withSemanticMinorVersionPattern(minor);
      }

      if (arg.hasMatchedOption(patchVersionPattern)) {
        final String patch = patchVersionPattern.getValue();
        changelogApiBuilder.withSemanticPatchVersionPattern(patch);
      }

      if (arg.hasMatchedOption(prependToFile)) {
        final String filePath = prependToFile.getValue();
        changelogApiBuilder.prependToFile(new File(filePath));
      }

      if (arg.hasMatchedOption(showDebugInfo)) {
        System.out.println( // NOPMD
            "Settings:\n" + changelogApiBuilder.getSettings().toJson());
        System.out.println( // NOPMD
            "Template:\n\n" + changelogApiBuilder.getTemplateString() + "\n\n"); // NOPMD
        final byte[] template =
            changelogApiBuilder.getTemplateString().getBytes(StandardCharsets.UTF_8);
        for (final byte element : template) {
          System.out.format("%02X ", element);
        }
        System.out.println(); // NOPMD
      }

      if (arg.hasMatchedOption(printHighestVersion)) {
        final String version = changelogApiBuilder.getHighestSemanticVersion().toString();
        System.out.println(version); // NOPMD
        System.exit(0);
      }

      if (arg.hasMatchedOption(printHighestVersionTag)) {
        final SemanticVersion highestSemanticVersion =
            changelogApiBuilder.getHighestSemanticVersion();
        final String tag = highestSemanticVersion.findTag().orElse("");
        System.out.println(tag); // NOPMD
        System.exit(0);
      }

      if (arg.hasMatchedOption(printNextVersion)) {
        final String version = changelogApiBuilder.getNextSemanticVersion().toString();
        System.out.println(version); // NOPMD
        System.exit(0);
      }

      if (arg.hasMatchedOption(printCurrentVersion)) {
        final String version = changelogApiBuilder.getCurrentSemanticVersion().toString();
        System.out.println(version); // NOPMD
        System.exit(0);
      }

    } catch (final ParameterException exception) {
      System.out.println(exception.getMessage()); // NOPMD
      exception.getCommandLine().usage(System.out);
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
      System.out.println(systemOutPrintln); // NOPMD
    }
  }
}
