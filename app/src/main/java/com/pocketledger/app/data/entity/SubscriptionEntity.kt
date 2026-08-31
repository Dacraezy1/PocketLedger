package com.pocketledger.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BillingCycle {
    MONTHLY, YEARLY
}

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val cost: Double,
    val billingCycle: BillingCycle,
    val renewalDayOfMonth: Int, // e.g. 15 for 15th of every month
    val category: String = "Entertainment",
    val isActive: Boolean = true
)
