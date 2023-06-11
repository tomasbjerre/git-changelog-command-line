FROM openjdk:17
COPY git-changelog-command-line.jar /usr/local/bin/git-changelog-command-line.jar
COPY git-changelog-command-line /usr/local/bin/git-changelog-command-line
WORKDIR /usr/src/git-changelog-command-line
RUN chmod +x /usr/local/bin/git-changelog-command-line
ENTRYPOINT ["git-changelog-command-line"]