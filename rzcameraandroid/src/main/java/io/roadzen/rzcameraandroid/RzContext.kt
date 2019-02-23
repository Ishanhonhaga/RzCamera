package io.roadzen.rzcameraandroid

import android.content.Context
import android.content.Intent
import android.util.Log
import io.roadzen.rzcameraandroid.capture.CaptureActivity
import io.roadzen.rzcameraandroid.capture.FlashMode
import io.roadzen.rzcameraandroid.model.RzCameraInstanceDetails
import io.roadzen.rzcameraandroid.util.ImageCache
import io.roadzen.rzcameraandroid.util.Resolution
import java.lang.ref.WeakReference

internal data class RzContext(
    val context: WeakReference<Context>,
    val imageCache: ImageCache = ImageCache(),
    val endCameraFlow: MutableList<NoArgCallback> = mutableListOf(),
    val rzCameraInstanceDetails: RzCameraInstanceDetails?
) {

    var resolution: Resolution = Resolution.MAX

//    /**
//     * File extension for all the video files. Default is "mp4".
//     * Please note: The file name will be structured as -
//     * "${fileName}_${current date time in millis}_${imageNumber}.${videoFileExtension}"
//     */
//    var videoFileExtension: String = "mp4"

    var useInternalStorage = false
    var defaultFlashMode = FlashMode.AUTO

    fun startCameraFlow() {
        rzCameraInstanceDetails?.prevCapturedImageUris?.let { imageCache.capturedImageUriList.addAll(it) }
        Log.d("SPECIALTY", "Start activity being called for ${rzCameraInstanceDetails?.fieldName}")

        val intent = Intent(context.get(), CaptureActivity::class.java)
        context.get()?.startActivity(intent)
    }

    fun cleanUpAndEnd(isCancel: Boolean) {
        if (isCancel) {
            RzCamera.stop(isCancel = true, imageUriList = null)
        } else {
            endCameraFlow.forEach { it() }
            RzCamera.stop(isCancel = false, imageUriList = imageCache.capturedImageUriList)
        }
    }
}

typealias NoArgCallback = () -> Unit