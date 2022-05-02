package com.example.routing

import com.example.routing.parameter.DataValid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.*

suspend fun ApplicationCall.callResponse(result: suspend () -> JsonElement?) {
    fun makeResponse(message: String, code: String, value: JsonElement?): kotlinx.serialization.json.JsonObject {
        return buildJsonObject {
            this.put("message", message)
            this.put("code", code)
            value?.let { this.put("result", it) }
        }
    }

    fun JsonElement?.isDataUse(): Boolean {
        if (this == null) {
            return false
        }
        return when (this) {
            is JsonArray -> {
                this.isNotEmpty()
            }
            is JsonObject -> {
                this.entries.isNotEmpty()
            }
            else -> {
                true
            }
        }
    }

    (try {
        val parse = result.invoke()
        if (parse.isDataUse().not()) {
            throw EmptyResultException("result empty")
        } else {
            makeResponse("", "success", parse)
        }
    } catch (e: Exception) {
        when (e) {
            is EmptyResultException -> {
                makeResponse(e.message.orEmpty(), "result_empty", null)
            }
            is InvalidParameterException -> {
                makeResponse(e.message.orEmpty(), "request_invalid_parameter", null)
            }
            else -> {
                makeResponse("result parsing error[${e.message}]", "result_parsing_error", null)
            }
        }
    }).run {
        this@callResponse.respond(HttpStatusCode.OK, this)
    }
}

inline fun <reified R : Any> R.asJsonElement(): JsonElement {
    return Json.encodeToJsonElement(this)
}

fun checkParameter(vararg param: Any?) {
    param.forEach {
        if (it == null) {
            throw InvalidParameterException("parameter is null")
        }
        when {
            it is DataValid && it.isValid().not() -> {
                throw InvalidParameterException("parameter(${it::class.java.simpleName}) ${it::class.java.name} : $it invalid")
            }
            it is Int && it <= 0 -> {
                throw InvalidParameterException("parameter(int) ${it::class.java.name} : $it invalid")
            }
            it is Long && it <= 0 -> {
                throw InvalidParameterException("parameter(long) ${it::class.java.name} : $it invalid")
            }
            it is Float && it <= 0 -> {
                throw InvalidParameterException("parameter(float) ${it::class.java.name} : $it invalid")
            }
            it is String && it.isEmpty() -> {
                throw InvalidParameterException("parameter(string) ${it::class.java.name} : $it invalid")
            }
        }
    }
}