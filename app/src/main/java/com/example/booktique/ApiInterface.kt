package com.example.booktique
import android.util.Log
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor

object ApiServiceManager {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // altre configurazioni del client OkHttpClient
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/")
        .client(client)
        .build()

    val apiService: ApiInterface = retrofit.create(ApiInterface::class.java)

}


interface ApiInterface {

    @GET("books/v1/volumes")
    fun getNewReleases(@Query("q") query: String, @Query("orderBy") filter: String,@Query("printType") printType: String = "books",@Query("filter") formatFilter: String = "ebooks"): Call<ResponseBody>

    @GET("books/v1/volumes")
    fun searchBooks(@Query("q") query: String, @Query("orderBy") filter: String,@Query("printType") printType: String = "books",@Query("filter") formatFilter: String = "ebooks"): Call<ResponseBody>

    @GET("books/v1/volumes")
    fun getMostRelevant(@Query("orderBy") filter: String): Call<ResponseBody>

    //generi
    @GET("books/v1/volumes")
    fun getSubjectBooks(@Query("q") query: String, @Query("orderBy") orderBy: String): Call<BookResponse>

    @GET("books/v1/volumes")
    fun getPerTe(@Query("q") query: String, @Query("orderBy") orderBy: String, @Query("maxResults") maxResults: Int,@Query("printType") printType: String = "books",@Query("filter") formatFilter: String = "ebooks"): Call<ResponseBody>

}

