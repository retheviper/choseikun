package com.retheviper.choseikun.domain.common.container

import org.springframework.data.annotation.Id

/**
 * Entity
 */
data class Candidate(
    @Id var id: Long?,
    val appointmentId: Long,
    val date: String,
)

/**
 * Dto & View
 */
data class CandidateDto(
    val appointmentId: Long,
    val date: String,
    val recommend: Boolean?,
    val participants: List<ParticipantDto>?
)

/**
 * Create & Update form
 */
data class CandidateForm(
    val appointmentId: Long,
    val date: String
)