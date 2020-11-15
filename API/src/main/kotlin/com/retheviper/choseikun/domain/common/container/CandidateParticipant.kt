package com.retheviper.choseikun.domain.common.container

import com.retheviper.choseikun.domain.common.value.Join
import org.springframework.data.annotation.Id

data class CandidateParticipant(
    @Id var id: Long?,
    val candidateId: Long,
    val participantId: Long,
    val join: Join
)

data class CandidateParticipantDto(
    val candidateId: Long,
    val participantId: Long,
    val join: Join
)

data class CandidateParticipantForm(
    val candidateId: Long,
    val participantId: Long,
    val join: Join
)