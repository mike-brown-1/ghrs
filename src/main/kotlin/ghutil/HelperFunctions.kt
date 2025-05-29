package ghutil

import org.kohsuke.github.GHDirection
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.PagedSearchIterable
import org.kohsuke.github.GHRepositorySearchBuilder
import kotlin.system.exitProcess

fun searchPublicRepos(
    config: Config
): PagedSearchIterable<GHRepository> {
    val github = GitHubBuilder.fromEnvironment().build() // Set env GITHUB_OAUTH to personal access token
    if (!github.isCredentialValid) {
        println("Missing or invalid GITHUB_OAUTH environment variable")
        exitProcess(20)
    }
    val searchBuilder = github.searchRepositories()
    config.terms.forEach { term ->
        searchBuilder.q(term)
    }
    config.languages.forEach { language  ->
        searchBuilder.language(language)
    }
    if (config.stars != null && config.stars!!.length > 0) {
        searchBuilder.stars(config.stars)
    }
    if (config.sort != null) {
        searchBuilder.sort(GHRepositorySearchBuilder.Sort.valueOf(config.sort!!.uppercase()))
    }
    if (config.order != null) {
        searchBuilder.order(GHDirection.valueOf(config.order!!.uppercase()))
    }
    val repos = searchBuilder.list()
//    println("Found initial ${repos.totalCount} repos")
    return repos // toList Does this fetch all?  yes
}

//private fun starsQualifier(stars: List<String>?): String {
//    // >=200, <100, and <=99, 100..500
//    var result = ""
//    val operators = listOf(">", ">=", "<", "<=")
//    if (stars != null) {
//        if (stars.size == 2) {
//            if (stars[0] in operators && stars[1].toIntOrNull() != null) {
//                result = "${stars[0]}${stars[1]}"
//            } else {
//                println("ERROR: stars parameter must be <operator>,<number of stars>")
//            }
//        } else if (stars.size == 3) {
//            if (stars[0].toIntOrNull() != null && stars[1] == ".." && stars[2].toIntOrNull() != null) {
//                result = "${stars[0]}..${stars[2]}"
//            } else {
//                println("ERROR: for three parts, a range is expected (<num>,..,<num>")
//            }
//        } else {
//            println("Invalid stars param. Must be <op>,number or number,..,number")
//            println("Valid operators are: >, >=, <, <=")
//        }
//    }
//    return result
//}
