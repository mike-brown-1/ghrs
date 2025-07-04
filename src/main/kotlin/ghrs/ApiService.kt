package ghrs

import ghapi.client.api.SearchApi
import ghapi.client.invoker.ApiClient
import ghapi.client.invoker.Configuration
import ghapi.client.model.SearchRepos200Response

//
//const val API_PREFIX = "https://api.github.com"
//const val MAX_REPO_REQUEST = 100

class ApiService {
    // TODO implement the following
//    private val AUTH_TOKEN = System.getenv("GH_TOKEN")
//    private var authorized = false
//    private var authenticatedUser: User? = null

    fun repoSearch(config: Config): SearchRepos200Response { // RepoSearchResponse? {
        val defaultClient: ApiClient = Configuration.getDefaultApiClient()
        System.out.printf("base url: %s\n", defaultClient.getBasePath())
        val api = SearchApi(defaultClient)
        val q = buildQuery(config) // config.terms.joinToString(separator = " ") // "JSON"
        val sort = config.sort //: String? = null
        val order = config.order // String? = null
        val perPage = config.limit // : Int? = null
        val page = 1 //: Int? = null
        val response = api.searchRepos(q, sort, order, perPage, page)
        System.out.printf("total found: %d\n", response.getTotalCount())
        System.out.printf("http code: %d\n", defaultClient.getStatusCode())
        System.out.printf("items returned: %d\n", response.getItems().size)
        return response
    }

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
        val qualifiers = builder.toString()
        println("qualifiers: $qualifiers")
        return qualifiers
    }

}

