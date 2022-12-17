package com.example.httm.data.remote

import com.example.httm.model.Student
import retrofit2.Call
import java.io.File

interface RemoteDataInterface {
    fun getInfoUser(file:File):Call<Student>
}