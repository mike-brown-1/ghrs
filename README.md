# ghrs 

ghrs (**G**it**H**ub **R**epository **S**earch) is a command line utility to quickly 
search GitHub public repositories.  Options
are provided to filter, limit, and sort the results.

## Authorization
Authentication with GitHub is not required to search public repositories.  It is recommended as it increases the 
limits on searching.  The application will use an environment variable, GH_TOKEN, value for authentication.
This should be set to a GitHub user's personal access token. 

A warning message is displayed if the GH_TOKEN is not set or is invalid.

## Usage
```shell
Usage: ghrs [<options>] [<terms>]...

Options:
  -l, --language=<text>       Primary language. Can be used multiple times.
  -s, --sort=(stars|forks|help-wanted-issues|updated)
                              Specify sort option.
  -o, --sortOrder=(asc|desc)  Specify sort order.
  --stars=<text>              Constrain search based on stars: operator ','
                              count (example: '>=,200'). Make sure you quote
                              the option value.
  --limit=<int>               Limit the search to x repositories
  --created=<text>            Search by created date (YYYY-MM-DD). Uses
                              operators (see --stars)
  --updated=<text>            Search by updated/pushed date (YYYY-MM-DD). Uses
                              operators (see --stars)
  --config=<text>             Configuration file that will be overridden by
                              command line options
  -h, --help                  Show this message and exit

Arguments:
  <terms>  Terms to search for

```

## Default Options

A configuration file can be specified with the `--config` option.  The configuration file can provide some or all
the command line arguments and options.  For example, you can specify that the number of stars is greater than 
or equal to 500 and sort by stars in the descending order.  The the user would specify search terms and, perhaps, 
language of the repository.

The configuration file must be on 
[HOCON](https://github.com/lightbend/config/blob/main/HOCON.md) format.

## Developers

Developed with Kotlin and Gradle build tool.

[CLIKT](https://ajalt.github.io/clikt/) is used to parse the command
line options.  This library is Graalvm friendly as it does not use
reflection.

### Building
Use `./gradlew shadowJar`.

### Detekt

Detekt is a static code analysis took for the Kotlin language.
To generate Detekt rules so you can modify them, run: `./gradlew detektGenerateConfig`.  This will create:
`config/detekt/detekt.yml` so you can modify the rules.

## History
* v.0.8.1 - Fix issues with created & updated options
* v.0.8.0 - Handle rest of the query parameters and qualifiers
* v 0.7.0 - Honors limit option
* v 0.6.0 - Supports filter by created and updated 
* v 0.5.0 - Replace individual properties with Command object for `overrideConfig`. Warn if not logged in.  Modify
detekt rules.
* v 0.4.0 - Support HOCON configuration files.
