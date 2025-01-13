package com.mgj.recepturasi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgj.recepturasi.network.LoginResponse
import com.mgj.recepturasi.network.UserCreateRequest
import com.mgj.recepturasi.network.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<Result<LoginResponse>>()
    val loginResponse: LiveData<Result<LoginResponse>> get() = _loginResponse

    private val _signUpResponse = MutableLiveData<Result<String>>()
    val signUpResponse: LiveData<Result<String>> get() = _signUpResponse

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    /**
     * Handles the login logic.
     */
    fun login(email: String, password: String) {
        _loadingState.value = true // Indicate loading
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        _loginResponse.value = Result.success(loginResponse)
                    } else {
                        _loginResponse.value = Result.failure(Exception("Unexpected response format."))
                    }
                } else {
                    _loginResponse.value = Result.failure(Exception("Login failed: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _loginResponse.value = Result.failure(e)
            } finally {
                _loadingState.value = false // Loading complete
            }
        }
    }

    /**
     * Handles the sign-up logic.
     */
    fun signUp(user: UserCreateRequest) {
        _loadingState.value = true // Indicate loading
        viewModelScope.launch {
            try {
                val response = repository.signUp(user)
                if (response.isSuccessful) {
                    _signUpResponse.value = Result.success("Sign-up successful!")
                } else {
                    _signUpResponse.value = Result.failure(Exception("Sign-up failed: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _signUpResponse.value = Result.failure(e)
            } finally {
                _loadingState.value = false // Loading complete
            }
        }
    }
}
