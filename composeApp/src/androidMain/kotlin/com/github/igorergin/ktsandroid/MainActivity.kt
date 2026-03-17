package com.github.igorergin.ktsandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.github.igorergin.ktsandroid.core.database.DatabaseFactory
import com.github.igorergin.ktsandroid.core.datastore.AppSettings
import com.github.igorergin.ktsandroid.core.datastore.SecureStorage
import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import com.github.igorergin.ktsandroid.core.network.NetworkClient
import com.github.igorergin.ktsandroid.feature.auth.data.repository.GithubAuthRepository
import com.github.igorergin.ktsandroid.feature.auth.presentation.LocalAuthManager
import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import com.github.igorergin.ktsandroid.feature.repositories.data.repository.GithubRepoRepositoryImpl
import io.github.aakira.napier.Napier
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class MainActivity : ComponentActivity() {

    private val authManager by lazy { AndroidAuthManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appSettings = AppSettings(applicationContext)
        val secureStorage = SecureStorage(applicationContext)
        val tokenManager = TokenManager(secureStorage)
        val httpClient = NetworkClient.createHttpClient(tokenManager)

        val db = DatabaseFactory(applicationContext).createBuilder().build()
        val repoDao = db.repositoryDao()

        val githubRepoRepository = GithubRepoRepositoryImpl(httpClient, repoDao)
        val profileRepository = ProfileRepository(httpClient)
        val githubAuthRepository = GithubAuthRepository(httpClient, tokenManager)
        val detailRepository = DetailRepository(httpClient)

        setContent {
            CompositionLocalProvider(LocalAuthManager provides authManager) {
                App(
                    appSettings = appSettings,
                    tokenManager = tokenManager,
                    githubRepoRepository = githubRepoRepository,
                    profileRepository = profileRepository,
                    githubAuthRepository = githubAuthRepository,
                    detailRepository = detailRepository
                )
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
                val code = response.authorizationCode
                if (code != null) {
                    authManager.onAuthCodeReceived(code)
                }
            }

            error != null -> {
                Napier.e(
                    message = "Ошибка при получении OAuth ответа из Intent",
                    throwable = error,
                    tag = "Auth-Intent"
                )
            }
        }
    }
}