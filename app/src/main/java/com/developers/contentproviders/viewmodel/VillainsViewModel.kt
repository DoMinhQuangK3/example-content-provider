package com.developers.contentproviders.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.developers.contentproviders.data.Villains
import com.developers.contentproviders.data.VillainsDatabase
import com.developers.contentproviders.repository.VillainsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel for managing UI-related data in a lifecycle conscious way
 */
class VillainsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VillainsRepository
    
    private val _uiState = MutableLiveData<UiState<List<Villains>>>()
    val uiState: LiveData<UiState<List<Villains>>> = _uiState

    init {
        val villainsDao = VillainsDatabase.getDatabase(application, viewModelScope).villainDao()
        repository = VillainsRepository(villainsDao)
        loadVillains()
    }

    /**
     * Load villains with proper state management
     */
    private fun loadVillains() {
        viewModelScope.launch {
            repository.allVillains
                .onStart { _uiState.value = UiState.Loading }
                .catch { exception -> 
                    _uiState.value = UiState.Error(exception)
                }
                .collect { villains ->
                    _uiState.value = UiState.Success(villains)
                }
        }
    }

    /**
     * Refresh data
     */
    fun refresh() {
        loadVillains()
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(villain: Villains) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.insert(villain)
        } catch (e: Exception) {
            _uiState.postValue(UiState.Error(e))
        }
    }

    fun update(villain: Villains) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.update(villain)
        } catch (e: Exception) {
            _uiState.postValue(UiState.Error(e))
        }
    }

    fun delete(villainId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.delete(villainId)
        } catch (e: Exception) {
            _uiState.postValue(UiState.Error(e))
        }
    }

    suspend fun getVillainById(id: Long): Villains? {
        return try {
            repository.getVillainById(id)
        } catch (e: Exception) {
            _uiState.postValue(UiState.Error(e))
            null
        }
    }
}
