package me.syari.niconico.twitter.api

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.application
import blue.starry.penicillin.endpoints.oauth
import blue.starry.penicillin.endpoints.oauth.AccessTokenResponse
import blue.starry.penicillin.endpoints.oauth.accessToken
import blue.starry.penicillin.endpoints.oauth.authenticateUrl
import blue.starry.penicillin.endpoints.oauth.requestToken
import blue.starry.penicillin.endpoints.search
import blue.starry.penicillin.endpoints.search.search
import blue.starry.penicillin.extensions.execute
import blue.starry.penicillin.models.Status
import io.ktor.http.Url
import kotlinx.coroutines.delay

object TwitterAPI {
    private val client = PenicillinClient {
        account {
            application(CONSUMER_API_KEY, CONSUMER_API_SECRET_KEY)
        }
    }

    object AuthURLProvider {
        data class GenerateResult(
            val requestToken: String,
            val requestTokenSecret: String,
            val url: Url
        )

        suspend fun generate(): GenerateResult {
            val response = client.oauth.requestToken()
            val url = client.oauth.authenticateUrl(response.requestToken)
            return GenerateResult(response.requestToken, response.requestTokenSecret, url)
        }

        suspend fun enterPin(generateResult: GenerateResult, pin: String): AccessTokenResponse {
            return client.oauth.accessToken(generateResult.requestToken, generateResult.requestTokenSecret, pin)
        }
    }

    object ContinuousSearch {
        suspend fun search(word: String, handler: (List<Status>) -> Unit) {
            var sinceId = client.search.search(word, count = 1).execute().result.searchMetadata.maxId
            while (true) {
                val response = client.search.search(word, count = 100, sinceId = sinceId).execute()
                sinceId = response.result.searchMetadata.maxId
                handler.invoke(response.result.statuses)
                delay(5000)
            }
        }
    }
}