package com.timsummertonbrier

import com.timsummertonbrier.categories.CategoryRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import org.slf4j.LoggerFactory

@Controller
class HomeController(private val categoryRepository: CategoryRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Get("/")
    @View("home")
    fun home(): HttpResponse<Any> {
        logger.info("Showing home page")
        return HttpResponse.ok(mapOf(
            "categories" to categoryRepository.getAllCategories()
        ))
    }
}