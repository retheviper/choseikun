package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.CandidateParticipantDto
import com.retheviper.choseikun.domain.common.container.Participant
import com.retheviper.choseikun.domain.common.container.ParticipantDto
import com.retheviper.choseikun.domain.common.container.ParticipantForm
import com.retheviper.choseikun.infrastructure.repository.ParticipantRepository
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
open class ParticipantHandler(
    private val repository: ParticipantRepository,
    private val candidateParticipantHandler: CandidateParticipantHandler
) {

    private val id: String = "participantId"

    internal fun getParticipant(participantId: Long): Mono<ParticipantDto> =
        repository.findById(participantId)
            .zipWith(candidateParticipantHandler.getCandidateParticipantsByParticipantsId(participantId).collectList())
            .map { mapDto(it.t1, it.t2) }

    fun createParticipant(request: ServerRequest): Mono<ServerResponse> =
        accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(ParticipantForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    val candidateParticipants =
                        Flux.fromIterable(
                            it.candidateParticipants.map { form ->
                                candidateParticipantHandler.createCandidateParticipant(form)
                            }).flatMap { candidateParticipant -> candidateParticipant }
                            .collectList()
                    repository.save(
                        Participant(
                            id = null,
                            name = it.name,
                            comment = it.comment
                        )
                    ).zipWith(candidateParticipants)
                        .map { tuple -> mapDto(tuple.t1, tuple.t2) }
                })

    fun updateParticipant(request: ServerRequest): Mono<ServerResponse> {
        val participantId = request.pathVariable(id).toLong()
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(ParticipantForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap { form ->
                    repository.findById(participantId)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map {
                            Participant(
                                id = it.id,
                                name = form.name,
                                comment = form.comment
                            )
                        }
                }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .flatMap { participant ->
                    repository.save(participant)
                        .zipWith(
                            candidateParticipantHandler.getCandidateParticipantsByParticipantsId(
                                participantId
                            ).collectList()
                        )
                        .map { mapDto(it.t1, it.t2) }
                })
    }

    private fun mapDto(participant: Participant, candidateParticipant: List<CandidateParticipantDto>) =
        ParticipantDto(
            name = participant.name,
            comment = participant.comment,
            candidateParticipants = candidateParticipant
        )
}