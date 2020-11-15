package com.retheviper.choseikun.application.router

import com.retheviper.choseikun.domain.handler.ParticipantHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.router

@Configuration
open class ParticipantRouter(private val handler: ParticipantHandler) {

    @Bean
    open fun routeParticipant() =
        nest(path("/api/v1/web/appointments/{appointmentId}/participant"),
            router {
                listOf(
                    POST("/", handler::createParticipant),
                    PUT("/{participantId}", handler::updateParticipant)
                )
            }
        )
}