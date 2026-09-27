package com.structiq.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.UserProfileEntity
import com.structiq.app.core.model.UserRole
import com.structiq.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _selectedRole = MutableStateFlow(UserRole.CONTRACTOR)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _companyName = MutableStateFlow("")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    fun onRoleSelected(role: UserRole) {
        _selectedRole.value = role
    }

    fun onFullNameChanged(name: String) {
        _fullName.value = name
    }

    fun onCompanyNameChanged(name: String) {
        _companyName.value = name
    }

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val profile = UserProfileEntity(
                id = "user_default",
                fullName = _fullName.value.ifBlank { "Eng. Professional" },
                role = _selectedRole.value,
                companyName = _companyName.value.ifBlank { "Civil Engineering Firm" },
                email = _email.value.ifBlank { "user@structiq.app" },
                phone = "",
                isOnboardingCompleted = true
            )
            userRepository.updateProfile(profile)
            onSuccess()
        }
    }
}
