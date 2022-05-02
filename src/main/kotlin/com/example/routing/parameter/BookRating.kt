package com.example.routing.parameter

import kotlinx.serialization.Serializable

@Serializable
data class BookRating(
    val title: String?,
    val author: String?,
    val summary: String?,
    val isbn: String?,
    val image: String?,
    val rating: Float?,
    val publisher: String?,
    val description: String?
) : DataValid {
    override fun isValid(): Boolean {
        return arrayOf(title, summary, isbn, image, author, rating, publisher, description).all {
            when (it) {
                is String -> {
                    it.isNotEmpty()
                }
                is Float -> {
                    it > 0f
                }
                else -> {
                    it != null
                }
            }
        }
    }
}