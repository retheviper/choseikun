package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.Appointment
import com.retheviper.choseikun.domain.common.container.AppointmentDto
import com.retheviper.choseikun.domain.common.container.AppointmentForm
import com.retheviper.choseikun.domain.common.container.CandidateDto
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
        val appointmentId = request.pathVariable(id).toLong()
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                repository.findById(appointmentId)
                    .zipWith(candidateHandler.getCandidates(appointmentId).collectList())
                    .map { mapDto(it.t1, it.t2) }
                    .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
            )
    }

    fun createAppointment(request: ServerRequest): Mono<ServerResponse> =
        accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(AppointmentForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .map {
                    it.candidates?.forEach(candidateHandler::createCandidate)
                    repository.save(
                        Appointment(
                            id = null,
                            title = it.title,
                            description = it.description,
                            limit = it.limit
                        )
                    ).map { entity -> mapDto(entity, null) }
                })


    fun updateAppointment(request: ServerRequest): Mono<ServerResponse> {
        val appointmentId = request.pathVariable(id).toLong()
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(AppointmentForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap { form ->
                    repository.findById(appointmentId)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map {
                            Appointment(
                                id = it.id,
                                title = form.title,
                                description = form.description,
                                limit = form.limit
                            )
                        }
                }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .map {
                    repository.save(it)
                        .zipWith(candidateHandler.getCandidates(appointmentId).collectList())
                        .map { mapDto(it.t1, it.t2) }
                })
    }

    private fun mapDto(appointment: Appointment, candidates: List<CandidateDto>?) =
        AppointmentDto(
            title = appointment.title,
            description = appointment.description,
            candidates = candidates,
            limit = appointment.limit
        )
}
