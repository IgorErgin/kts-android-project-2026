package com.github.igorergin.ktsandroid.feature.detail.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DetailRepositoryTest {

    private fun createMockClient(handler: suspend () -> String): HttpClient {
        return HttpClient(MockEngine {
            respond(
                content = handler(),
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        }) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun `getRepositoryDetails should return mapped repository detail`() = runTest {
        // Given
        val jsonResponse = """
            {
                "id": 1,
                "name": "RepoName",
                "full_name": "Owner/RepoName",
                "description": "Desc",
                "stargazers_count": 100,
                "owner": {
                    "login": "Owner",
                    "avatar_url": "url"
                }
            }
        """.trimIndent()
        
        val client = createMockClient { jsonResponse }
        val repository = DetailRepository(client)

        // When
        val result = repository.getRepositoryDetails("Owner", "RepoName")

        // Then
        assertTrue(result.isSuccess)
        val detail = result.getOrThrow()
        assertEquals(1L, detail.id.value)
        assertEquals("RepoName", detail.name)
        assertEquals("Owner", detail.ownerLogin)
    }

    @Test
    fun `getReadme should decode base64 content`() = runTest {
        // Given
        val jsonResponse = """
            {
                "encoding": "base64",
                "content": "SGVsbG8gV29ybGQ=" 
            }
        """.trimIndent()
        // SGVsbG8gV29ybGQ= is "Hello World"
        
        val client = createMockClient { jsonResponse }
        val repository = DetailRepository(client)

        // When
        val result = repository.getReadme("Owner", "Repo")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Hello World", result.getOrThrow())
    }
}
