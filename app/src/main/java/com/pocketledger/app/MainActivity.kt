package com.pocketledger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pocketledger.app.data.repository.LedgerRepository
import com.pocketledger.app.ui.components.BottomNavBar
import com.pocketledger.app.ui.components.Screen
import com.pocketledger.app.ui.screens.AnalyticsScreen
import com.pocketledger.app.ui.screens.HomeScreen
import com.pocketledger.app.ui.screens.SubscriptionsScreen
import com.pocketledger.app.ui.theme.PocketLedgerTheme
import com.pocketledger.app.ui.viewmodel.LedgerViewModel
import com.pocketledger.app.ui.viewmodel.LedgerViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: LedgerViewModel by viewModels {
        val app = application as PocketLedgerApplication
        val repo = LedgerRepository(
            transactionDao = app.database.transactionDao(),
            subscriptionDao = app.database.subscriptionDao()
        )
        LedgerViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketLedgerTheme {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }

                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            currentScreen = currentScreen,
                            onScreenSelected = { currentScreen = it }
                        )
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.HOME -> HomeScreen(viewModel = viewModel)
                            Screen.SUBSCRIPTIONS -> SubscriptionsScreen(viewModel = viewModel)
                            Screen.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
