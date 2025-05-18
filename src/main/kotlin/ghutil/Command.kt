package ghutil

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.choice
import java.lang.StringBuilder
import kotlin.system.exitProcess

data class Repository(val name: String, val owner: String, val description: String,
    val stars: Int, val url: String)

class Command: CliktCommand(name = "ghsearch") {
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
        .help("Constrain search based on stars (>=,200)")
        .split(",")

    override fun run() {
        println("running, terms: ${terms}, languages: ${languages}, sort: ${sort}, sortOrder: ${order}")
        if (terms.size == 0) {
            echo("You must provide a term to search for", trailingNewline = true, err = true)
            exitProcess(10)
        }
        val qualifiers = collectQualifiers(stars, languages)

//        println("stars: ${stars}, size: ${stars?.size}")

        val repositories = searchGitHubRepositories(terms, qualifiers)
        repositories.forEach { repo ->
            println("\n\nRepository: ${repo.name} by ${repo.owner}")
            println("Description: ${repo.description}")
            println("Stars: ${repo.stars}, URL: ${repo.url}")
        }
    }
}

fun main(args: Array<String>) = Command().main(args)
