package ghutil

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.kohsuke.github.GHDirection
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GHRepositorySearchBuilder
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.PagedSearchIterable
import java.net.URLEncoder
import kotlin.system.exitProcess

fun searchPublicRepos(terms: List<String>, languages: List<String>): PagedSearchIterable<GHRepository> {
    val github = GitHubBuilder.fromEnvironment().build() // Set env GITHUB_OAUTH to personal access token
    if (!github.isCredentialValid) {
        println("Missing or invalid GITHUB_OAUTH environment variable")
        exitProcess(20)
    }
    val searchBuilder = github.searchRepositories()
    terms.forEach { term ->
        searchBuilder.q(term)
    }
    languages.forEach { language  ->
        searchBuilder.language(language)
    }
//        .stars(">=150")
//        .sort(GHRepositorySearchBuilder.Sort.STARS)
//        .order(GHDirection.DESC)
    val repos = searchBuilder.list()
    repos.withPageSize(30)
    println("Found initial ${repos.totalCount} repos")
    return repos // toList Does this fetch all?  yes
    // TODO review source of PagedSearchIterable to see if we can limit results returned
}

// TODO update to handle sort and order options
//fun searchGitHubRepositories(
//    searchTerms: List<String>,
//    qualifiers: String,
////    languages: List<String>,
//    token: String? = null
//): List<Repository> {
//    val result = mutableListOf<Repository>()
//    val logger = KotlinLogging.logger {}
//    val queryBuilder = StringBuilder()
//
//    // Add search terms to be found in description or readme(?)
//    searchTerms.forEach { term ->
//        queryBuilder.append("${URLEncoder.encode(term, "UTF-8")} ")
//    }
//    queryBuilder.append(qualifiers)
//
//    // Add language filter if any
////    languages.forEach { language ->
////        queryBuilder.append(" language:$language")
////    }
//
//    // sort options:
//    // Sorts the results of your query by number of stars , forks , or help-wanted-issues or how recently
//    //the items were updated . Default: best match
//
//    // Build the API URL with the encoded query
//    val apiUrl = "https://api.github.com/search/repositories?q=${queryBuilder.toString().trim()}&sort=stars&order=desc"
//
//    // Create OkHttp client
//    val client = OkHttpClient()
//
//    // Build the request
//    val requestBuilder = Request.Builder()
//        .url(apiUrl)
//        .header("Accept", "application/vnd.github.v3+json")
//
//    // Add authorization if token is provided
//    if (token != null) {
//        requestBuilder.header("Authorization", "token $token")
//    }
//
//    val request = requestBuilder.build()
//
//    // Execute the request
//    val response = client.newCall(request).execute()
//    val body = response.body?.string() ?: ""
//    logger.info { "headers:\n${response.headers.toString()}\n\n"}
//    logger.info { "body:\n$body"}
//
//    val jsonResponse = JSONObject(body)
//    val totalCount = jsonResponse.getInt("total_count")
//    val items = jsonResponse.getJSONArray("items")
//
//    for (i in 0 until items.length()) {
//        val repo = items.getJSONObject(i)
//        result.add(Repository(repo.getString("name"),
//            repo.getJSONObject("owner").getString("login"),
//            repo.optString("description", "No description"),
//            repo.getInt("stargazers_count"), repo.getString("html_url")))
//    }
//    return result
//}

fun collectQualifiers(stars: List<String>?, languages: List<String>): String {
    val collection = StringBuilder()
    collection.append(starsQualifier(stars))
    collection.append(languageQualifier(languages))

    return collection.toString()
}

private fun starsQualifier(stars: List<String>?): String {
    // >=200, <100, and <=99, 100..500
    var result = ""
    val operators = listOf(">", ">=", "<", "<=")
    if (stars != null) {
        if (stars.size == 2) {
            if (stars[0] in operators && stars[1].toIntOrNull() != null) {
                result = " stars:${stars[0]}${stars[1]}"
            }
            println("stars parameter must be <operator>,<number of stars>")
        } else if (stars.size == 3) {
            if (stars[0].toIntOrNull() != null && stars[1] == ".." && stars[2].toIntOrNull() != null) {
                result = " stars:${stars[0]}..${stars[2]}"
            }
        } else {
            println("Invalid stars param. Must be <op>,number or number,..,number")
            println("Valid operators are: >, >=, <, <=")
        }
    }
    return result
}

private fun languageQualifier(languages: List<String>): String {
    var result = ""

    if (languages.size > 0) {
        val queryBuilder = StringBuilder()
        languages.forEach { language ->
            queryBuilder.append(" language:$language")
        }
        result = queryBuilder.toString()
    }

    return result
}
