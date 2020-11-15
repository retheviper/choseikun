package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.*
import com.retheviper.choseikun.infrastructure.repository.AppointmentRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.accepted
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
open class AppointmentHandler(
    private val repository: AppointmentRepository,
    private val candidateHandler: CandidateHandler
) {

    private val id: String = "appointmentId"

    fun getAppointment(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(id).toLong()
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                repository.findById(id)
                    .zipWith(
                        candidateHandler
                            .getCandidates(id)
                            .collectList()
                    )
                    .map {
                        AppointmentDto(
                            title = it.t1.title,
                            description = it.t1.description,
                            candidates = it.t2,
                            limit = it.t1.limit
                        )
                    }
                    .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
            )
    }

    fun createAppointment(request: ServerRequest): Mono<ServerResponse> =
        accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(AppointmentForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    Mono.fromCallable {
                        repository.save(
                            Appointment(
                                id = null,
                                title = it.title,
                                description = it.description,
                                candidates = it.candidates,
                                limit = it.limit
                            )
                        ).subscribe()
                    }.map { AppointmentDto::class.java }
                })

    fun updateAppointment(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(AppointmentForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    repository.findById(request.pathVariable(id).toLong())
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map {
                            Appointment(
                                id = it.id,
                                title = it.title,
                                description = it.description,
                                candidates = it.candidates,
                                limit = it.limit
                            )
                        }
                }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .flatMap { Appointment ->
                    Mono.fromCallable {
                        repository.save(Appointment).subscribe()
                    }.map { AppointmentDto::class.java }
                })
}
