package com.retheviper.choseikun.infrastructure.repository

import com.retheviper.choseikun.domain.common.container.CandidateParticipant
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface CandidateParticipantRepository : ReactiveCrudRepository<CandidateParticipant, Long> {
    fun findAllByCandidateId(candidateId: Long): Flux<CandidateParticipant>
    fun findAllByParticipantsId(participantsId: Long): Flux<CandidateParticipant>
}