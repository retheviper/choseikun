package com.retheviper.choseikun.infrastructure.repository

import com.retheviper.choseikun.domain.common.container.Appointment
import com.retheviper.choseikun.domain.common.container.Candidate
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface CandidateRepository : ReactiveCrudRepository<Candidate, Long> {
    fun findAllByAppointmentId(appointmentId: Long): Flux<Candidate>
}