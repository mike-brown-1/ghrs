package ghrs

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class ApiService {
    private var requestBuilder: Request.Builder? = null
    private val AUTHTOKEN = System.getenv("GH_TOKEN")
    private var authorized = false
    private val APIPREFIX = "https://api.github.com"
    private val MAXREPOREQUEST = 100
    private val client = OkHttpClient()
    private var authenticatedUser: User? = null

    fun repoSearch(config: Config): RepoSearchResponse? {
        var repoSearchResponse: RepoSearchResponse? = null
        var nextPageUrl: String = ""
        val builder = getRequestBuilder()
        // TODO need fun to build "query" from config should also set per page based on requested limit
        builder.url("$APIPREFIX/search/repositories?q=${queryBuilder(config)}")
        val request = builder.build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                nextPageUrl = getNextPageUrl(response.header("link"))
                val bodyString = response.body!!.string()
                val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter: JsonAdapter<RepoSearchResponse> = moshi.adapter(RepoSearchResponse::class.java)
                repoSearchResponse = jsonAdapter.fromJson(bodyString)
            } else {
                println("Request was not successful. Code: ${response.code}, Message: ${response.message}")
            }
        }
        if (repoSearchResponse != null) {
            if (repoSearchResponse.items.size > config.limit) {
                val requestedItems = repoSearchResponse.items.take(config.limit)
                println("requestedItems size: ${requestedItems.size}")
                repoSearchResponse.items.clear()
                repoSearchResponse.items.addAll(requestedItems)
                // replace "items" with new list with only the requested items
            } else {
                repoSearchResponse.items.addAll(fetchMoreData(config.limit, repoSearchResponse.items.size, nextPageUrl))
            }
        }
        return repoSearchResponse
    }

    private fun fetchMoreData(desired: Int, have: Int, nextUrl: String): List<Repository> {
        val repoList = mutableListOf<Repository>()
        var needed = desired - have
        var nextPageUrl = nextUrl
        while (needed > 0 && nextPageUrl.isNotEmpty()) {
            val builder = getRequestBuilder()
            builder.url(nextPageUrl)
            val request = builder.build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    nextPageUrl = getNextPageUrl(response.header("link"))

                    val bodyString = response.body!!.string()
                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter: JsonAdapter<RepoSearchResponse> = moshi.adapter(RepoSearchResponse::class.java)
                    val repoSearchResponse = jsonAdapter.fromJson(bodyString)
                    if (needed < repoSearchResponse!!.items.size) {
                        repoList.addAll(repoSearchResponse.items.take(needed))
                        needed = 0
                    } else {
                        repoList.addAll(repoSearchResponse.items)
                        needed = needed - repoSearchResponse.items.size
                    }
                } else {
                    println("Subsequent request was not successful. Code: ${response.code}, Message: ${response.message}")
                }
            }
        }

        return repoList
    }

    private fun getNextPageUrl(data: String?): String {
        var result: String = ""

        if (!data.isNullOrBlank()) {
            val links = data.split(",")

            for (link in links) {
                val trimmedLink = link.trim()
                if (trimmedLink.contains("""rel="next"""")) {
                    val urlStart = trimmedLink.indexOf('<')
                    val urlEnd = trimmedLink.indexOf('>')

                    if (urlStart != -1 && urlEnd != -1 && urlStart < urlEnd) {
                        result = trimmedLink.substring(urlStart + 1, urlEnd)
                    }
                }
            }
        }
        return result
    }

    private fun getRequestBuilder(): Request.Builder {
        if (requestBuilder != null) {
            return requestBuilder!!
        } else {
            requestBuilder = Request.Builder()
                .header("User-Agent", "ghrs")
                .addHeader("Accept", "application/vnd.github.v3+json")
            if (AUTHTOKEN != null && AUTHTOKEN.isNotEmpty()) {
                requestBuilder?.addHeader("Authorization", "Bearer $AUTHTOKEN")
            }
            val requestCopy = requestBuilder!!
            requestCopy.url("$APIPREFIX/user")
            val request = requestCopy.build()
            client.newCall(request).execute().use { response ->
                if (response.code == 401) {
                    println("Authorization token is not valid")
                } else {
                    val bodyString = response.body!!.string()
                    println(bodyString)
                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter: JsonAdapter<User> = moshi.adapter(User::class.java)

                    authenticatedUser = jsonAdapter.fromJson(bodyString)
                    println("user: $authenticatedUser")

                    authorized = true
                }
            }
        }
        return requestBuilder!!
    }

    fun queryBuilder(config: Config): String {
        var result = ""
        val builder = StringBuilder()
        config.terms.forEach { term ->
            builder.append("$term ")
        }
        config.languages.forEach { language ->
            builder.append("language:$language ")
        }
        if (config.stars != null) {
            val starList = config.stars
            if (starList?.isEmpty() == false) {
                builder.append("stars:${starList[0]}${starList[1]} ")
            }
        }
        if (config.created != null) {
            val createdList = config.created
            if (createdList?.isEmpty() == false) {
                builder.append("created:${createdList[0]}${createdList[1]} ")
            }
        }
        if (config.updated != null) {
            val updatedList = config.updated
            if (updatedList?.isEmpty() == false) {
                builder.append("pushed:${updatedList[0]}${updatedList[1]} ")
            }
        }
        val qualifiers = builder.toString().trim()

        val builder2 = StringBuilder()
        if (config.sort != null) {
            builder2.append("&sort=${config.sort}")
        }
        if (config.order != null) {
            builder2.append("&order=${config.order}")
        }
        if (config.limit <= 100) {
            builder2.append("&per_page=${config.limit}")
        }

        val combined = "$qualifiers${builder2.toString()}"
        println("combined: $combined")
        result = URLEncoder.encode(combined, "utf-8")
        println("encoded: $result")
        return result
    }
}

