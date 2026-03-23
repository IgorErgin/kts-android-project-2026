package com.github.igorergin.ktsandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.github.igorergin.ktsandroid.core.database.DatabaseFactory
import com.github.igorergin.ktsandroid.core.datastore.AppSettings
import com.github.igorergin.ktsandroid.core.datastore.AndroidSecureStorage
import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.datastore.createAndroidDataStore
import com.github.igorergin.ktsandroid.core.network.NetworkClient
import com.github.igorergin.ktsandroid.feature.auth.data.api.AuthApiImpl
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

        // Создаем два разных хранилища для разных целей
        val settingsDataStore = createAndroidDataStore(applicationContext, "settings")
        val tokensDataStore = createAndroidDataStore(applicationContext, "secure_tokens")

        val appSettings = AppSettings(settingsDataStore)
        val secureStorage = AndroidSecureStorage(tokensDataStore)
        val tokenManager = TokenManager(secureStorage)

        val unauthClient = NetworkClient.unauthenticatedClient
        val authApi = AuthApiImpl(unauthClient)
        val httpClient = NetworkClient.createHttpClient(tokenManager, authApi)

        val db = DatabaseFactory(applicationContext).createBuilder().build()
        val repoDao = db.repositoryDao()

        val githubRepoRepository = GithubRepoRepositoryImpl(httpClient, repoDao)
        val profileRepository = ProfileRepository(httpClient)
        val githubAuthRepository = GithubAuthRepository(authApi, tokenManager, githubRepoRepository)
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
            response != null -> response.authorizationCode?.let { authManager.onAuthCodeReceived(it) }
            error != null -> Napier.e("OAuth Intent Error", error, tag = "Auth-Intent")
        }
    }
}