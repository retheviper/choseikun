package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.Candidate
import com.retheviper.choseikun.domain.common.container.CandidateDto
import com.retheviper.choseikun.domain.common.container.CandidateForm
import com.retheviper.choseikun.domain.common.container.CandidateParticipantDto
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
open class CandidateHandler(
    private val repository: CandidateRepository,
    private val candidateParticipantHandler: CandidateParticipantHandler,
    private val participantHandler: ParticipantHandler
) {

    private val id: String = "candidateId"

    internal fun getCandidates(appointmentId: Long): Flux<CandidateDto> {
        return repository.findAllByAppointmentId(appointmentId)
            .zipWith(getCandidateParticipantsByAppointmentId(appointmentId))
            .map { mapDto(it.t1, it.t2) }
    }

    internal fun createCandidate(form: CandidateForm): Mono<CandidateDto> =
        repository.save(
            Candidate(
                id = null,
                appointmentId = form.appointmentId,
                date = form.date,
            )
        ).map { mapDto(it, null) }

    fun createCandidate(request: ServerRequest): Mono<ServerResponse> =
        accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(CandidateForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap {
                    createCandidate(it)
                })

    fun updateCandidate(request: ServerRequest): Mono<ServerResponse> {
        val appointmentId = request.pathVariable(id).toLong()
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request.bodyToMono(CandidateForm::class.java)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap { form ->
                    repository.findById(appointmentId)
                        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map {
                            Candidate(
                                id = it.id,
                                appointmentId = form.appointmentId,
                                date = form.date
                            )
                        }
                }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.CONFLICT)))
                .flatMap {
                    repository.save(it)
                        .zipWith(getCandidateParticipantsByAppointmentId(appointmentId))
                        .map { mapDto(it.t1, it.t2) }
                })
    }

    private fun mapDto(candidate: Candidate, candidateParticipants: List<CandidateParticipantDto>?) =
        CandidateDto(
            appointmentId = candidate.appointmentId,
            date = candidate.date,
            recommend = candidateParticipants?.let { isRecommended(it) },
            participants = null // TODO
        )

    private fun isRecommended(candidateParticipant: List<CandidateParticipantDto>): Boolean {
        var ok = 0
        var ng = 0
        var pend = 0
        candidateParticipant.forEach {
            when (it.join) {
                Join.OK -> ok++
                Join.NG -> ng++
                Join.PEND -> pend++
            }
        }
        return ok > ng && pend < (candidateParticipant.size / 2)
    }

    private fun getCandidateParticipantsByAppointmentId(appointmentId: Long): Mono<MutableList<CandidateParticipantDto>> =
        repository.findAllByAppointmentId(appointmentId).flatMap {
            it.id?.let { id ->
                candidateParticipantHandler.getCandidateParticipantsByCandidateId(id)
            }
        }.collectList()
}