package ghrs

import org.kohsuke.github.GHDirection
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.PagedSearchIterable
import org.kohsuke.github.GHRepositorySearchBuilder

fun searchPublicRepos(
    config: Config
): PagedSearchIterable<GHRepository> {
    val github = GitHubBuilder.fromEnvironment().build() // Set env GITHUB_OAUTH to personal access token
    try {
        println("Logged in as: ${github.myself.login}")
    } catch (e: IllegalStateException) {
        println("*** WARNING: Environment variable GITHUB_OAUTH not set or invalid ***")
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
    if (config.created != null && config.created!!.length > 0) {
        searchBuilder.created(config.created)
    }
    if (config.updated != null && config.updated!!.length > 0) {
        searchBuilder.pushed(config.updated)
    }
    if (config.sort != null) {
        searchBuilder.sort(GHRepositorySearchBuilder.Sort.valueOf(config.sort!!.uppercase()))
    }
    if (config.order != null) {
        searchBuilder.order(GHDirection.valueOf(config.order!!.uppercase()))
    }
    val repos = searchBuilder.list()
    return repos // toList Does this fetch all?  yes
}
