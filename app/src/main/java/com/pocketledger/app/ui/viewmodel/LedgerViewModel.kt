package com.pocketledger.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pocketledger.app.data.entity.BillingCycle
import com.pocketledger.app.data.entity.SubscriptionEntity
import com.pocketledger.app.data.entity.TransactionCategory
import com.pocketledger.app.data.entity.TransactionEntity
import com.pocketledger.app.data.entity.TransactionType
import com.pocketledger.app.data.repository.LedgerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LedgerSummary(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val monthlySubscriptionCost: Double = 0.0
)

class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: StateFlow<LedgerSummary> = transactions.map { list ->
        var income = 0.0
        var expense = 0.0
        for (tx in list) {
            if (tx.type == TransactionType.INCOME) {
                income += tx.amount
            } else {
                expense += tx.amount
            }
        }
        val balance = income - expense
        LedgerSummary(
            totalBalance = balance,
            totalIncome = income,
            totalExpense = expense
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LedgerSummary())

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        note: String
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                title = title,
                amount = amount,
                type = type,
                category = category,
                note = note
            )
            repository.insertTransaction(tx)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addSubscription(
        name: String,
        cost: Double,
        billingCycle: BillingCycle,
        renewalDay: Int
    ) {
        viewModelScope.launch {
            val sub = SubscriptionEntity(
                name = name,
                cost = cost,
                billingCycle = billingCycle,
                renewalDayOfMonth = renewalDay
            )
            repository.insertSubscription(sub)
        }
    }

    fun deleteSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }
}

class LedgerViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LedgerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LedgerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
