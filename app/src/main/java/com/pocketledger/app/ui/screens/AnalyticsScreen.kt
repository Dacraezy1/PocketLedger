package com.pocketledger.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketledger.app.data.entity.TransactionCategory
import com.pocketledger.app.data.entity.TransactionType
import com.pocketledger.app.ui.theme.AccentPurple
import com.pocketledger.app.ui.theme.GreenPrimary
import com.pocketledger.app.ui.viewmodel.LedgerViewModel
import java.util.Locale

@Composable
fun AnalyticsScreen(viewModel: LedgerViewModel) {
    val transactions by viewModel.transactions.collectAsState()

    val categoryTotals = remember(transactions) {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val totalExpenseAmount = expenses.sumOf { it.amount }
        TransactionCategory.entries.map { cat ->
            val catAmount = expenses.filter { it.category == cat }.sumOf { it.amount }
            val percentage = if (totalExpenseAmount > 0) (catAmount / totalExpenseAmount * 100).toFloat() else 0f
            Triple(cat, catAmount, percentage)
        }.filter { it.second > 0 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Analytics & Privacy",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Privacy Guarantee Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(GreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "100% Offline & Private",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Your financial data is stored encrypted locally in SQLite. Zero cloud tracking or analytics.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Text(
            text = "Expense Categories",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (categoryTotals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No expenses recorded yet.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categoryTotals) { (cat, amount, pct) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = cat.name, fontWeight = FontWeight.SemiBold)
                            Text(text = "$${String.format(Locale.US, "%.2f", amount)} (${String.format(Locale.US, "%.1f", pct)}%)")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { pct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AccentPurple,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}
