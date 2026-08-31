package com.pocketledger.app.data.repository

import com.pocketledger.app.data.dao.SubscriptionDao
import com.pocketledger.app.data.dao.TransactionDao
import com.pocketledger.app.data.entity.SubscriptionEntity
import com.pocketledger.app.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class LedgerRepository(
    private val transactionDao: TransactionDao,
    private val subscriptionDao: SubscriptionDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allSubscriptions: Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    suspend fun insertSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.insertSubscription(subscription)
    }

    suspend fun deleteSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.deleteSubscription(subscription)
    }
}
