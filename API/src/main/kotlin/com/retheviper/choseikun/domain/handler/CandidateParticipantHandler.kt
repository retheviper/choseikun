package com.retheviper.choseikun.domain.handler

import com.retheviper.choseikun.domain.common.container.CandidateParticipant
import com.retheviper.choseikun.domain.common.container.CandidateParticipantDto
import com.retheviper.choseikun.domain.common.container.CandidateParticipantForm
import com.retheviper.choseikun.infrastructure.repository.CandidateParticipantRepository
import org.springframework.stereotype.Component

@Component
open class CandidateParticipantHandler(
    private val repository: CandidateParticipantRepository
) {

    internal fun createCandidateParticipant(form: CandidateParticipantForm) =
        repository.save(
            CandidateParticipant(
                id = null,
                candidateId = form.candidateId,
                participantId = form.participantId,
                join = form.join
            )
        ).map { mapDto(it) }

    fun getCandidateParticipantsByCandidateId(candidateId: Long) =
        repository.findAllByCandidateId(candidateId).map(this::mapDto)

    fun getCandidateParticipantsByParticipantsId(participantsId: Long) =
        repository.findAllByParticipantsId(participantsId).map(this::mapDto)

    private fun mapDto(entity: CandidateParticipant) =
        CandidateParticipantDto(
            candidateId = entity.candidateId,
            participantId = entity.participantId,
            join = entity.join
        )
}