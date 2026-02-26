# Blood Donation Android App (Production-Style Kotlin + Firebase)

This repository now contains organization-grade Kotlin source files for an Android Studio app that includes:

- Firebase Authentication (Google + Apple)
- Firestore-based donor directory
- Realtime donor stream (only available donors)
- Stronger state management with `StateFlow` + one-shot UI events
- Smooth Jetpack Compose animations and polished UI components

## Project structure

- `app/src/main/java/com/example/blooddonation/MainActivity.kt`
- `app/src/main/java/com/example/blooddonation/App.kt`
- `app/src/main/java/com/example/blooddonation/AppConfig.kt`
- `app/src/main/java/com/example/blooddonation/BloodDonationViewModelFactory.kt`
- `app/src/main/java/com/example/blooddonation/ui/BloodDonationViewModel.kt`
- `app/src/main/java/com/example/blooddonation/ui/screens/LoginScreen.kt`
- `app/src/main/java/com/example/blooddonation/ui/screens/DonorDashboardScreen.kt`
- `app/src/main/java/com/example/blooddonation/data/AuthRepository.kt`
- `app/src/main/java/com/example/blooddonation/data/DonorRepository.kt`
- `app/src/main/java/com/example/blooddonation/data/Donor.kt`

## Firebase setup

1. Add `google-services.json` in your app module.
2. Enable Firebase Authentication providers:
   - Google
   - Apple (`apple.com`)
3. Create Firestore collection `donors`.
4. Update `AppConfig.GOOGLE_WEB_CLIENT_ID` with the Firebase Web client ID.

## Firestore donor schema

A donor document (document ID = Firebase `uid`) includes:
- `name`
- `email`
- `bloodGroup`
- `city`
- `phoneNumber`
- `isAvailable`
- `updatedAtEpochMillis`

## Recommended dependencies

- Firebase BOM
- `com.google.firebase:firebase-auth-ktx`
- `com.google.firebase:firebase-firestore-ktx`
- `com.google.android.gms:play-services-auth`
- Jetpack Compose Material3 + animation

