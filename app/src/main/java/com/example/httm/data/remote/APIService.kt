package com.example.httm.data.remote

import TestResponse
import com.example.httm.data.remote.response.TestFileResponse
import com.example.httm.data.remote.response.ThingSpeakResponse
import com.example.httm.model.Student
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @GET(value = "random")
    fun test():Call<TestResponse>

    @POST("finger")
    @Multipart
    fun getInfoUser(@Part file : MultipartBody.Part):Call<Student>

    @POST("fingers")
    fun sendBase64Image( @Body file:DataRequest):Call<Student>

    @POST("post")
    @Multipart
    fun uplaodFIle(@Part fileName  : MultipartBody.Part):Call<TestFileResponse>

    @Headers("Accept: application/json")
    @GET("update")
    fun setUpStateLight(@Query("api_key") apiKey:String,
                        @Query("field1") field1 : Int,
                        @Query("field2") field2:Int,
                        @Query("field3") field3:Int
                        ):Call<ThingSpeakResponse>

    @Headers("Accept: application/json")
    @GET("update")
    fun setUpStateLight1(@Query("api_key") apiKey:String,
                        @Query("field1") field1 : Int
    ):Call<ThingSpeakResponse>

    @Headers("Accept: application/json")
    @GET("update")
    fun setUpStateLight2(@Query("api_key") apiKey:String,
                         @Query("field2") field2 : Int
    ):Call<ThingSpeakResponse>

    @Headers("Accept: application/json")
    @GET("update")
    fun setUpStateLight3(@Query("api_key") apiKey:String,
                         @Query("field3") field3 : Int
    ):Call<ThingSpeakResponse>
}
