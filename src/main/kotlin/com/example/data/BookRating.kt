package com.example.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


object BookRatingTable : IntIdTable() {

    val title = text("title")

    val author = text("author")

    val summary = text("summary")

    val isbn = text("isbn")

    val image = text("image")

    val rating = float("rating")

    val publisher = text("publisher")

    val description = text("description")


    val createDate = datetime("create_date").clientDefault {
        LocalDateTime.now()
    }

    val updateDate = datetime("update_date").clientDefault {
        LocalDateTime.now()
    }

    override val tableName: String
        get() = "BookHistory"
}

@Serializable
data class BookRating(
    var id: Int = 0,
    var title: String = "",
    var author: String = "",
    var summary: String = "",
    var isbn: String = "",
    var image: String = "",
    var rating: Float = 0f,
    var publisher: String = "",
    var description: String = "",
    @Contextual @Serializable(with = LocalDateTimeSerializer::class) var createDate: LocalDateTime? = null,
    @Contextual @Serializable(with = LocalDateTimeSerializer::class) var updateDate: LocalDateTime? = null
)

@Serializable
data class BookMonthlyInfo(
    var id: Int = 0,
    var ratingSum: Float = 0.0f,
    var count: Int = 0,
    var average: Float = 0.0f,
): Cloneable {

    public override fun clone(): BookMonthlyInfo {
        var clone: BookMonthlyInfo ?= null
        try {
            clone = super.clone() as BookMonthlyInfo
        } catch (e : CloneNotSupportedException) {
            throw RuntimeException(e)
        }
        return clone
    }
}

object BookRatingRepository {

    private fun ResultRow.toBookRating(): BookRating {
        val id = this[BookRatingTable.id].value
        val title = this[BookRatingTable.title]
        val author = this[BookRatingTable.author]
        val summary = this[BookRatingTable.summary]
        val isbn = this[BookRatingTable.isbn]
        val image = this[BookRatingTable.image]
        val rating = this[BookRatingTable.rating]
        val publisher = this[BookRatingTable.publisher]
        val description = this[BookRatingTable.description]
        val createDate: LocalDateTime = this[BookRatingTable.createDate]
        val updateDate: LocalDateTime = this[BookRatingTable.updateDate]

        return BookRating(
            id, title, author, summary, isbn, image, rating, publisher, description, createDate, updateDate
        )
    }

    fun getAll(orderByDesc: Boolean = true) = transaction {
        BookRatingTable.selectAll().orderBy(
            BookRatingTable.updateDate, if (orderByDesc) SortOrder.DESC_NULLS_LAST else SortOrder.ASC_NULLS_LAST
        ).map {
            it.toBookRating()
        }.toMutableList()
    }

    fun findBookRatingByIsbn(isbn: String) = transaction {
        BookRatingTable.select {
            BookRatingTable.isbn eq isbn
        }.map {
            it.toBookRating()
        }.singleOrNull()
    }

    fun getBookRatingByDate(start: String = "", end: String = "") = transaction {
        val minTime = LocalDateTime.parse("$start 00:00:00", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))
        val maxTime = LocalDateTime.parse("$end 23:59:59", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))

        BookRatingTable.select {
            BookRatingTable.updateDate greaterEq (minTime) and
                    (BookRatingTable.updateDate lessEq maxTime)
        }.sortedByDescending {
            BookRatingTable.updateDate
        }.map {
            it.toBookRating()
        }.toMutableList()
    }

    fun getBookRatingByYear(year: String) = transaction {
        val minTime = LocalDateTime.parse("${year}0101 00:00:00", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))
        val maxTime = LocalDateTime.parse("${year}1231 23:59:59", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))

        BookRatingTable.select {
            BookRatingTable.createDate greaterEq (minTime) and
                    (BookRatingTable.createDate lessEq maxTime)
        }.orderBy(BookRatingTable.createDate to SortOrder.ASC)
        .map {
            it.toBookRating()
        }.toMutableList()
    }

    fun supportInsert(value: BookRating.() -> Unit) = transaction {
        val pro = BookRating()
        pro.value()
        BookRatingTable.insert {
            it[title] = pro.title
            it[author] = pro.author
            it[summary] = pro.summary
            it[isbn] = pro.isbn
            it[image] = pro.image
            it[rating] = pro.rating
            it[publisher] = pro.publisher
            it[description] = pro.description
        }.getOrNull(BookRatingTable.id)?.value
    }

    fun supportDelete(id: Int) = transaction {
        BookRatingTable.deleteWhere {
            BookRatingTable.id eq id
        }
    }

    fun supportUpdate(id: Int, value: BookRating.() -> Unit) = transaction {
        val pro = BookRating()
        pro.value()
        BookRatingTable.update({ BookRatingTable.id eq id }) {
            it[title] = pro.title
            it[author] = pro.author
            it[summary] = pro.summary
            it[isbn] = pro.isbn
            it[image] = pro.image
            it[rating] = pro.rating
            it[publisher] = pro.publisher
            it[description] = pro.description
            it[updateDate] = LocalDateTime.now()
        }
    }
}

