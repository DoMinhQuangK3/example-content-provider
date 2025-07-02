package com.developers.contentproviders.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDatabase
import com.developers.contentproviders.repository.VillainsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing UI-related data in a lifecycle conscious way
 */
class VillainsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VillainsRepository
    val allVillains: LiveData<List<Villains>>

    init {
        val villainsDao = VillainsDatabase.getDatabase(application, viewModelScope).villainDao()
        repository = VillainsRepository(villainsDao)
        allVillains = repository.allVillains.asLiveData()
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(villain: Villains) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(villain)
    }

    fun update(villain: Villains) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(villain)
    }

    fun delete(villainId: Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(villainId)
    }

    suspend fun getVillainById(id: Long): Villains? {
        return repository.getVillainById(id)
    }
}
