package ghutil

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

// TODO update to handle sort and order options
fun searchGitHubRepositories(
    searchTerms: List<String>,
    languages: List<String>,
    token: String? = null
): List<Repository> {
    val result = mutableListOf<Repository>()
    val logger = KotlinLogging.logger {}
    val queryBuilder = StringBuilder()

    // Add search terms to be found in description or readme(?)
    searchTerms.forEach { term ->
        queryBuilder.append("${URLEncoder.encode(term, "UTF-8")} ")
    }

    // Add language filter if any
    languages.forEach { language ->
        queryBuilder.append(" language:$language")
    }

    // sort options:
    // Sorts the results of your query by number of stars , forks , or help-wanted-issues or how recently
    //the items were updated . Default: best match

    // Build the API URL with the encoded query
    val apiUrl = "https://api.github.com/search/repositories?q=${queryBuilder.toString().trim()}&sort=stars&order=desc"

    // Create OkHttp client
    val client = OkHttpClient()

    // Build the request
    val requestBuilder = Request.Builder()
        .url(apiUrl)
        .header("Accept", "application/vnd.github.v3+json")

    // Add authorization if token is provided
    if (token != null) {
        requestBuilder.header("Authorization", "token $token")
    }

    val request = requestBuilder.build()

    // Execute the request
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: ""
    logger.info { "headers:\n${response.headers.toString()}\n\n"}
    logger.info { "body:\n$body"}

    val jsonResponse = JSONObject(body)
    val totalCount = jsonResponse.getInt("total_count")
    val items = jsonResponse.getJSONArray("items")

    for (i in 0 until items.length()) {
        val repo = items.getJSONObject(i)
        result.add(Repository(repo.getString("name"),
            repo.getJSONObject("owner").getString("login"),
            repo.optString("description", "No description"),
            repo.getInt("stargazers_count"), repo.getString("html_url")))
    }
    return result
}
