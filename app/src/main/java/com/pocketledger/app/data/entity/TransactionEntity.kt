package com.pocketledger.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionCategory {
    FOOD, TECH, BILLS, SHOPPING, TRANSPORT, SALARY, ENTERTAINMENT, OTHER
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
