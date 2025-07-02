package com.developers.contentproviders.repository

import androidx.annotation.WorkerThread
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository class for managing Villains data operations
 * Abstracted repository as single source of truth for data operations
 */
class VillainsRepository(private val villainsDao: VillainsDao) {

    // Observed Flow will notify the observer when the data has changed.
    val allVillains: Flow<List<Villains>> = villainsDao.getAllVillainsFlow()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(villain: Villains): Long {
        return villainsDao.insert(villain)
    }

    @WorkerThread
    suspend fun update(villain: Villains): Int {
        return villainsDao.update(villain)
    }

    @WorkerThread
    suspend fun delete(villainId: Long): Int {
        return villainsDao.deleteById(villainId)
    }

    @WorkerThread
    suspend fun getAllVillains(): List<Villains> {
        return villainsDao.getAllVillains()
    }

    @WorkerThread
    suspend fun getVillainById(id: Long): Villains? {
        return villainsDao.getVillainById(id)
    }

    @WorkerThread
    suspend fun getVillainsCount(): Int {
        return villainsDao.count()
    }
}
