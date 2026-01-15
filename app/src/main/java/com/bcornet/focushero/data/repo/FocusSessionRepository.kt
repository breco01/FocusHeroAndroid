package com.bcornet.focushero.data.repo

import com.bcornet.focushero.data.db.FocusSessionDao
import com.bcornet.focushero.data.mappers.toDomain
import com.bcornet.focushero.data.mappers.toEntity
import com.bcornet.focushero.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FocusSessionRepository (
    private val dao: FocusSessionDao,
) {
    fun observeSessionsMostRecentFirst(): Flow<List<FocusSession>> {
        return dao.observeAllMostRecentFirst()
            .map { entities -> entities.map { it.toDomain() } }
    }

    fun observeTotalPoints(): Flow<Int> {
        return dao.observeTotalPoints()
    }

    suspend fun getSessionsMostRecentFirst(): List<FocusSession> {
        return dao.getAllMostRecentFirst().map { it.toDomain() }
    }

    suspend fun saveSession(session: FocusSession) {
        dao.upsert(session.toEntity())
    }

    suspend fun clearAllSessions() {
        dao.clearAll()
    }
}