package com.example.blooddonation.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.blooddonation.data.Donor

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DonorDashboardScreen(
    userName: String,
    userEmail: String,
    bloodGroup: String,
    city: String,
    phone: String,
    isAvailable: Boolean,
    donors: List<Donor>,
    onBloodGroupChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onAvailabilityChanged: (Boolean) -> Unit,
    onSaveProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFFAFA), Color(0xFFFFE0E0))))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Welcome, $userName", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(userEmail, style = MaterialTheme.typography.bodyMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = onBloodGroupChanged,
                        modifier = Modifier.weight(1f),
                        label = { Text("Blood Group") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = onCityChanged,
                        modifier = Modifier.weight(1f),
                        label = { Text("City") },
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Phone Number") },
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Available for donation", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isAvailable, onCheckedChange = onAvailabilityChanged)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onSaveProfile) { Text("Save Profile") }
                    Button(onClick = onLogout) { Text("Sign Out") }
                }
            }
        }

        Text("Available Donors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        AnimatedContent(
            targetState = donors,
            transitionSpec = {
                fadeIn(spring(stiffness = Spring.StiffnessLow)) togetherWith
                    fadeOut(spring(stiffness = Spring.StiffnessMedium))
            },
            label = "donor-list"
        ) { currentDonors ->
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                items(currentDonors, key = { it.id }) { donor ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(donor.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(donor.email, style = MaterialTheme.typography.bodyMedium)
                            Text("${donor.bloodGroup} • ${donor.city}", style = MaterialTheme.typography.bodySmall)
                            if (donor.phoneNumber.isNotBlank()) {
                                Text(donor.phoneNumber, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
