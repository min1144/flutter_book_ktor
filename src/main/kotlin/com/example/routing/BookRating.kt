package com.example.routing

import com.example.data.BookMonthlyInfo
import com.example.data.BookRatingRepository
import com.example.routing.parameter.BookRating
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.util.*

fun Application.routingBookRating() {
    routing {
        get("/bookRating") { //최근 읽은 책 리스트 조회
            call.callResponse {
                BookRatingRepository.getAll(true).asJsonElement()
            }
        }
        get("/bookRating/isbn/{isbn}") {
            call.callResponse {
                val isbn = call.parameters["isbn"].orEmpty()
                checkParameter(isbn)
                BookRatingRepository.findBookRatingByIsbn(isbn)?.asJsonElement()
            }
        }
        get("/bookRating/monthly") {
            call.callResponse {
                val start = call.request.queryParameters["start"]
                val end = call.request.queryParameters["end"]
                checkParameter(start, end)
                BookRatingRepository.getBookRatingByDate(start.orEmpty(), end.orEmpty()).asJsonElement()
            }
        }
        post("/bookRating") {
            call.callResponse {
                val param = call.receive<BookRating>()
                BookRatingRepository.supportInsert {
                    this.title = param.title.orEmpty()
                    this.author = param.author.orEmpty()
                    this.summary = param.summary.orEmpty()
                    this.isbn = param.isbn.orEmpty()
                    this.image = param.image.orEmpty()
                    this.rating = param.rating ?: 0.0f
                    this.publisher = param.publisher.orEmpty()
                    this.description = param.description.orEmpty()
                }?.asJsonElement()
            }
        }

        put("/bookRating/{id}") {
            call.callResponse {
                val param = call.receive<BookRating>()
                val id = call.parameters["id"]?.toInt() ?: -1
                checkParameter(param, id)
                BookRatingRepository.supportUpdate(id) {
                    this.title = param.title.orEmpty()
                    this.author = param.author.orEmpty()
                    this.summary = param.summary.orEmpty()
                    this.isbn = param.isbn.orEmpty()
                    this.image = param.image.orEmpty()
                    this.rating = param.rating ?: 0.0f
                    this.publisher = param.publisher.orEmpty()
                    this.description = param.description.orEmpty()
                }.asJsonElement()
            }
        }
        delete("/bookRating/{id}") {
            call.callResponse {
                val id = call.parameters["id"]?.toInt() ?: -1
                checkParameter(id)
                BookRatingRepository.supportDelete(id).asJsonElement()
            }
        }

        get("bookRating/info/{year}") {
            call.callResponse {
                val year = call.parameters["year"].orEmpty()
                checkParameter(year)
                val list = BookRatingRepository.getBookRatingByYear(year).asJsonElement().jsonArray
                val result = ArrayList<BookMonthlyInfo>()
                for(i in 0 until 13) {
                    result.add(BookMonthlyInfo(id = i))
                }
                var temp = 0

                list.forEach { j ->
                    val month = (j.jsonObject["createDate"].toString()).substring(6,8).toInt()
                    val rating = (j.jsonObject["rating"].toString().toFloat())

                    if(temp != month) {
                        temp = month
                        result[temp] = result[temp].clone()
                    }

                    result[temp].apply {
                        this.count += 1
                        this.ratingSum = this.ratingSum.plus(rating)
                        this.average = if(this.count == 0) this.ratingSum else (this.ratingSum / this.count)
                    }
                }

                ArrayList<BookMonthlyInfo>().apply {
                    addAll(result)
                    removeAt(0)
                }.asJsonElement()
            }
        }
    }
}