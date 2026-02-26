package com.example.blooddonation.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blooddonation.data.AuthRepository
import com.example.blooddonation.data.Donor
import com.example.blooddonation.data.DonorRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BloodDonationUiState(
    val userName: String = "",
    val userEmail: String = "",
    val donors: List<Donor> = emptyList(),
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val bloodGroupInput: String = "",
    val cityInput: String = "",
    val phoneInput: String = "",
    val isAvailableInput: Boolean = true
)

sealed interface UiEvent {
    data class Error(val message: String) : UiEvent
    data class Success(val message: String) : UiEvent
}

class BloodDonationViewModel(
    private val authRepository: AuthRepository,
    private val donorRepository: DonorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        authRepository.currentUser.toUiState()
    )
    val uiState: StateFlow<BloodDonationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        observeDonors()
    }

    fun onBloodGroupChanged(value: String) = _uiState.update { it.copy(bloodGroupInput = value) }
    fun onCityChanged(value: String) = _uiState.update { it.copy(cityInput = value) }
    fun onPhoneChanged(value: String) = _uiState.update { it.copy(phoneInput = value) }
    fun onAvailabilityChanged(value: Boolean) = _uiState.update { it.copy(isAvailableInput = value) }

    fun signInWithGoogle(idToken: String) {
        launchAuthAction { authRepository.signInWithGoogleIdToken(idToken) }
    }

    fun signInWithApple(activity: Activity) {
        launchAuthAction { authRepository.signInWithApple(activity) }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = BloodDonationUiState()
    }

    fun saveDonorProfile() {
        val state = _uiState.value
        if (!state.isAuthenticated) {
            emitEvent(UiEvent.Error("Please sign in first."))
            return
        }

        if (state.bloodGroupInput.isBlank() || state.cityInput.isBlank()) {
            emitEvent(UiEvent.Error("Blood group and city are required."))
            return
        }

        val user = authRepository.currentUser ?: run {
            emitEvent(UiEvent.Error("Session expired. Please sign in again."))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                donorRepository.upsertDonor(
                    Donor(
                        id = user.uid,
                        name = user.displayName.orEmpty(),
                        email = user.email.orEmpty(),
                        bloodGroup = state.bloodGroupInput.trim(),
                        city = state.cityInput.trim(),
                        phoneNumber = state.phoneInput.trim(),
                        isAvailable = state.isAvailableInput,
                        updatedAtEpochMillis = System.currentTimeMillis()
                    )
                )
            }.onSuccess {
                emitEvent(UiEvent.Success("Donor profile saved."))
            }.onFailure {
                emitEvent(UiEvent.Error(it.message ?: "Unable to save donor profile."))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun launchAuthAction(action: suspend () -> com.google.firebase.auth.FirebaseUser) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { action() }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            userName = user.displayName.orEmpty().ifBlank { "Donor" },
                            userEmail = user.email.orEmpty(),
                            isLoading = false
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false) }
                    emitEvent(UiEvent.Error(throwable.message ?: "Authentication failed."))
                }
        }
    }

    private fun observeDonors() {
        viewModelScope.launch {
            donorRepository.observeAvailableDonors()
                .catch { emitEvent(UiEvent.Error(it.message ?: "Unable to load donors.")) }
                .collect { donors -> _uiState.update { it.copy(donors = donors) } }
        }
    }

    private fun emitEvent(event: UiEvent) {
        viewModelScope.launch { _events.emit(event) }
    }

    private fun com.google.firebase.auth.FirebaseUser?.toUiState(): BloodDonationUiState {
        if (this == null) return BloodDonationUiState()
        return BloodDonationUiState(
            isAuthenticated = true,
            userName = displayName.orEmpty().ifBlank { "Donor" },
            userEmail = email.orEmpty()
        )
    }
}
