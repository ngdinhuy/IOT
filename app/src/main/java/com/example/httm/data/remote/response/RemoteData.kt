package com.example.httm.data.remote.response

import com.example.httm.data.remote.APIService
import com.example.httm.data.remote.RemoteDataInterface
import com.example.httm.model.Student
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import java.io.File

class RemoteData(val apiService: APIService):RemoteDataInterface {
    override fun getInfoUser(file: File): Call<Student> {
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart =  MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
        return apiService.getInfoUser(filePart)
    }
    fun sendFile(file:File):Call<TestFileResponse>{
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart =  MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
        return apiService.uplaodFIle(filePart)
    }
}