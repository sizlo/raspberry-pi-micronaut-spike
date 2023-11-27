package com.timsummertonbrier.books

import com.timsummertonbrier.authors.Author
import com.timsummertonbrier.authors.AuthorRepository
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.net.URI

data class Book(
    val id: Int,
    val title: String,
    val category: String,
    val author: Author,
)

@Serdeable
data class BookRequest(
    @field:NotBlank
    val title: String? = null,

    @field:NotBlank
    val category: String? = null,

    val authorId: Int? = null
) {
    companion object {
        fun fromBook(book: Book): BookRequest {
            return BookRequest(
                book.title,
                book.category,
                book.author.id,
            )
        }
    }
}

@Controller("/books")
open class BookController(private val bookRepository: BookRepository, private val authorRepository: AuthorRepository) {

    @Get
    @View("books/view-all")
    fun books(): HttpResponse<Any> {
        return HttpResponse.ok(mapOf(
            "books" to bookRepository.getAllBooks()
        ))
    }

    @Get("/add")
    @View("books/add")
    fun showAddBookForm(): HttpResponse<Any> {
        return HttpResponse.ok(mapOf(
            "bookRequest" to  BookRequest(),
            "authors" to  authorRepository.getAllAuthors(),
        ))
    }

    @Get("/edit/{id}")
    @View("books/edit")
    fun showUpdateBookForm(@PathVariable("id") id: Int): HttpResponse<Any> {
        return HttpResponse.ok(mapOf(
            "id" to id,
            "bookRequest" to BookRequest.fromBook(bookRepository.getBook(id)),
            "authors" to authorRepository.getAllAuthors(),
        ))
    }

    @Post("/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    open fun addBook(@Valid @Body bookRequest: BookRequest): HttpResponse<Any> {
        bookRepository.addBook(bookRequest)
        return HttpResponse.seeOther(URI.create("/books"))
    }

    @Post("/update/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    open fun updateBook(@PathVariable("id") id: Int, @Valid @Body bookRequest: BookRequest): HttpResponse<Any> {
        bookRepository.updateBook(id, bookRequest)
        return HttpResponse.seeOther(URI.create("/books"))
    }

    @Post("/delete/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun deleteBook(@PathVariable("id") id: Int): HttpResponse<Any> {
        bookRepository.deleteBook(id)
        return HttpResponse.seeOther(URI.create("/books"))
    }

    @Error
    fun onValidationError(request: HttpRequest<Any>, exception: ConstraintViolationException): HttpResponse<Any> {
        val viewName = when {
            request.path == "/books/add" -> "books/add"
            request.path.startsWith("/books/update") -> "books/edit"
            else -> return HttpResponse.serverError()
        }

        val id = if (request.path.startsWith("/books/update")) {
            request.path.split("/").last()
        } else {
            null
        }

        val body = ModelAndView(
            viewName,
            mapOf(
                "id" to id,
                "bookRequest" to request.body.get(),
                "authors" to authorRepository.getAllAuthors(),
                "fieldErrors" to buildFieldErrors(exception)
            )
        )
        return HttpResponse.unprocessableEntity<Any>().body(body)
    }

    private fun buildFieldErrors(exception: ConstraintViolationException): Map<String, List<String>> {
        return exception.constraintViolations.groupBy(
            keySelector = { it.propertyPath.last().name },
            valueTransform = { it.message },
        )
    }
}