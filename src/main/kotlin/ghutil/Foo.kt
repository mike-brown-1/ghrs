package ghutil

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

fun searchGitHubRepositories(
    searchTerms: List<String>,
    token: String? = null
): String {
    // Build the search query
    val queryBuilder = StringBuilder()

    // Add search terms to be found in description or readme
    searchTerms.forEach { term ->
        queryBuilder.append("${URLEncoder.encode(term, "UTF-8")} ")
    }

    // Add language filter for Java OR Kotlin
    queryBuilder.append("language:java language:kotlin")

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

    return response.body?.string() ?: ""
}

// Example usage
fun main() {
    val searchTerms = listOf("machine learning", "android")
    val results = searchGitHubRepositories(searchTerms)

    // Parse and process the JSON response
    val jsonResponse = JSONObject(results)
    val totalCount = jsonResponse.getInt("total_count")
    val items = jsonResponse.getJSONArray("items")

    println("Found $totalCount repositories")

    // Process the results
    for (i in 0 until items.length()) {
        val repo = items.getJSONObject(i)
        println("${repo.getString("name")} by ${repo.getJSONObject("owner").getString("login")}")
        println("Description: ${repo.optString("description", "No description")}")
        println("Stars: ${repo.getInt("stargazers_count")}")
        println("URL: ${repo.getString("html_url")}")
        println("-".repeat(50))
    }
}

