package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.Candidate
import com.retheviper.choseikun.domain.common.container.CandidateDto
import com.retheviper.choseikun.domain.common.container.CandidateForm
import com.retheviper.choseikun.domain.common.container.ParticipantDto
import com.retheviper.choseikun.domain.common.value.Join
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
import reactor.core.publisher.Mono

@Component
open class CandidateHandler(internal val repository: CandidateRepository) {

    private val id: String = "id"

    fun getCandidates(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                repository.findAll()
                    .map(this::mapDto)
            )

    fun getCandidate(request: ServerRequest): Mono<ServerResponse> =
        ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                repository.findById(request.pathVariable(id).toLong())
                    .map(this::mapDto)
                    .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
            )

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
                                participants = null,
                                date = it.date
                            )
                        ).map(this::mapDto).subscribe()
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
                                participants = it.participants,
                                date = it.date
                            )
                        }
                }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .flatMap { Candidate ->
                    Mono.fromCallable {
                        repository.save(Candidate).map(this::mapDto).subscribe()
                    }
                })

    private fun mapDto(entity: Candidate): CandidateDto {
        var ok = 0
        var ng = 0
        val participants = entity.participants
            ?.map {
                when (it.join) {
                    Join.OK -> {
                        ok++
                    }
                    Join.PEND -> {
                        ok++
                        ng++
                    }
                    Join.NG -> {
                        ng++
                    }
                }
                ParticipantDto(
                    name = it.name,
                    candidate = it.candidate,
                    join = it.join,
                    comment = it.comment
                )
            }
        return CandidateDto(
            appointmentId = entity.appointmentId,
            participants = participants,
            date = entity.date,
            recommend = ok > ng
        )
    }
}
