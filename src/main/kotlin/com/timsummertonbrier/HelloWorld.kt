package com.timsummertonbrier

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Singleton

@Singleton
class HelloWorldService {
    fun message() = "Hello world!"
}

@Controller
class HelloWorldController(private val helloWorldService: HelloWorldService) {
    @Get("/hello")
    fun hello() = helloWorldService.message()
}
