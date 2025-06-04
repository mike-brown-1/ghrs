package ghrs

import okhttp3.OkHttpClient
import okhttp3.Request

class ApiService {
    private var requestBuilder: Request.Builder? = null
    private val AUTHTOKEN = System.getenv("GH_TOKEN")
    private var authorized = false
    private val APIPREFIX = "https://api.github.com"
    private val client = OkHttpClient()

    fun repoSearch(term: String) {
        val builder = getRequestBuilder()
        builder.url("$APIPREFIX/search/repositories?q=$term")
        val request = builder.build()
        client.newCall(request).execute().use { response ->
            println(response.code)
        }

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
                    println(response.body!!.string())
                    authorized = true
                }
            }
        }
        return requestBuilder!!
    }
}

