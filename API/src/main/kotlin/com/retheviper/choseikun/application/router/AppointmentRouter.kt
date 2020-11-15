package com.retheviper.choseikun.application.router

import com.retheviper.choseikun.domain.handler.AppointmentHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.router

@Configuration
open class AppointmentRouter(private val handler: AppointmentHandler) {

    @Bean
    open fun routeAppointment() = nest(path("/api/v1/web/Appointments"),
        router {
            listOf(
                GET("/{id}", handler::getAppointment),
                POST("/", handler::createAppointment),
                PUT("/{id}", handler::updateAppointment)
            )
        }
    )
}