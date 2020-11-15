package com.retheviper.choseikun.domain.common.container

import org.springframework.data.annotation.Id
import java.time.LocalDate

data class Appointment(
    @Id var id: Long?,
    val title: String,
    val description: String?,
    val candidates: List<String>,
    val limit: LocalDate?
)

data class AppointmentDto(
    val title: String,
    val description: String?,
    val candidates: List<String>,
    val limit: LocalDate?
)

data class AppointmentForm(
    val title: String,
    val description: String?,
    val candidates: List<String>,
    val limit: LocalDate?
)