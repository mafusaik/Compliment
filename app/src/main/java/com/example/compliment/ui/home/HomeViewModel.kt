package com.example.compliment.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compliment.data.repositories.ComplimentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ComplimentsRepository): ViewModel() {

    private val _currentComplimentFlow = MutableStateFlow("")
    val currentComplimentFlow: StateFlow<String> = _currentComplimentFlow

//    init {
//        getCompliment("")
//    }

    fun getCompliment(lastCompliment: String){
        viewModelScope.launch {
           val compliment = repository.getCompliment()
            _currentComplimentFlow.emit(compliment)
        }
    }

    fun setCompliment(compliment: String){
        viewModelScope.launch {
            _currentComplimentFlow.emit(compliment)
        }
    }
}