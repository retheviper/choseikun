package com.retheviper.choseikun.domain.common.container

import com.retheviper.choseikun.domain.common.value.Join
import org.springframework.data.annotation.Id

data class Participant(
    @Id var id: Long?,
    val candidate: Candidate,
    val name: String,
    val join: Join,
    val comment: String
)

data class ParticipantDto(
    val name: String,
    val candidate: Candidate,
    val join: Join,
    val comment: String
)

data class ParticipantForm(
    val name: String,
    val candidate: Candidate,
    val join: Join,
    val comment: String
)