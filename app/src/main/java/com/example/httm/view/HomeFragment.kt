package com.example.httm.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.httm.databinding.FragmentHomeBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class HomeFragment:Fragment() {
    lateinit var databinding:FragmentHomeBinding
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView : PreviewView
    private lateinit var preview: Preview
    lateinit var cameraSelector: CameraSelector
    val executor = Executors.newSingleThreadExecutor()
    lateinit var imageCapture: ImageCapture
    lateinit var btnCapture: Button
    lateinit var imgBitmap : Bitmap
    var img = MutableLiveData<Bitmap>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentHomeBinding.inflate(inflater,container,false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = databinding.viewFinder
        btnCapture = databinding.button
        setUpCamera()
        setUpEvent()
    }
    private fun setUpEvent() {
        btnCapture.setOnClickListener{
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        Log.e("format",image.format.toString())
                        Log.e("format2", ImageFormat.YUV_420_888.toString())
                        imgBitmap = getBitmap(image)
                        val imgResize = Bitmap.createBitmap(imgBitmap,(image.width/3).toInt(),(image.height*0.35).toInt(),(image.width/3).toInt(),(image.width/3).toInt(),null,true)
//                        saveImage(Uri.parse(getImageUri(imgResize)))
                        img.value = imgResize
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        Log.e("Error Capture",exception.toString())
                    }
                })
        }
        img = MutableLiveData()
        img.observe(this, Observer {
            val action = HomeFragmentDirections.actionHomeFragmentToPreviewFragment(convertBitmapToBase64(it))
            findNavController().navigate(action)
        })
    }

    private fun getBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        val clonedBytes = bytes.clone()
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.size)
    }
    private fun setUpCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        preview  = Preview.Builder()
            .build()
//        cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        preview.setSurfaceProvider(previewView.surfaceProvider)
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
        imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
        cameraProvider!!.unbindAll()
        val camera = cameraProvider!!.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture, preview)
        setTouchFocus(camera)
    }

    private fun setTouchFocus(camera: Camera) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio: Float = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1F
                val delta = detector.scaleFactor
                camera.cameraControl.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(previewView.context, listener)

        previewView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {
                val factory = previewView.meteringPointFactory
                val point = factory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(5, TimeUnit.SECONDS)
                    .build()
                camera.cameraControl.startFocusAndMetering(action)
            }
            true
        }
    }

    fun getImageUri(image: Bitmap) : String{
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG,100,bytes)
        val uri = MediaStore.Images.Media.insertImage(requireContext().contentResolver,image,"IMG_"+ Calendar.getInstance().time,null)
        return uri
    }

    fun saveImage(uri: Uri):Bitmap{
        val bitmapInputImage = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)
        lateinit var outputBitmapImage: Bitmap
        var width = bitmapInputImage.width
        var height = bitmapInputImage.height
        outputBitmapImage = Bitmap.createScaledBitmap(bitmapInputImage,width,height,true)
        return outputBitmapImage
    }
    fun convertBitmapToBase64(bm:Bitmap):String{
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b,Base64.DEFAULT)
    }
}