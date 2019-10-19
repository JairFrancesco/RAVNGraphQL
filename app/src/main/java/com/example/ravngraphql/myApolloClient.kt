package com.example.ravngraphql

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient;


class myApolloClient {
    companion object {
        const val BASE_URL = "https://api.github.com/graphql"
        val myApolloClient: ApolloClient = getApolloClient()

        private fun getApolloClient() : ApolloClient{
            val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "bearer ${BuildConfig.github_apiKey}")
                        .build()

                    chain.proceed(request)
                }
                .build()

            return ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build()
        }




    }

}