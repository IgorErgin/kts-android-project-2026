//package com.github.igorergin.ktsandroid.core.network
//
//import com.github.igorergin.ktsandroid.feature.auth.data.network.OAuthTokenResponse
//import io.ktor.client.call.body
//import io.ktor.client.request.header
//import io.ktor.client.request.post
//import io.ktor.http.HttpHeaders
//
//class GithubAuthClient {
//    private val client = NetworkClient.oauthClient
//
//    suspend fun exchangeCodeForToken(code:String): OAuthTokenResponse {
//        return client.post(GithubAuthConfig.TOKEN_ENDPOINT) {
//            header(HttpHeaders.Accept, "application/json")
//            url {
//                parameters.append("client_id", GithubAuthConfig.CLIENT_ID)
//                parameters.append("client_secret", GithubAuthConfig.CLIENT_SECRET)
//                parameters.append("code", code)
//                parameters.append("redirect_uri", GithubAuthConfig.REDIRECT_URI)
//            }
//        }.body()
//    }
//}