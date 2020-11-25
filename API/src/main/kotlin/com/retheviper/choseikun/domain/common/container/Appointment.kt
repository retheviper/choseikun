package com.retheviper.choseikun.domain.common.container

import org.springframework.data.annotation.Id
import java.time.LocalDate

/**
 * Entity
 */
data class Appointment(
    @Id var id: Long?,
    val title: String,
    val description: String?,
    val limit: LocalDate?
)

/**
 * Dto & View
 */
data class AppointmentDto(
    val title: String,
    val description: String?,
    val candidates: List<CandidateDto>?,
    val limit: LocalDate?
)

/**
 * Create & Update form
 */
data class AppointmentForm(
    val title: String,
    val description: String?,
    val candidates: List<CandidateForm>?,
    val limit: LocalDate?
)