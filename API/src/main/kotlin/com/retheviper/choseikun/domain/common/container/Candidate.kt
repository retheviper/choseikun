package com.retheviper.choseikun.domain.common.container

import com.retheviper.choseikun.domain.common.value.Join
import org.springframework.data.annotation.Id

data class Candidate(
    @Id var id: Long?,
    val appointmentId: Long,
    val date: String,
    val candidateParticipants: List<CandidateParticipant>?
)

data class CandidateDto(
    val appointmentId: Long,
    val date: String,
    val recommend: Boolean?,
    val candidateParticipants: List<CandidateParticipantDto>?
)

data class CandidateForm(
    val appointmentId: Long,
    val date: String
)