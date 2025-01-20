package com.example.compliment.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compliment.data.clipboard.SystemClipboard
import com.example.compliment.data.repositories.ComplimentsRepository
import com.example.compliment.models.HomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ComplimentsRepository,
    private val clipboard: SystemClipboard,
) : ViewModel() {

    private val _isTextVisible = MutableStateFlow(true)

    val state: StateFlow<HomeState> = _isTextVisible
        .combine(repository.currentCompliment().distinctUntilChanged()) { isTextVisible, compliment ->
            Log.i("RecompositionTracker", "viewmodel state $compliment")
            HomeState(isTextVisible, compliment)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000), HomeState.Initial)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restoreCompliments()
        }
    }

    fun onGetComplimentClicked() {
        viewModelScope.launch {
            _isTextVisible.value = false
            delay(300)

            repository.nextCompliment()
            _isTextVisible.value = true
        }
    }

    fun onComplimentClicked() {
        clipboard.copyToClipboard(state.value.compliment)
    }

    fun setInitCompliment(compliment: String) {
      repository.setCompliment(compliment)
    }

}