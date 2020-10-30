package me.syari.niconico.twitter.api

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.application
import blue.starry.penicillin.endpoints.oauth
import blue.starry.penicillin.endpoints.oauth.AccessTokenResponse
import blue.starry.penicillin.endpoints.oauth.accessToken
import blue.starry.penicillin.endpoints.oauth.authenticateUrl
import blue.starry.penicillin.endpoints.oauth.requestToken
import io.ktor.http.*
import me.syari.niconico.twitter.CONSUMER_API_KEY
import me.syari.niconico.twitter.CONSUMER_API_SECRET_KEY

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
}