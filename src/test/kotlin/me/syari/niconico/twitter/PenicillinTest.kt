package me.syari.niconico.twitter

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.ApiClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.application
import blue.starry.penicillin.endpoints.oauth
import blue.starry.penicillin.endpoints.oauth.accessToken
import blue.starry.penicillin.endpoints.oauth.authenticateUrl
import blue.starry.penicillin.endpoints.oauth.requestToken
import blue.starry.penicillin.endpoints.search
import blue.starry.penicillin.endpoints.search.search
import blue.starry.penicillin.extensions.execute
import blue.starry.penicillin.extensions.models.text
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.syari.niconico.twitter.api.CONSUMER_API_KEY
import me.syari.niconico.twitter.api.CONSUMER_API_SECRET_KEY

object PenicillinTest {
    object APIConnect {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking {
                val client = PenicillinClient {
                    account {
                        application(CONSUMER_API_KEY, CONSUMER_API_SECRET_KEY)
                    }
                }
                val response = client.oauth.requestToken()
                println(client.oauth.authenticateUrl(response.requestToken))
                val pin = readLine()!!
                println(client.oauth.accessToken(CONSUMER_API_KEY, CONSUMER_API_SECRET_KEY, response.requestToken, response.requestTokenSecret, pin))
            }
        }
    }

    suspend fun getConnectedClient(): ApiClient {
        return PenicillinClient {
            account {
                application(CONSUMER_API_KEY, CONSUMER_API_SECRET_KEY)
            }
        }.apply {
            val response = oauth.requestToken()
            println(oauth.authenticateUrl(response.requestToken))
            val pin = readLine()!!
            println(oauth.accessToken(CONSUMER_API_KEY, CONSUMER_API_SECRET_KEY, response.requestToken, response.requestTokenSecret, pin))
        }
    }

    object SimpleSearch {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking {
                val client = getConnectedClient()
                val response = client.search.search("#test").execute()
                println(response)
                println(response.json.toString().length)
            }
        }
    }

    object SimpleSearchIgnoreRT {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking {
                val client = getConnectedClient()
                val response = client.search.search("#test -RT").execute()
                println(response)
                println(response.json.toString().length)
            }
        }
    }

    object ContinuousSearchIgnoreRT {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking {
                val client = getConnectedClient()
                var sinceId = client.mostRecentId("#test -RT")
                while (true) {
                    val response = client.search.search("#test -RT", count = 100, sinceId = sinceId).execute()
                    sinceId = response.result.searchMetadata.maxId
                    response.result.statuses.forEach {
                        println(it.user.screenName + ": " + it.text)
                    }
                    delay(5000)
                }
            }
        }

        private suspend fun ApiClient.mostRecentId(query: String): Long? {
            val response = search.search(query, count = 1).execute()
            return response.result.searchMetadata.maxId
        }
    }
}