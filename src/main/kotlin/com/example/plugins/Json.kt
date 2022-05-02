package com.example.plugins

import com.example.data.LocalDateTimeSerializer
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule


fun Application.configureJson() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(LocalDateTimeSerializer::class) {
                    LocalDateTimeSerializer
                }
            }
        })
        gson()
    }
}
