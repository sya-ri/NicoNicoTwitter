package me.syari.niconico.twitter

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.application
import blue.starry.penicillin.core.session.config.token
import blue.starry.penicillin.endpoints.oauth
import blue.starry.penicillin.endpoints.oauth.accessToken
import blue.starry.penicillin.endpoints.oauth.authenticateUrl
import blue.starry.penicillin.endpoints.oauth.requestToken
import blue.starry.penicillin.endpoints.search
import blue.starry.penicillin.endpoints.search.search
import blue.starry.penicillin.extensions.execute
import kotlinx.coroutines.runBlocking

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

    object SimpleSearch {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking {
                val client = PenicillinClient {
                    account {
                        application(CONSUMER_API_KEY, CONSUMER_API_SECRET_KEY)
                        token(ACCESS_TOKEN, ACCESS_TOKEN_SECRET)
                    }
                }
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
                val client = PenicillinClient {
                    account {
                        application(CONSUMER_API_KEY, CONSUMER_API_SECRET_KEY)
                        token(ACCESS_TOKEN, ACCESS_TOKEN_SECRET)
                    }
                }
                val response = client.search.search("#test -RT").execute()
                println(response)
                println(response.json.toString().length)
            }
        }
    }
}