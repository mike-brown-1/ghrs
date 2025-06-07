package ghrs

import kotlin.collections.mutableListOf

data class User(
    val login: String,
    val id: Int,
    val name: String,
    val html_url: String,
    val company: String?,
    val location: String?
)

data class RepoSearchResponse (
    val total_count: Int,
    val incomplete_results: Boolean,
    var items: MutableList<Repository>  = mutableListOf<Repository>()
)

data class Repository (
    val id: Int,
    val name: String,
    val full_name: String,
    val html_url: String,
    val description: String?,
    val created_at: String,
    val updated_at: String,
    val pushed_at: String,
    val size: Int,
    val stargazers_count: Int,
    val watchers_count: Int,
    val language: String?
)
