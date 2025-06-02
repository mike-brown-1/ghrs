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
    var limit: Int? = null,
    var created: String? = null,
    var updated: String? = null
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
    result.stars = setOption(command.stars, config.stars, "stars")
    result.created = setOption(command.created, config.created, "created")
    result.updated = setOption(command.updated, config.updated, "updated")
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

fun setOption(commandLine: List<String>?, loaded: String?, optionName: String): String {
    var result = parseOption(loaded?.split(","), optionName)
    val cline = parseOption(commandLine, optionName)
    if (cline.isNotEmpty()) {
        result = cline
    }
    return result
}

private fun parseOption(value: List<String>?, optionName: String): String {
    // >=200, <100, and <=99, 100..500
    var result = ""
    val operators = listOf(">", ">=", "<", "<=")
    if (value != null) {
        if (value.size == 2) {
            if (value[0] in operators) { // when only stars,  && value[1].toIntOrNull() != null
                result = "${value[0]}${value[1]}"
            } else {
                println("ERROR: $optionName parameter invalid")
            }
        } else if (value.size == 3) {
            if (value[1] == "..") {
                result = "${value[0]}..${value[2]}"
            } else {
                println("ERROR: $optionName parameter invalid")
            }
        } else {
            println("ERROR: $optionName parameter invalid")
        }
    }
    return result
}
