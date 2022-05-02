package com.example.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteDataSource

fun connectSQLlite() {
    val ds = SQLiteDataSource()
    ds.url = "jdbc:sqlite:books"
    Database.connect(ds)
    transaction {
        addLogger(StdOutSqlLogger)
        arrayOf(BookRatingTable).run {
            SchemaUtils.create(*this)
        }
    }
}