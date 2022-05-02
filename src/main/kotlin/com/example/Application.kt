package com.example

import com.example.data.connectSQLlite
import com.example.plugins.configureJson
import com.example.routing.routingBookRating
import io.ktor.server.application.*
import io.ktor.server.netty.*
import java.util.*

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    EngineMain.main(args)
}

fun Application.module() {
    configureJson()
    connectSQLlite()
    routingBookRating()
}