package com.timsummertonbrier.books

import com.timsummertonbrier.authors.Author
import com.timsummertonbrier.authors.Authors
import com.timsummertonbrier.database.ExposedTransactional
import jakarta.inject.Singleton
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object Books : IntIdTable("book") {
    var title = text("title")
    var category = text("category")
    var authorId = reference("author_id", Authors)

    fun UpdateBuilder<Int>.populateFrom(bookRequest: BookRequest) {
        this[title] = bookRequest.title!!
        this[category] = bookRequest.category!!
        this[authorId] = bookRequest.authorId!!
    }
}

fun ResultRow.toBook(): Book {
    return Book(
        this[Books.id].value,
        this[Books.title],
        this[Books.category],
        Author(
            this[Authors.id].value,
            this[Authors.firstName],
            this[Authors.lastName]
        )
    )
}

@Singleton
@ExposedTransactional
class BookRepository {
    fun getAllBooks(): List<Book> {
        return (Books innerJoin Authors).selectAll().map { it.toBook() }
    }

    fun getBook(id: Int): Book {
        return (Books innerJoin Authors).select { Books.id eq id }.map { it.toBook() }.first()
    }

    fun addBook(bookRequest: BookRequest) : Int {
        return Books.insertAndGetId { it.populateFrom(bookRequest) }.value
    }

    fun updateBook(id: Int, bookRequest: BookRequest) {
        Books.update({ Books.id eq id }) { it.populateFrom(bookRequest) }
    }

    fun deleteBook(id: Int) {
        Books.deleteWhere { Books.id eq id }
    }
}