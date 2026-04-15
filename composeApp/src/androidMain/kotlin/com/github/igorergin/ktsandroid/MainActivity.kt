package com.github.igorergin.ktsandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.github.igorergin.ktsandroid.feature.auth.presentation.LocalAuthManager
import com.google.firebase.messaging.FirebaseMessaging
import com.github.igorergin.ktsandroid.core.notification.NotificationRepository
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.aakira.napier.Napier
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import kotlin.getValue

import com.github.igorergin.ktsandroid.feature.auth.domain.AuthManager

class MainActivity : ComponentActivity() {

    private val authManager: AuthManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(LocalAuthManager provides authManager) {
                App()
            }
        }

        handleIntent(intent)
        requestNotificationPermission()
        fetchAndSaveFcmToken()
    }

    private val notificationRepository: NotificationRepository by inject()

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun fetchAndSaveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Napier.w("Fetching FCM registration token failed", task.exception, tag = "FCM")
                return@addOnCompleteListener
            }

            val token = task.result
            Napier.d("FCM Token: $token", tag = "FCM")
            
            lifecycleScope.launch {
                notificationRepository.saveFcmToken(token)
            }
        }
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