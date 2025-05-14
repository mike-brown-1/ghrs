package ghutil

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class GitHubRepositorySearch {
    private val client = OkHttpClient()
    private val baseUrl = "https://api.github.com/search/repositories"

    fun searchRepositories(): List<Repository> {
        // Construct the search query
        val terms = "graphql"
//        val query = "graphql+language:java+language:kotlin&sort=stars&order=desc"
        val encodedQuery = URLEncoder.encode(terms, "UTF-8")
        val query = "${encodedQuery}+language:java+language:kotlin&sort=stars&order=desc"

        // Create the request
        val request = Request.Builder()
            .url("$baseUrl?q=$query")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .build()

        // Execute the request
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                // Parse the response
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                val jsonResponse = JSONObject(responseBody)
                val itemsArray = jsonResponse.getJSONArray("items")

                // Convert JSON to Repository objects
                return (0 until itemsArray.length()).map { index ->
                    val item = itemsArray.getJSONObject(index)
                    Repository(
                        name = item.getString("full_name"),
                        description = item.getString("description"),
                        stars = item.getInt("stargazers_count"),
                        language = item.getString("language"),
                        url = item.getString("html_url")
                    )
                }
            }
        } catch (e: IOException) {
            println("Error searching repositories: ${e.message}")
            return emptyList()
        }
    }

    // Data class to represent repository information
    data class Repository(
        val name: String,
        val description: String,
        val stars: Int,
        val language: String,
        val url: String
    )
}

fun main() {
    val searcher = GitHubRepositorySearch()

    println("Searching for Java/Kotlin repositories with GraphQL client and >500 stars:")
    val repositories = searcher.searchRepositories()

    if (repositories.isEmpty()) {
        println("No repositories found matching the criteria.")
    } else {
        repositories.forEachIndexed { index, repo ->
            println("\nRepository ${index + 1}:")
            println("Name: ${repo.name}")
            println("Description: ${repo.description}")
            println("Stars: ${repo.stars}")
            println("Primary Language: ${repo.language}")
            println("URL: ${repo.url}")
        }
    }
}
