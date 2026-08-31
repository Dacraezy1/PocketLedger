package com.pocketledger.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketledger.app.data.entity.SubscriptionEntity
import com.pocketledger.app.ui.components.AddSubscriptionDialog
import com.pocketledger.app.ui.theme.AccentPurple
import com.pocketledger.app.ui.theme.GreenPrimary
import com.pocketledger.app.ui.viewmodel.LedgerViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun SubscriptionsScreen(viewModel: LedgerViewModel) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val monthlyTotal = remember(subscriptions) {
        subscriptions.sumOf { it.cost }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subscription")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Subscriptions",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Monthly Total Subscription Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Monthly Recurring",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", monthlyTotal)}/mo",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurple
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = AccentPurple)
                    }
                }
            }

            Text(
                text = "Active Subscriptions (${subscriptions.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (subscriptions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active subscriptions tracked.\nTap + to add recurring bills (Netflix, Spotify, etc.)",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subscriptions, key = { it.id }) { sub ->
                        SubscriptionItem(
                            subscription = sub,
                            onDelete = { viewModel.deleteSubscription(sub) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSubscriptionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cost, cycle, day ->
                viewModel.addSubscription(name, cost, cycle, day)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SubscriptionItem(subscription: SubscriptionEntity, onDelete: () -> Unit) {
    val daysUntilRenewal = remember(subscription.renewalDayOfMonth) {
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)
        if (subscription.renewalDayOfMonth >= currentDay) {
            subscription.renewalDayOfMonth - currentDay
        } else {
            val maxDay = today.getActualMaximum(Calendar.DAY_OF_MONTH)
            (maxDay - currentDay) + subscription.renewalDayOfMonth
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = subscription.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Renews in $daysUntilRenewal days (Day ${subscription.renewalDayOfMonth})",
                    fontSize = 12.sp,
                    color = if (daysUntilRenewal <= 3) GreenPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${String.format(Locale.US, "%.2f", subscription.cost)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AccentPurple
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
