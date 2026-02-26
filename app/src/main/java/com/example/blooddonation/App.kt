package com.example.blooddonation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collect
import androidx.compose.ui.platform.LocalContext
import com.example.blooddonation.data.AuthRepository
import com.example.blooddonation.ui.BloodDonationViewModel
import com.example.blooddonation.ui.UiEvent
import com.example.blooddonation.ui.screens.DonorDashboardScreen
import com.example.blooddonation.ui.screens.LoginScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BloodDonationApp(
    viewModel: BloodDonationViewModel,
    authRepository: AuthRepository,
    googleWebClientId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var transientError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Error -> transientError = event.message
                is UiEvent.Success -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
        val idToken = account?.idToken
        if (!idToken.isNullOrBlank()) {
            viewModel.signInWithGoogle(idToken)
        } else {
            transientError = "Google login failed. Please try again."
        }
    }

    AnimatedContent(
        targetState = uiState.isAuthenticated,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "auth-navigation"
    ) { isAuthenticated ->
        if (!isAuthenticated) {
            LoginScreen(
                isLoading = uiState.isLoading,
                onGoogleLoginClick = {
                    val options = authRepository.buildGoogleSignInOptions(googleWebClientId)
                    val client = GoogleSignIn.getClient(context, options)
                    googleSignInLauncher.launch(client.signInIntent)
                },
                onAppleLoginClick = {
                    val activity = context as? Activity
                    if (activity == null) {
                        transientError = "Unable to start Apple login from this context."
                    } else {
                        viewModel.signInWithApple(activity)
                    }
                },
                errorMessage = transientError
            )
        } else {
            transientError = null
            DonorDashboardScreen(
                userName = uiState.userName,
                userEmail = uiState.userEmail,
                bloodGroup = uiState.bloodGroupInput,
                city = uiState.cityInput,
                phone = uiState.phoneInput,
                isAvailable = uiState.isAvailableInput,
                donors = uiState.donors,
                onBloodGroupChanged = viewModel::onBloodGroupChanged,
                onCityChanged = viewModel::onCityChanged,
                onPhoneChanged = viewModel::onPhoneChanged,
                onAvailabilityChanged = viewModel::onAvailabilityChanged,
                onSaveProfile = viewModel::saveDonorProfile,
                onLogout = viewModel::signOut
            )
        }
    }
}
