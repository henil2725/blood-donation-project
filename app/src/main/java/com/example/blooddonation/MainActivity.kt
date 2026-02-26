package com.example.blooddonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blooddonation.data.FirebaseAuthRepository
import com.example.blooddonation.data.FirestoreDonorRepository
import com.example.blooddonation.ui.BloodDonationViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val authRepository = FirebaseAuthRepository(FirebaseAuth.getInstance())
        val donorRepository = FirestoreDonorRepository(FirebaseFirestore.getInstance())

        setContent {
            val viewModel: BloodDonationViewModel = viewModel(
                factory = BloodDonationViewModelFactory(
                    authRepository = authRepository,
                    donorRepository = donorRepository
                )
            )

            BloodDonationApp(
                viewModel = viewModel,
                authRepository = authRepository,
                googleWebClientId = AppConfig.GOOGLE_WEB_CLIENT_ID
            )
        }
    }
}
