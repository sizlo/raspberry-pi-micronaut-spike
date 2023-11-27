package com.timsummertonbrier

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.views.ModelAndView
import io.micronaut.views.model.ViewModelProcessor
import jakarta.inject.Singleton

@Singleton
class GlobalModelAttributes (
    @Value("\${app.version}") private val appVersion: String,
    @Value("\${app.environment}") private val appEnvironment: String,
) : ViewModelProcessor<Map<String, Any>> {

    override fun process(request: HttpRequest<*>, modelAndView: ModelAndView<Map<String, Any>>) {
        modelAndView.addToModel(mapOf(
            "buildInfo" to "$appVersion - running on: $appEnvironment - database: in memory maps"
        ))
    }

    private fun ModelAndView<Map<String, Any>>.addToModel(attributes: Map<String, Any>) {
        val enrichedModel = this.model
            .map { it.toMutableMap() }
            .orElse(mutableMapOf())
            .also { it.putAll(attributes) }
        this.setModel(enrichedModel)
    }
}