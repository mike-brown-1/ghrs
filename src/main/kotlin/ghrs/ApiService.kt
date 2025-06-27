package ghrs

/* ***********
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
 **************** */
import ghapi.client.api.SearchApi
import ghapi.client.invoker.ApiClient
import ghapi.client.invoker.ApiException
import ghapi.client.invoker.Configuration
import ghapi.client.model.SearchRepos200Response
import java.net.URLEncoder


const val API_PREFIX = "https://api.github.com"
const val MAX_REPO_REQUEST = 100

class ApiService {
//    private var requestBuilder: Request.Builder? = null
    private val AUTH_TOKEN = System.getenv("GH_TOKEN")
    private var authorized = false
//    private val client = OkHttpClient()
    private var authenticatedUser: User? = null

    fun repoSearch(config: Config): SearchRepos200Response { // RepoSearchResponse? {
//        var repoSearchResponse: RepoSearchResponse? = null
//        var nextPageUrl: String = ""
//        val builder = getRequestBuilder()

        val defaultClient: ApiClient = Configuration.getDefaultApiClient()
        System.out.printf("base url: %s\n", defaultClient.getBasePath())
        val api = SearchApi(defaultClient)
        val q = buildQuery(config) // config.terms.joinToString(separator = " ") // "JSON"
        val sort = config.sort //: String? = null
        val order = config.order // String? = null
        val perPage = config.limit // : Int? = null
        val page = 1 //: Int? = null
//        try {
            val response = api.searchRepos(q, sort, order, perPage, page)
            System.out.printf("total found: %d\n", response.getTotalCount())
            System.out.printf("http code: %d\n", defaultClient.getStatusCode())
            System.out.printf("items returned: %d\n", response.getItems().size)
            return response
//        } catch (e: ApiException) {
//            e.printStackTrace()
//        }


//        builder.url("$API_PREFIX/search/repositories?q=${queryBuilder(config)}")
//        val request = builder.build()
//        println("--- request string: ${request}")
//        client.newCall(request).execute().use { response ->
//            if (response.isSuccessful) {
//                nextPageUrl = getNextPageUrl(response.header("link"))
//                val bodyString = response.body?.string()
//                val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//                val jsonAdapter: JsonAdapter<RepoSearchResponse> = moshi.adapter(RepoSearchResponse::class.java)
//                repoSearchResponse = jsonAdapter.fromJson(bodyString!!)
//            } else {
//                println("*** Request was not successful. Code: ${response.code}, Message: ${response.message}")
//            }
//        }
//        if (repoSearchResponse != null) {
//            if (repoSearchResponse.items.size > config.limit) {
//                val requestedItems = repoSearchResponse.items.take(config.limit)
//                println("requestedItems size: ${requestedItems.size}")
//                repoSearchResponse.items.clear()
//                repoSearchResponse.items.addAll(requestedItems)
//            } else {
//                repoSearchResponse.items.addAll(fetchMoreData(config.limit, repoSearchResponse.items.size, nextPageUrl))
//            }
//        }
//        return repoSearchResponse
    }

//    private fun fetchMoreData(desired: Int, have: Int, nextUrl: String): List<Repository> {
//        val repoList = mutableListOf<Repository>()
//        var needed = desired - have
//        var nextPageUrl = nextUrl
//        while (needed > 0 && nextPageUrl.isNotEmpty()) {
//            val builder = getRequestBuilder()
//            builder.url(nextPageUrl)
//            val request = builder.build()
//            client.newCall(request).execute().use { response ->
//                if (response.isSuccessful) {
//                    nextPageUrl = getNextPageUrl(response.header("link"))
//                    val bodyString = response.body?.string()
//                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//                    val jsonAdapter: JsonAdapter<RepoSearchResponse> = moshi.adapter(RepoSearchResponse::class.java)
//                    val repoSearchResponse = jsonAdapter.fromJson(bodyString!!)
//                    if (repoSearchResponse != null) {
//                        if (needed < repoSearchResponse.items.size) {
//                            repoList.addAll(repoSearchResponse.items.take(needed))
//                            needed = 0
//                        } else {
//                            repoList.addAll(repoSearchResponse.items)
//                            needed = needed - repoSearchResponse.items.size
//                        }
//                    } else {
//                        println("*** Parsing of response JSON failed")
//                    }
//                } else {
//                    println("""
//                        |*** Subsequent request was not successful. Code: ${response.code}
//                        |, Message: ${response.message}""".trimIndent())
//                }
//            }
//        }
//
//        return repoList
//    }
//
//    private fun getNextPageUrl(data: String?): String {
//        var result: String = ""
//
//        if (!data.isNullOrBlank()) {
//            val links = data.split(",")
//
//            for (link in links) {
//                val trimmedLink = link.trim()
//                if (trimmedLink.contains("""rel="next"""")) {
//                    val urlStart = trimmedLink.indexOf('<')
//                    val urlEnd = trimmedLink.indexOf('>')
//
//                    if (urlStart != -1 && urlEnd != -1 && urlStart < urlEnd) {
//                        result = trimmedLink.substring(urlStart + 1, urlEnd)
//                    }
//                }
//            }
//        }
//        return result
//    }
//
//    private fun getRequestBuilder(): Request.Builder {
//        if (requestBuilder != null) {
//            return requestBuilder!!
//        } else {
//            requestBuilder = Request.Builder()
//                .header("User-Agent", "ghrs")
//                .addHeader("Accept", "application/vnd.github.v3+json")
//            if (AUTH_TOKEN != null && AUTH_TOKEN.isNotEmpty()) {
//                requestBuilder?.addHeader("Authorization", "Bearer $AUTH_TOKEN")
//            }
//            val requestCopy = requestBuilder!!
//            requestCopy.url("$API_PREFIX/user")
//            val request = requestCopy.build()
//            client.newCall(request).execute().use { response ->
//                if (response.code == 401) {
//                    println("*** Authorization token is not valid")
//                } else {
//                    val bodyString = response.body!!.string()
//                    println(bodyString)
//                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//                    val jsonAdapter: JsonAdapter<User> = moshi.adapter(User::class.java)
//
//                    authenticatedUser = jsonAdapter.fromJson(bodyString)
//                    println("user: $authenticatedUser")
//                    authorized = true
//                }
//            }
//        }
//        return requestBuilder!!
//    }

    fun buildQuery(config: Config): String {
        val builder = StringBuilder()
        config.terms.forEach { term ->
            builder.append("$term ")
        }
        config.languages.forEach { language ->
            builder.append("language:$language ")
        }
        if (config.stars != null) {
            val stars = config.stars
            if (stars?.isEmpty() == false) {
                builder.append("stars:${stars} ")
            }
        }
        if (config.created != null) {
            val created
                    = config.created
            if (created?.isEmpty() == false) {
                builder.append("created:${created} ")
            }
        }
        if (config.updated != null) {
            val updated = config.updated
            if (updated?.isEmpty() == false) {
                builder.append("pushed:${updated} ")
            }
        }
//        val qualifiers = URLEncoder.encode(builder.toString().trim(), "utf-8")
        val qualifiers = builder.toString()
        println("qualifiers: $qualifiers")
        return qualifiers
    }

//    fun queryBuilder(config: Config): String {
//        val builder = StringBuilder()
//        config.terms.forEach { term ->
//            builder.append("$term ")
//        }
//        config.languages.forEach { language ->
//            builder.append("language:$language ")
//        }
//        if (config.stars != null) {
//            val stars = config.stars
//            if (stars?.isEmpty() == false) {
//                builder.append("stars:${stars} ")
//            }
//        }
//        if (config.created != null) {
//            val created
//            = config.created
//            if (created?.isEmpty() == false) {
//                builder.append("created:${created} ")
//            }
//        }
//        if (config.updated != null) {
//            val updated = config.updated
//            if (updated?.isEmpty() == false) {
//                builder.append("pushed:${updated} ")
//            }
//        }
//        val qualifiers = URLEncoder.encode(builder.toString().trim(), "utf-8")
//
//        val builder2 = StringBuilder()
//        if (config.sort != null) {
//            builder2.append("&sort=${config.sort}")
//        }
//        if (config.order != null) {
//            builder2.append("&order=${config.order}")
//        }
//        if (config.limit <= MAX_REPO_REQUEST) {
//            builder2.append("&per_page=${config.limit}")
//        }
//        val options = builder2.toString()
//
//        return "$qualifiers${options}"
//    }
}

