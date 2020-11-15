package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.Participant
import com.retheviper.choseikun.domain.common.container.ParticipantDto
import com.retheviper.choseikun.domain.common.container.ParticipantForm
import com.retheviper.choseikun.infrastructure.repository.ParticipantRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
open class ParticipantHandler(private val repository: ParticipantRepository) {

    private val id: String = "id"

    internal fun getParticipant(id: Long): Mono<ParticipantDto> =
        repository.findById(id)
            .map {
                ParticipantDto(
                    name = it.name,
                    comment = it.comment,
                    candidateParticipants = null
                )
            }


    fun createParticipant(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(ParticipantForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    Mono.fromCallable {
                        repository.save(
                            Participant(
                                id = null,
                                candidateParticipants = null,
                                name = it.name,
                                comment = it.comment
                            )
                        ).subscribe()
                    }
                })

    fun updateParticipant(request: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(ParticipantForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    repository.findById(request.pathVariable(id).toLong())
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map {
                            Participant(
                                id = it.id,
                                candidateParticipants = null,
                                name = it.name,
                                comment = it.comment
                            )
                        }
                }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .flatMap { Participant ->
                    Mono.fromCallable {
                        repository.save(Participant).subscribe()
                    }
                })
}