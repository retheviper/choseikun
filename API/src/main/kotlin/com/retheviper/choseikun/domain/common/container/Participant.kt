package com.retheviper.choseikun.domain.common.container

import com.retheviper.choseikun.domain.common.value.Join
import org.springframework.data.annotation.Id

data class Participant(
    @Id var id: Long?,
    val candidateParticipants: List<CandidateParticipant>?,
    val name: String,
    val comment: String
)

data class ParticipantDto(
    val name: String,
    val comment: String,
    val candidateParticipants: List<CandidateParticipantDto>?
)

data class ParticipantForm(
    val name: String,
    val comment: String,
    val candidateParticipants: List<CandidateParticipantForm>?
)