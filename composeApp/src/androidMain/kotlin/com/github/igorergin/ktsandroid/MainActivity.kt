package com.github.igorergin.ktsandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.github.igorergin.ktsandroid.core.util.AndroidAuthManager
import com.github.igorergin.ktsandroid.feature.auth.presentation.LocalAuthManager
import io.github.aakira.napier.Napier
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class MainActivity : ComponentActivity() {

    private val authManager by lazy { AndroidAuthManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(LocalAuthManager provides authManager) {
                App()
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        authManager.dispose()
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val response = AuthorizationResponse.fromIntent(intent)
        val error = AuthorizationException.fromIntent(intent)

        when {
            response != null -> {
                response.authorizationCode?.let { code ->
                    authManager.onAuthCodeReceived(code)
                }
            }
            error != null -> {
                Napier.e("OAuth Intent Error", error, tag = "Auth-Intent")
            }
        }
    }
}