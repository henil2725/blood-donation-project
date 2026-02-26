package com.example.blooddonation.data

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUser
    suspend fun signInWithApple(activity: Activity): FirebaseUser
    fun buildGoogleSignInOptions(webClientId: String): GoogleSignInOptions
    fun signOut()
}

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUser {
        require(idToken.isNotBlank()) { "Missing Google ID token." }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth
            .signInWithCredential(credential)
            .await()
            .user
            ?: throw IllegalStateException("Google sign-in completed without a Firebase user.")
    }

    override suspend fun signInWithApple(activity: Activity): FirebaseUser {
        val provider = OAuthProvider.newBuilder(APPLE_PROVIDER_ID)
            .setScopes(listOf("email", "name"))
            .build()

        val result = firebaseAuth.pendingAuthResult
            ?: firebaseAuth.startActivityForSignInWithProvider(activity, provider).await()

        return result.user
            ?: throw IllegalStateException("Apple sign-in completed without a Firebase user.")
    }

    override fun buildGoogleSignInOptions(webClientId: String): GoogleSignInOptions {
        require(webClientId.isNotBlank()) { "Google web client ID must not be blank." }
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(webClientId)
            .build()
    }

    override fun signOut() = firebaseAuth.signOut()

    private companion object {
        private const val APPLE_PROVIDER_ID = "apple.com"
    }
}
