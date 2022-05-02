package com.example

import com.example.data.BookRatingRepository
import com.example.data.connectSQLlite
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*
import kotlinx.coroutines.*
import org.junit.Test
import java.sql.DriverManager
import java.sql.DriverManager.println

class ApplicationTest {

    @Test
    fun testRead() {
        connectSQLlite()
        print(BookRatingRepository.getAll())
    }

    private fun log(message: String) {
        println(message)
    }
}