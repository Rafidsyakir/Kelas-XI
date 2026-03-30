package com.example.trifhop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trifhop.data.model.LoginResponse
import com.example.trifhop.data.repository.AuthRepository
import com.example.trifhop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Login dan Register
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // Login State
    private val _loginState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState
    
    // Register State
    private val _registerState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val registerState: StateFlow<Resource<LoginResponse>?> = _registerState
    
    // Logout State
    private val _logoutState = MutableStateFlow<Resource<Unit>?>(null)
    val logoutState: StateFlow<Resource<Unit>?> = _logoutState
    
    /**
     * Login user
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = authRepository.login(email, password)
            _loginState.value = result
        }
    }
    
    /**
     * Register user
     */
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val result = authRepository.register(name, email, password)
            _registerState.value = result
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            _logoutState.value = Resource.Loading()
            val result = authRepository.logout()
            _logoutState.value = result
        }
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    /**
     * Reset login state
     */
    fun resetLoginState() {
        _loginState.value = null
    }
    
    /**
     * Reset register state
     */
    fun resetRegisterState() {
        _registerState.value = null
    }
}
