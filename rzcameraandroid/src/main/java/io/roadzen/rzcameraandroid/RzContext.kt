package io.roadzen.rzcameraandroid

import android.content.Context
import android.content.Intent
import io.roadzen.rzcameraandroid.capture.CaptureActivity
import io.roadzen.rzcameraandroid.capture.FlashMode
import io.roadzen.rzcameraandroid.util.ImageCache
import java.lang.ref.WeakReference

internal class RzContext(context: Context) {

    private var context: WeakReference<Context> = WeakReference(context)

    var callback: ImageCaptureCallback? = null
    var fileName: String = "mediaFile"
    var imageFileExtension: String = "jpg"

    /**
     * File extension for all the video files. Default is "mp4".
     * Please note: The file name will be structured as -
     * "${fileName}_${current date time in millis}_${imageNumber}.${videoFileExtension}"
     */
    var videoFileExtension: String = "mp4"

    var useInternalStorage = false
    var defaultFlashMode = FlashMode.AUTO
    var overlayImageUri: String? = null
        set(value) {
            field = value
            if (value != null) overlayImageResId = null
        }
    var overlayImageResId: Int? = null
        set(value) {
            field = value
            if (value != null) overlayImageUri = null
        }
    var overlayLabel: String? = null
    var prevImageUriList: List<String>? = null

    fun with(context: Context): RzContext {
        this.context = WeakReference(context)
        return this
    }

    /**
     * Start ConvenientCamera with assigned configuration
     */
    fun startCameraFlow() {
        prevImageUriList?.let { imageCache.capturedImageUriList.addAll(it) }
        context.get()?.startActivity(Intent(context.get(), CaptureActivity::class.java))
    }

    fun cleanUpAndEnd(isCancel: Boolean) {
        RzCamera.stop()
        if (isCancel) {
            callback?.onCancelled()
        } else {
            callback?.onImagesCaptured(imageCache.capturedImageUriList)
            endCameraFlow.forEach { it() }
        }
    }

    val imageCache: ImageCache = ImageCache()
    val endCameraFlow: MutableList<NoArgCallback> = mutableListOf()
}

typealias NoArgCallback = () -> Unit