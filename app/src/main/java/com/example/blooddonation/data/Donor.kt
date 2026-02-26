package com.example.blooddonation.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Donor(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val phoneNumber: String = "",
    val isAvailable: Boolean = true,
    val updatedAtEpochMillis: Long = 0L
)
