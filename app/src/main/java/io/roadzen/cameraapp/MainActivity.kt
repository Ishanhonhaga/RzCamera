package io.roadzen.cameraapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.roadzen.rzcameraandroid.ImageCaptureCallback
import io.roadzen.rzcameraandroid.RzCamera
import io.roadzen.rzcameraandroid.util.LOG_TAG
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text.setOnClickListener { startCamFlow() }
    }

    private fun startCamFlow() {
        RzCamera.with(this).apply {
            setOverlayImageResId( R.drawable.front_image_4w)
            setOverlayLabel("Front of Car")
            setImageCaptureCallback(object : ImageCaptureCallback {
                override fun onImagesCaptured(imageUriList: List<String>) {
                    Log.d(LOG_TAG, "List of Images: $imageUriList")
                }

                override fun onCancelled() {
                    Log.d(LOG_TAG, "Camera Cancelled")
                    startCamFlow()
                }
            })
        }.start()
    }
}
