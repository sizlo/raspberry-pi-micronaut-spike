package com.timsummertonbrier

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Controller
class HomeController {
    @Get("/")
    @View("home")
    fun home(): HttpResponse<Any> {
        val messages = listOf(
            "Welcome to Micronaut",
            "Zoom zoom to the moon",
            "Quickfast website"
        )

        return HttpResponse.ok(mapOf(
            "message" to messages.random()
        ))
    }
}