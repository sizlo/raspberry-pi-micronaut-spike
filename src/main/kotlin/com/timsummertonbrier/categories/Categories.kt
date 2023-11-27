package com.timsummertonbrier.categories

import com.timsummertonbrier.books.Book
import com.timsummertonbrier.books.books
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.views.View
import jakarta.inject.Singleton

@Singleton
class CategoryRepository {
    fun getAllCategories(): List<String> {
        return books.values.map { it.category }.distinct()
    }

    fun getBooksInCategory(category: String): List<Book> {
        return books.values.filter { it.category == category }
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