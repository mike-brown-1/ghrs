package ghrs

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int

class Command: CliktCommand(name = "ghrs") {
    val terms: List<String> by argument()
        .help("Terms to search for")
        .multiple()

    val languages: List<String> by option("-l", "--language")
        .help("Primary language. Can be used multiple times.")
        .multiple()

    val sort: String? by option("-s", "--sort")
        .help("Specify sort option.")
        .choice("stars", "forks", "help-wanted-issues", "updated")

    val order: String? by option("-o", "--sortOrder")
        .help("Specify sort order.")
        .choice("asc", "desc")

    val stars by option("--stars")
        .help("""Constrain search based on stars: operator ',' count (example: '>=,200'). 
            |Make sure you quote the option value.""".trimMargin())
        .split(",")

    val limit: Int? by option("--limit")
        .help("Limit the search to x repositories.  Default is 30.")
        .int().default(30)

    val created by option("--created")
        .help("Search by created date (YYYY-MM-DD). Uses operators (see --stars)")
        .split(",")

    val updated by option("--updated")
        .help("Search by updated/pushed date (YYYY-MM-DD). Uses operators (see --stars)")
        .split(",")

    val configFile: String? by option("--config")
        .help("Configuration file that will be overridden by command line options")

    override fun run() {
        var config = Config()
        println("configFile: ${this.configFile}")
        configFile?.let { config = loadConfig(it) }
        config = overrideConfig(config, this)
        println("""running, terms: ${config.terms}, languages: ${config.languages}, sort: ${config.sort}, 
            |sortOrder: ${config.order}
            |
            |created: ${created}, updated: ${updated}""".trimMargin())
        val apiService = ApiService()
        val response = apiService.repoSearch(config)
        println("total results: ${response?.total_count}")
        response?.items?.forEach { repo ->
            println("id: ${repo.id}, name: ${repo.name}, stars: ${repo.stargazers_count}")
        }

//        val repositories = searchPublicRepos(config)
//        var item = 1
//        run {
//            repositories.forEach { repo ->
//                println("\n\nRepository: ${repo.name} by ${repo.owner.login}/${repo.owner.name}/${repo.owner.company}")
//                println("Description: ${repo.description}")
//                println("Stars: ${repo.stargazersCount}, URL: ${repo.htmlUrl}")
//                println("Created: ${repo.createdAt} / Updated: ${repo.updatedAt}")
//                if (limit != null && item++ == limit!!) return@run
//            }
//        }
    }
}

fun main(args: Array<String>) = Command().main(args)
