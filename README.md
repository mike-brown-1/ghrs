# ghsearch

Command line utility to quickly search GitHub public repositories.  Options
are provided to qualify or limit the search and to sort the results.

Uses [GitHub API for Java](https://hub4j.github.io/github-api/) library
to interact with the GitHub REST API and leverage the existing Java POJO
objects.

[CLIKT](https://ajalt.github.io/clikt/) is used to parse the command
line options.  This library is Graalvm friendly as it does not use
reflection.

Developed with Kotlin and Gradle build tool.
