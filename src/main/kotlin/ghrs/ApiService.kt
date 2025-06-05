package ghrs

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiService {
    private var requestBuilder: Request.Builder? = null
    private val AUTHTOKEN = System.getenv("GH_TOKEN")
    private var authorized = false
    private val APIPREFIX = "https://api.github.com"
    private val client = OkHttpClient()
    private var authenticatedUser: User? = null

    fun repoSearch(config: Config): RepoSearchResponse? {
        var repoSearchResponse: RepoSearchResponse? = null
        val builder = getRequestBuilder()
        // TODO need fun to build "query" from config
        builder.url("$APIPREFIX/search/repositories?q=xml")
        val request = builder.build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val bodyString = response.body!!.string()
                val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter: JsonAdapter<RepoSearchResponse> = moshi.adapter(RepoSearchResponse::class.java)
                repoSearchResponse = jsonAdapter.fromJson(bodyString)
            } else {
                println("Response not successful. Code: ${response.code}, Message: ${response.message}")
            }
        }
        // TODO handle pagination up to limit
        return repoSearchResponse
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
}

