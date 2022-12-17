package com.example.httm.view

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.httm.data.remote.APIClient
import com.example.httm.data.remote.APIService
import com.example.httm.data.remote.response.ThingSpeakResponse
import com.example.httm.databinding.FragmentMainBinding
import com.example.httm.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainFragment:Fragment() {
    lateinit var databinding : FragmentMainBinding
    lateinit var mSpeechRecognizer : SpeechRecognizer
    lateinit var t1 : TextToSpeech
    lateinit var mSpeechRecognizerIntent : Intent
    var stateMic = false
    val textLiveData = MutableLiveData<String>()
    lateinit var apiService:APIService
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentMainBinding.inflate(layoutInflater,container,false)
        return databinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = APIClient.getClient().create(APIService::class.java)
        setUpListener()
        setUpEvent()
    }

    private fun setUpListener() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        mSpeechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(p0: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(p0: Bundle?) {
                val matches = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if(matches != null){
                    Log.e("text", matches[0])
                    textLiveData.value = matches[0]
                }
            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }

        })
    }

    private fun setUpEvent() {
        databinding.mic.setOnClickListener{
            stateMic = if (stateMic){
                mSpeechRecognizer.stopListening()
                Toast.makeText(requireContext(),"MICRO has turn off",Toast.LENGTH_SHORT).show()
                !stateMic
            }else{
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                Toast.makeText(requireContext(),"MICRO has turn on",Toast.LENGTH_SHORT).show()
                !stateMic
            }
        }

        textLiveData.observe(this, androidx.lifecycle.Observer {
            when (it) {
                "turn on led one" -> {
                    Log.e("TURN_ON","TURN_ON")
                    turnOnLed1(1)
                }
                "turn on led two" -> {
                    Log.e("TURN_ON","TURN_ON")
                    turnOnLed2(1,)
                }
                "turn on led three" -> {
                    Log.e("TURN_ON","TURN_ON")
                    turnOnLed3(1)
                }
                "turn off led one" -> {
                    Log.e("TURN_OFF","TURN_OFF")
                    turnOnLed1(0)
                }
                "turn off led two" -> {
                    Log.e("TURN_OFF","TURN_OFF")
                    turnOnLed2(0)
                }
                "turn off led three" -> {
                    Log.e("TURN_OFF","TURN_OFF")
                    turnOnLed3(0)
                }
            }
        })
    }
    private fun turnOnLed(field1:Int, field2:Int, field3:Int){
        val call = apiService.setUpStateLight("NUV0HFS4T1LJHUHO",field1,field2,field3)
        call.enqueue(object : Callback<ThingSpeakResponse>{
            override fun onResponse(
                call: Call<ThingSpeakResponse>,
                response: Response<ThingSpeakResponse>
            ) {
                val thingSpeakResponse = response.body()
                Log.e("field",thingSpeakResponse?.field1.toString())
            }

            override fun onFailure(call: Call<ThingSpeakResponse>, t: Throwable) {
                Log.e("Error",t.toString())
                call.cancel()
            }

        })
    }
    private fun turnOnLed1(field1:Int){
        val call = apiService.setUpStateLight1("NUV0HFS4T1LJHUHO",field1)
        call.enqueue(object : Callback<ThingSpeakResponse>{
            override fun onResponse(
                call: Call<ThingSpeakResponse>,
                response: Response<ThingSpeakResponse>
            ) {
                val thingSpeakResponse = response.body()
                Log.e("Success","Success")
            }

            override fun onFailure(call: Call<ThingSpeakResponse>, t: Throwable) {
                Log.e("Error",t.toString())
                call.cancel()
            }

        })
    }
    private fun turnOnLed2(field2:Int){
        val call = apiService.setUpStateLight2("NUV0HFS4T1LJHUHO",field2)
        call.enqueue(object : Callback<ThingSpeakResponse>{
            override fun onResponse(
                call: Call<ThingSpeakResponse>,
                response: Response<ThingSpeakResponse>
            ) {
                val thingSpeakResponse = response.body()
                Log.e("Success","Success")
            }

            override fun onFailure(call: Call<ThingSpeakResponse>, t: Throwable) {
                Log.e("Error",t.toString())
                call.cancel()
            }

        })
    }
    private fun turnOnLed3(field3:Int){
        val call = apiService.setUpStateLight3("NUV0HFS4T1LJHUHO",field3)
        call.enqueue(object : Callback<ThingSpeakResponse>{
            override fun onResponse(
                call: Call<ThingSpeakResponse>,
                response: Response<ThingSpeakResponse>
            ) {
                val thingSpeakResponse = response.body()
                Log.e("Success","Success")
            }

            override fun onFailure(call: Call<ThingSpeakResponse>, t: Throwable) {
                Log.e("Error",t.toString())
                call.cancel()
            }

        })
    }

}