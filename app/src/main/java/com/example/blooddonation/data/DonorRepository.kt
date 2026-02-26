package com.example.blooddonation.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface DonorRepository {
    fun observeAvailableDonors(): Flow<List<Donor>>
    suspend fun upsertDonor(donor: Donor)
}

class FirestoreDonorRepository(
    private val firestore: FirebaseFirestore
) : DonorRepository {

    override fun observeAvailableDonors(): Flow<List<Donor>> = callbackFlow {
        val registration = firestore.collection(COLLECTION_DONORS)
            .whereEqualTo(FIELD_IS_AVAILABLE, true)
            .orderBy(FIELD_UPDATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val donors = snapshot
                    ?.documents
                    .orEmpty()
                    .mapNotNull { document ->
                        document.toObject<Donor>()?.copy(id = document.id)
                    }

                trySend(donors)
            }

        awaitClose { registration.remove() }
    }

    override suspend fun upsertDonor(donor: Donor) {
        require(donor.id.isNotBlank()) { "Donor ID is required for upsert." }
        firestore.collection(COLLECTION_DONORS)
            .document(donor.id)
            .set(donor)
            .await()
    }

    private companion object {
        private const val COLLECTION_DONORS = "donors"
        private const val FIELD_IS_AVAILABLE = "isAvailable"
        private const val FIELD_UPDATED_AT = "updatedAtEpochMillis"
    }
}
