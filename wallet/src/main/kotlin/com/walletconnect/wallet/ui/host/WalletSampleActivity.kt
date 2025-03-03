package com.walletconnect.wallet.ui.host

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.walletconnect.wallet.R
import com.walletconnect.wallet.SESSION_REQUEST_ARGS_NUM_KEY
import com.walletconnect.wallet.SESSION_REQUEST_KEY
import com.walletconnect.wallet.databinding.ActivityWalletBinding
import com.walletconnect.wallet.ui.SampleWalletEvents
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WalletSampleActivity : AppCompatActivity(R.layout.activity_wallet) {
    private val viewModel: WalletSampleViewModel by viewModels()
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fcvHost) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityWalletBinding.bind(findViewById(R.id.root))

        viewModel.events
            .flowWithLifecycle(lifecycle)
            .onEach { event ->
                when (event) {
                    is SampleWalletEvents.SessionProposal -> navController.navigate(R.id.action_global_to_session_proposal)
                    is SampleWalletEvents.SessionRequest -> {
                        navController.navigate(R.id.action_global_to_session_request, bundleOf(SESSION_REQUEST_KEY to event.arrayOfArgs, SESSION_REQUEST_ARGS_NUM_KEY to event.numOfArgs))
                    }
                    else -> Unit
                }
            }
            .launchIn(lifecycleScope)

        NavigationUI.setupActionBarWithNavController(this, navController, AppBarConfiguration(setOf(R.id.fragment_accounts, R.id.fragment_active_sessions)))
        binding.bnvTabs.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bnvTabs.isVisible = destination.id != R.id.fragment_scanner
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}