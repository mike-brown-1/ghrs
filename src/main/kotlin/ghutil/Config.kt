package ghutil

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addFileSource
import kotlin.system.exitProcess

data class Config(
    var stars: String? = null,
    var languages: MutableList<String> = mutableListOf(),
    var terms: MutableList<String> = mutableListOf(),
    var sort: String? = null,
    var order: String? = null,
    var limit: Int? = null
)

@OptIn(ExperimentalHoplite::class)
fun loadConfig(path: String): Config {
    val config = ConfigLoaderBuilder.default()
        .withExplicitSealedTypes()
        .addFileSource(path)
        .build()
        .loadConfigOrThrow<Config>()
    return config
}

fun overrideConfig(config: Config, command: Command): Config {
    val result = config
    if (command.terms.isNotEmpty()) {
        result.terms.addAll(command.terms)
    }
    if (result.terms.isEmpty()) {
        println("You must provide a term to search for")
        exitProcess(10)
    }
    result.stars = validateUpdateStars(command.stars, config.stars)
    if (command.languages.isNotEmpty()) {
        result.languages.addAll(command.languages)
    }
    if (command.sort != null) {
        val theSort = command.sort!!
        if (theSort.isNotEmpty()) {
            result.sort = command.sort
        }
    }
    if (command.order != null) {
        val theOrder = command.order!!
        if (theOrder.isNotEmpty()) {
            result.order = theOrder
        }
    }
    if (command.limit != null) {
        result.limit = command.limit
    }

    return result
}

fun validateUpdateStars(commandLine: List<String>?, loaded: String?): String {
    var result = formatStars(loaded?.split(","))
    val cline = formatStars(commandLine)
    if (cline.isNotEmpty()) {
        result = cline
    }
    return result
}

private fun formatStars(stars: List<String>?): String {
    // >=200, <100, and <=99, 100..500
    var result = ""
    val operators = listOf(">", ">=", "<", "<=")
    if (stars != null) {
        if (stars.size == 2) {
            if (stars[0] in operators && stars[1].toIntOrNull() != null) {
                result = "${stars[0]}${stars[1]}"
            } else {
                println("ERROR: stars parameter must be <operator>,<number of stars>")
            }
        } else if (stars.size == 3) {
            if (stars[0].toIntOrNull() != null && stars[1] == ".." && stars[2].toIntOrNull() != null) {
                result = "${stars[0]}..${stars[2]}"
            } else {
                println("ERROR: for three parts, a range is expected (<num>,..,<num>")
            }
        } else {
            println("Invalid stars param. Must be <op>,number or number,..,number")
            println("Valid operators are: >, >=, <, <=")
        }
    }
    return result
}
