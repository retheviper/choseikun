package com.retheviper.choseikun.infrastructure.repository

import com.retheviper.choseikun.domain.common.container.Candidate
import com.retheviper.choseikun.domain.common.container.Participant
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ParticipantRepository : ReactiveCrudRepository<Participant, Long> {
    fun findAllByCandidateId(candidateId: Long): Flux<Participant>
}