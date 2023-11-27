package com.timsummertonbrier.authors

import jakarta.inject.Singleton

@Singleton
class AuthorRepository {

    private val authors = mutableMapOf<Int, Author>()

    fun getAllAuthors(): List<Author> {
        return authors.values.toList()
    }

    fun getAuthor(id: Int): Author {
        return authors[id]!!
    }

    fun addAuthor(authorRequest: AuthorRequest) {
        authors[authors.size] = authorRequest.toAuthor(authors.size)
    }

    fun updateAuthor(id: Int, authorRequest: AuthorRequest) {
        authors[id] = authorRequest.toAuthor(id)
    }

    fun deleteAuthor(id: Int) {
        authors.remove(id)
    }

    private fun AuthorRequest.toAuthor(id: Int): Author {
        return Author(id, firstName!!, lastName!!)
    }
}