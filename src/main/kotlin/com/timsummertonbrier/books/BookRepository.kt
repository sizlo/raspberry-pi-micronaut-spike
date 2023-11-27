package com.timsummertonbrier.books

import com.timsummertonbrier.authors.authors
import jakarta.inject.Singleton

val books = mutableMapOf<Int, Book>()

@Singleton
class BookRepository {

    fun getAllBooks(): List<Book> {
        return books.values.toList()
    }

    fun getBook(id: Int): Book {
        return books[id]!!
    }

    fun addBook(bookRequest: BookRequest) {
        books[books.size] = bookRequest.toBook(books.size)
    }

    fun updateBook(id: Int, bookRequest: BookRequest) {
        books[id] = bookRequest.toBook(id)
    }

    fun deleteBook(id: Int) {
        books.remove(id)
    }

    private fun BookRequest.toBook(id: Int): Book {
        return Book(id, title!!, category!!, authors[authorId!!]!!)
    }
}