package com.bookmarks.networking

import android.icu.util.TimeUnit
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.moshi.MoshiConverterFactory

interface OpenLibraryApiService {

  @GET("v1/volumes?maxResults=3")
  fun bookDetails(@Query("q") key: String): Observable<OpenLibraryResponse.Result>

  companion object {
    fun create(): OpenLibraryApiService {

      val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)

      val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

      val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(
          RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://www.googleapis.com/books/")
        .client(okHttpClient)
        .build()

      return retrofit.create(OpenLibraryApiService::class.java)
    }
  }
}