package com.timsummertonbrier.categories

import com.timsummertonbrier.authors.Authors
import com.timsummertonbrier.books.Book
import com.timsummertonbrier.books.Books
import com.timsummertonbrier.books.toBook
import com.timsummertonbrier.database.ExposedTransactional
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.views.View
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

@Singleton
@ExposedTransactional
class CategoryRepository {
    fun getAllCategories(): List<String> {
        return Books.slice(Books.category).selectAll().withDistinct().map { it[Books.category] }
    }

    fun getBooksInCategory(category: String): List<Book> {
        return (Books innerJoin Authors).select { Books.category eq category }.map { it.toBook() }
    }
}

@Controller("/categories")
class CategoryController(private val categoryRepository: CategoryRepository) {
    @Get("/{category}")
    @View("categories/view-one")
    fun category(@PathVariable("category") category: String): HttpResponse<Any> {
        return HttpResponse.ok(mapOf(
            "category" to category,
            "books" to categoryRepository.getBooksInCategory(category),
        ))
    }
}