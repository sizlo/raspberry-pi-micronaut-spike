package com.timsummertonbrier.authors

import com.timsummertonbrier.database.ExposedTransactional
import jakarta.inject.Singleton
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object Authors : IntIdTable("author") {
    var firstName = text("first_name")
    var lastName = text("last_name")

    fun UpdateBuilder<Int>.populateFrom(authorRequest: AuthorRequest) {
        this[firstName] = authorRequest.firstName!!
        this[lastName] = authorRequest.lastName!!
    }
}

fun ResultRow.toAuthor(): Author {
    return Author(
        this[Authors.id].value,
        this[Authors.firstName],
        this[Authors.lastName]
    )
}

@Singleton
@ExposedTransactional
class AuthorRepository {
    fun getAllAuthors(): List<Author> {
        return Authors.selectAll().map { it.toAuthor() }
    }

    fun getAuthor(id: Int): Author {
        return Authors.select { Authors.id eq id }.map { it.toAuthor() }.first()
    }

    fun addAuthor(authorRequest: AuthorRequest): Int {
        return Authors.insertAndGetId { it.populateFrom(authorRequest) }.value
    }

    fun updateAuthor(id: Int, authorRequest: AuthorRequest) {
        Authors.update({ Authors.id eq id }) { it.populateFrom(authorRequest) }
    }

    fun deleteAuthor(id: Int) {
        Authors.deleteWhere { Authors.id eq id }
    }
}