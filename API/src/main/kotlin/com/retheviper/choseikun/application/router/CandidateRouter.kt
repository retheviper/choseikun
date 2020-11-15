package com.retheviper.choseikun.application.router

import com.retheviper.choseikun.domain.handler.CandidateHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.router

@Configuration
open class CandidateRouter(private val handler: CandidateHandler) {

    @Bean
    open fun routeCandidate() =
        nest(path("/api/v1/web/appointments/{appointmentId}/candidates"),
            router {
                listOf(
                    POST("/", handler::createCandidate),
                    PUT("/{candidateId}", handler::updateCandidate)
                )
            }
        )
}