package io.roadzen.rzcameraandroid.camera

import android.content.Context
import android.content.Intent
import io.roadzen.rzcameraandroid.capture.CaptureActivity
import io.roadzen.rzcameraandroid.model.RzCameraInstanceInfo
import io.roadzen.rzcameraandroid.util.ImageCache
import java.lang.ref.WeakReference

internal data class RzContext(
    val context: WeakReference<Context>,
    val imageCache: ImageCache = ImageCache(),
    val endCameraFlow: MutableList<NoArgCallback> = mutableListOf(),
    val cameraInstanceInfo: RzCameraInstanceInfo?,
    val rzCameraFlow: RzCameraFlowImpl
) {

//    /**
//     * File extension for all the video files. Default is "mp4".
//     * Please note: The file name will be structured as -
//     * "${fileName}_${current date time in millis}_${imageNumber}.${videoFileExtension}"
//     */
//    var videoFileExtension: String = "mp4"

    fun startCameraFlow() {
        cameraInstanceInfo?.prevCapturedImageUris?.let { imageCache.capturedImageUriList.addAll(it) }

        val intent = Intent(context.get(), CaptureActivity::class.java)
        context.get()?.startActivity(intent)
    }
}

typealias NoArgCallback = () -> Unit