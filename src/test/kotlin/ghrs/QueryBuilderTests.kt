package ghrs
import kotlin.test.Test

class QueryBuilderTests {

//    @Test
//    fun singleTerm() {
//        val apiService = ApiService()
//        val config = Config(
//            stars = null,
//            languages = mutableListOf(),
//            terms = mutableListOf("xml"),
//            sort = null,
//            order = null,
//            limit = 30,
//            created = null,
//            updated = null
//        )
//        val qString = apiService.queryBuilder(config)
//        println("gString: $qString")
//        assert(qString.isNotBlank() )
//    }
//
//    @Test
//    fun multipleTerms() {
//        val apiService = ApiService()
//        val config = Config(
//            stars = null,
//            languages = mutableListOf(),
//            terms = mutableListOf("xml", "schema", "openapi"),
//            sort = null,
//            order = null,
//            limit = 30,
//            created = null,
//            updated = null
//        )
//        val qString = apiService.queryBuilder(config)
//        println("gString: $qString")
//        assert(qString.isNotBlank() )
//    }
//
//    @Test
//    fun language() {
//        val apiService = ApiService()
//        val config = Config(
//            stars = null,
//            languages = mutableListOf("java"),
//            terms = mutableListOf("xml"),
//            sort = null,
//            order = null,
//            limit = 30,
//            created = null,
//            updated = null
//        )
//        val qString = apiService.queryBuilder(config)
//        println("gString: $qString")
//        assert(qString.contains("java")) { "language should include java"}
//    }
//
//    @Test
//    fun sort() {
//        val apiService = ApiService()
//        val config = Config(
//            stars = null,
//            languages = mutableListOf("java"),
//            terms = mutableListOf("xml"),
//            sort = "stars",
//            order = null,
//            limit = 30,
//            created = null,
//            updated = null
//        )
//        val qString = apiService.queryBuilder(config)
//        println("gString: $qString")
//        assert(qString.contains("stars")) { "query should include stars"}
//    }
}