package com.retheviper.choseikun.infrastructure.repository

import com.retheviper.choseikun.domain.common.container.Participant
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ParticipantRepository : ReactiveCrudRepository<Participant, Long> {
}