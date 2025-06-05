package ghrs

data class User(
    val login: String,
    val id: Int,
    val name: String,
    val html_url: String,
    val company: String?,
    val location: String?
)
