package com.example.httm.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.httm.data.remote.APIClient
import com.example.httm.data.remote.APIService
import com.example.httm.data.remote.DataRequest
import com.example.httm.data.remote.response.RemoteData
import com.example.httm.data.remote.response.TestFileResponse
import com.example.httm.data.remote.response.TestResponse
import com.example.httm.databinding.FragmentPreviewBinding
import com.example.httm.model.Student
import com.example.httm.util.Utils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class PreviewFragment:Fragment() {
    lateinit var databinding:FragmentPreviewBinding
    private val args by navArgs<PreviewFragmentArgs>()
    private lateinit var img:Bitmap
    lateinit var apiService: APIService
    lateinit var remoteData: RemoteData
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentPreviewBinding.inflate(inflater,container,false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = APIClient.getClient().create(APIService::class.java)
        remoteData = RemoteData(apiService)
        setUpView()
        setUpEnvent()
    }

    private fun setUpView() {
        val base64 = args.base64
        val imageAsBytes: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        img = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
        databinding.image.setImageBitmap(img)
    }

    private fun setUpEnvent() {
        databinding.btnSave.setOnClickListener{
            saveImage(Uri.parse(getImageUri(img)))
        }
        databinding.btnSend.setOnClickListener{
            Log.e("send","send")
            sendFile()

        }
        databinding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    fun sendFile(){
        val file = File(Utils.getRealPathFromURI(requireContext(), Uri.parse(getImageUri(img))))
        Log.e("file path",file.length().toString())
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val linkImage =  MultipartBody.Part.createFormData("image", file.name, requestBody)
        var s : String = args.base64
        
        val dataRequest = DataRequest(s)
        val call = apiService.sendBase64Image(dataRequest)
        Log.e("base64",args.base64)
        call.enqueue(object : Callback<Student> {
            override fun onResponse(call: Call<Student>?, response: Response<Student>) {
                val student = response.body()
                Log.e("succes",student!!.userName)
                val action = PreviewFragmentDirections.actionPreviewFragmentToResultFragment(username = student.userName, msv = student.code)
                findNavController().navigate(action)
            }

            override fun onFailure(call: Call<Student>, t: Throwable?) {
                Log.e("Error",t.toString())
                call.cancel()
            }
        })
    }

    fun testAPI(){
        val call = apiService.test()
        call.enqueue(object : Callback<TestResponse> {
            override fun onResponse(call: Call<TestResponse>?, response: Response<TestResponse>) {
                val testResponse = response.body()
                Log.e("succes",testResponse!!.message)
            }

            override fun onFailure(call: Call<TestResponse>, t: Throwable?) {
                call.cancel()
            }
        })

    }

    fun getImageUri(image: Bitmap) : String{
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG,100,bytes)
        val uri = MediaStore.Images.Media.insertImage(requireContext().contentResolver,image,"IMG_"+ Calendar.getInstance().time,null)
        return uri
    }

    fun saveImage(uri: Uri): Bitmap {
        val bitmapInputImage = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)
        lateinit var outputBitmapImage: Bitmap
        var width = bitmapInputImage.width
        var height = bitmapInputImage.height
        outputBitmapImage = Bitmap.createScaledBitmap(bitmapInputImage,width,height,true)
        return outputBitmapImage
    }
}