package io.roadzen.cameraapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.roadzen.rzcameraandroid.RzCamera
import io.roadzen.rzcameraandroid.model.RzCameraInstanceDetails
import io.roadzen.rzcameraandroid.util.LOG_TAG
import io.roadzen.rzcameraandroid.util.Resolution
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        oneFieldButton.setOnClickListener { startOneFieldCamFlow() }
        fourFieldButton.setOnClickListener { startFourFieldCamFlow() }
    }

    private fun startFourFieldCamFlow() {
        RzCamera.with(this).apply {
            resolution = Resolution.R1920x1080

            rzCameraInstanceDetails = listOf(
                RzCameraInstanceDetails(
                    overlayImageResId = R.drawable.front_image_4w,
                    overlayLabel = "Front of Car",
                    fieldName = "front_car",
                    fileName = "frontCar",
                    fileExtension = "jpg"
                ),
                RzCameraInstanceDetails(
                    overlayImageResId = R.drawable.front_image_4w,
                    overlayLabel = "Back of Car",
                    fieldName = "back_car",
                    fileName = "backCar",
                    fileExtension = "jpg"
                ),
                RzCameraInstanceDetails(
                    overlayImageResId = R.drawable.front_image_4w,
                    overlayLabel = "Left Side of Car",
                    fieldName = "left_car",
                    fileName = "leftCar",
                    fileExtension = "jpg"
                ),
                RzCameraInstanceDetails(
                    overlayImageResId = R.drawable.front_image_4w,
                    overlayLabel = "Right Side of Car",
                    fieldName = "right_car",
                    fileName = "rightCar",
                    fileExtension = "jpg"
                )
            )

            successCallback = { map -> // Returns a HashMap that maps "fieldName" to it's associated List of ImageUris
                Log.d(LOG_TAG, "Map of Images: $map")
            }

            cancelCallback = {
                Log.d(LOG_TAG, "Camera Cancelled")
            }

            errorCallback = { errorMsg ->
                Log.d(LOG_TAG, errorMsg)
            }
        }.start()
    }

    private fun startOneFieldCamFlow() {
        RzCamera.with(this).apply {
            resolution = Resolution.R1920x1080
            rzCameraInstanceDetails = listOf(
                RzCameraInstanceDetails(
                    fieldName = "front_car", // REQUIRED & Unique
                    overlayImageResId = R.drawable.front_image_4w, // Optional, default is null
                    overlayLabel = "Front of Car", // Optional, default is null
                    fileName = "frontCar", // Optional, default is "$fieldName_image"
                    fileExtension = "jpg" // Optional, default is jpg
                )
            )

            successCallback = { map -> // Returns a HashMap that maps "fieldName" to it's associated List of ImageUris
                Log.d(LOG_TAG, "Map of Images: $map")
            }

            cancelCallback = {
                Log.d(LOG_TAG, "Camera Cancelled")
            }

            errorCallback = { errorMsg ->
                Log.d(LOG_TAG, errorMsg)
            }
        }.start()
    }
}