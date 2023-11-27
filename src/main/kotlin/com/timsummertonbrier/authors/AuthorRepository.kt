package com.timsummertonbrier.authors

import com.timsummertonbrier.books.books
import jakarta.inject.Singleton

val authors = mutableMapOf<Int, Author>()

@Singleton
class AuthorRepository {

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
        if (books.any { it.value.author.id == id }) {
            throw BooksStillExistForAuthorException()
        }
        authors.remove(id)
    }

    private fun AuthorRequest.toAuthor(id: Int): Author {
        return Author(id, firstName!!, lastName!!)
    }
}

class BooksStillExistForAuthorException : RuntimeException()