package com.example.app_nueva_panchovilla.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_nueva_panchovilla.viewmodel.TrabajoViewModel

class TrabajoViewModelFactory(
    private val repository: TrabajoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrabajoViewModel::class.java)) {
            return TrabajoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}