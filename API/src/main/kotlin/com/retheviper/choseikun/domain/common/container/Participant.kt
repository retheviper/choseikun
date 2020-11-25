package com.retheviper.choseikun.domain.common.container

import com.retheviper.choseikun.domain.common.value.Join
import org.springframework.data.annotation.Id

/**
 * Entity
 */
data class Participant(
    @Id var id: Long?,
    val name: String,
    val comment: String
)

/**
 * Dto & View
 */
data class ParticipantDto(
    val name: String,
    val comment: String,
    val candidateParticipants: List<CandidateParticipantDto>
)

/**
 * Create & Update form
 */
data class ParticipantForm(
    val name: String,
    val comment: String,
    val candidateParticipants: List<CandidateParticipantForm>
)