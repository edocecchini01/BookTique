package com.example.booktique
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object ApiServiceManager {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: ApiInterface = retrofit.create(ApiInterface::class.java)
}
interface ApiInterface {

    @GET("books/v1/volumes")
    fun getNewReleases(@Query("q") query: String, @Query("orderBy") filter: String): Call<BookResponse>

    @GET("books/v1/volumes")
    fun searchBooks(@Query("q") query: String): Call<BookResponse>

    @GET("books/v1/volumes")
    fun getMostRelevant(@Query("orderBy") filter: String): Call<BookResponse>

    //generi
    @GET("books/v1/volumes")
    fun getSubjectBooks(@Query("q") query: String, @Query("orderBy") orderBy: String): Call<BookResponse>

}

