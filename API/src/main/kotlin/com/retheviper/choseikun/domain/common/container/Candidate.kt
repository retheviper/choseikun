package com.retheviper.choseikun.domain.common.container

import com.retheviper.choseikun.domain.common.value.Join
import org.springframework.data.annotation.Id

data class Candidate(
    @Id var id: Long?,
    val appointmentId: Long,
    val participants: List<Participant>?,
    val date: String
)

data class CandidateDto(
    val appointmentId: Long,
    val participants: List<ParticipantDto>?,
    val date: String,
    val recommend: Boolean?
)

data class CandidateForm(
    val appointmentId: Long,
    val date: String
)