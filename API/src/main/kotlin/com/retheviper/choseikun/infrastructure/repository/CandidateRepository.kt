package com.retheviper.choseikun.infrastructure.repository

import com.retheviper.choseikun.domain.common.container.Candidate
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CandidateRepository : ReactiveCrudRepository<Candidate, Long> {
}