package com.pocketledger.app

import android.app.Application
import com.pocketledger.app.data.db.PocketLedgerDatabase

class PocketLedgerApplication : Application() {
    val database: PocketLedgerDatabase by lazy {
        PocketLedgerDatabase.getDatabase(this)
    }
}
