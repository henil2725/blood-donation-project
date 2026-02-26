package com.example.blooddonation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blooddonation.data.AuthRepository
import com.example.blooddonation.data.DonorRepository
import com.example.blooddonation.ui.BloodDonationViewModel

class BloodDonationViewModelFactory(
    private val authRepository: AuthRepository,
    private val donorRepository: DonorRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(BloodDonationViewModel::class.java)) {
            "Unsupported ViewModel class: ${modelClass.name}"
        }
        @Suppress("UNCHECKED_CAST")
        return BloodDonationViewModel(authRepository, donorRepository) as T
    }
}
