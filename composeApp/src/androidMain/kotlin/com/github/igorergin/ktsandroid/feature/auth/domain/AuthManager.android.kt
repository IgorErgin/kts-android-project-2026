package com.github.igorergin.ktsandroid.feature.auth.domain

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.github.igorergin.ktsandroid.MainActivity
import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

actual class AuthManager(private val context: Context) {

    private val _authCodeFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    actual val authCodeFlow: SharedFlow<String> = _authCodeFlow.asSharedFlow()

    private var authService: AuthorizationService? = null

    actual fun launchAuthFlow() {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(GithubAuthConfig.AUTH_ENDPOINT),
            Uri.parse(GithubAuthConfig.TOKEN_ENDPOINT)
        )

        val authRequest = AuthorizationRequest.Builder(
            serviceConfiguration,
            GithubAuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(GithubAuthConfig.REDIRECT_URI)
        ).setScope(GithubAuthConfig.SCOPES)
            .setCodeVerifier(null)
            .build()

        authService?.dispose()
        val currentService = AuthorizationService(context)
        authService = currentService

        val returnIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, returnIntent, pendingIntentFlags)

        currentService.performAuthorizationRequest(authRequest, pendingIntent, pendingIntent)
    }

    actual fun dispose() {
        authService?.dispose()
        authService = null
    }

    fun onAuthCodeReceived(code: String) {
        _authCodeFlow.tryEmit(code)
    }
}
