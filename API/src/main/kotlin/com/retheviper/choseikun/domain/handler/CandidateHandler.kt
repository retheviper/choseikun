package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.Candidate
import com.retheviper.choseikun.domain.common.container.CandidateDto
import com.retheviper.choseikun.domain.common.container.CandidateForm
import com.retheviper.choseikun.infrastructure.repository.CandidateRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.accepted
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
open class CandidateHandler(
    private val repository: CandidateRepository
) {

    private val id: String = "candidateId"

    // TODO Map participant
    internal fun getCandidates(appointmentId: Long): Flux<CandidateDto> {
        return repository.findAllByAppointmentId(appointmentId)
            .map {
                CandidateDto(
                    appointmentId = it.appointmentId,
                    date = it.date,
                    recommend = null,
                    candidateParticipants = null
                )
            }
    }

    fun createCandidate(request: ServerRequest): Mono<ServerResponse> =
        accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(CandidateForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    Mono.fromCallable {
                        repository.save(
                            Candidate(
                                id = null,
                                appointmentId = it.appointmentId,
                                candidateParticipants = null,
                                date = it.date
                            )
                        ).subscribe()
                    }
                })

    fun updateCandidate(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(CandidateForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    repository.findById(request.pathVariable(id).toLong())
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map {
                            Candidate(
                                id = it.id,
                                appointmentId = it.appointmentId,
                                candidateParticipants = it.candidateParticipants,
                                date = it.date
                            )
                        }
                }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .flatMap {
                    Mono.fromCallable {
                        repository.save(it).subscribe()
                    }
                })
}