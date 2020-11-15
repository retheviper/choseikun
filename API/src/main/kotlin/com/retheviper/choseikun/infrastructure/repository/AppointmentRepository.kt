package com.retheviper.choseikun.infrastructure.repository

import com.retheviper.choseikun.domain.common.container.Appointment
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface AppointmentRepository : ReactiveCrudRepository<Appointment, Long> {
}