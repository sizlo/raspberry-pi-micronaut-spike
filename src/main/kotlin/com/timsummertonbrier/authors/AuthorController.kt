package com.timsummertonbrier.authors

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

data class Author(
    val id: Int,
    val firstName: String,
    val lastName: String,
) {
    val name = "$firstName $lastName"
}

@Serdeable
data class AuthorRequest(
    @field:NotBlank
    val firstName: String? = null,

    @field:NotBlank
    val lastName: String? = null,
) {
    companion object {
        fun fromAuthor(author: Author): AuthorRequest {
            return AuthorRequest(
                author.firstName,
                author.lastName
            )
        }
    }
}

@Controller("/authors")
open class AuthorController(private val authorRepository: AuthorRepository) {

    @Get
    @View("authors/view-all")
    fun authors(): HttpResponse<Any> {
        return HttpResponse.ok(mapOf(
            "authors" to authorRepository.getAllAuthors()
        ))
    }

    @Get("/add")
    @View("authors/add")
    fun showAddAuthorForm(): HttpResponse<Any> {
        return HttpResponse.ok(mapOf(
            "authorRequest" to AuthorRequest()
        ))
    }

    @Get("/edit/{id}")
    @View("authors/edit")
    fun showUpdateAuthorForm(@PathVariable("id") id: Int): HttpResponse<Any> {
        return HttpResponse.ok(mapOf(
            "id" to id,
            "authorRequest" to AuthorRequest.fromAuthor(authorRepository.getAuthor(id))
        ))
    }

    @Post("/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    open fun addAuthor(@Valid @Body authorRequest: AuthorRequest): HttpResponse<Any> {
        authorRepository.addAuthor(authorRequest)
        return HttpResponse.seeOther(URI.create("/authors"))
    }

    @Post("/update/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    open fun updateAuthor(@PathVariable("id") id: Int, @Valid @Body authorRequest: AuthorRequest): HttpResponse<Any> {
        authorRepository.updateAuthor(id, authorRequest)
        return HttpResponse.seeOther(URI.create("/authors"))
    }

    /**
     * We need to include the authorRequest object as a model attribute so the current state of the edit form
     * is preserved if the delete fails.
     * A better implementation would be to have a view author page, which is separate from the edit author page.
     * The form would be on the edit page, the delete button on the view page.
     */
    @Post("/delete/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun deleteAuthor(@PathVariable("id") id: Int, @Body authorRequest: AuthorRequest): HttpResponse<Any> {
        try {
            authorRepository.deleteAuthor(id)
        } catch (e: BooksStillExistForAuthorException) {
            val body = ModelAndView(
                "authors/edit",
                mapOf(
                    "error" to "Could not delete author as they still have books",
                    "authorRequest" to authorRequest,
                    "id" to id,
                )
            )
            return HttpResponse.badRequest(body)
        }
        return HttpResponse.seeOther(URI.create("/authors"))
    }

    @Error
    fun onValidationError(request: HttpRequest<Any>, exception: ConstraintViolationException): HttpResponse<Any> {
        val viewName = when {
            request.path == "/authors/add" -> "authors/add"
            request.path.startsWith("/authors/update") -> "authors/edit"
            else -> return HttpResponse.serverError()
        }

        val id = if (request.path.startsWith("/authors/update")) {
            request.path.split("/").last()
        } else {
            null
        }

        val body = ModelAndView(
            viewName,
            mapOf(
                "id" to id,
                "authorRequest" to request.body.get(),
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