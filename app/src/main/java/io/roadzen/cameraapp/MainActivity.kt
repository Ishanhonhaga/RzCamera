package io.roadzen.cameraapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.roadzen.rzcameraandroid.RzContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RzContext.with(this).apply {
            overlayImageResId = R.drawable.front_image_4w
            overlayLabel = "Front of Car"
        }.startCameraFlow()
    }
}
