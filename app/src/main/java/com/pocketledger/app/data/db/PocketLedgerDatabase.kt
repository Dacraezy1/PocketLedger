package com.pocketledger.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pocketledger.app.data.dao.SubscriptionDao
import com.pocketledger.app.data.dao.TransactionDao
import com.pocketledger.app.data.entity.SubscriptionEntity
import com.pocketledger.app.data.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, SubscriptionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PocketLedgerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: PocketLedgerDatabase? = null

        fun getDatabase(context: Context): PocketLedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PocketLedgerDatabase::class.java,
                    "pocket_ledger.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
