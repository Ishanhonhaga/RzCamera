package io.roadzen.rzcameraandroid

import android.content.Context
import android.content.Intent
import io.roadzen.rzcameraandroid.capture.CaptureActivity
import io.roadzen.rzcameraandroid.capture.FlashMode
import java.lang.ref.WeakReference

object RzContext {

    private var context: WeakReference<Context>? = null

    /**
     * Prefix for all the image/video files. Default is "mediaFile".
     * Please note: The file name will be structured as -
     * "${fileName}_${current date time in millis}_${imageNumber}.${fileExtension}"
     */
    var fileName: String = "mediaFile"

    /**
     * File extension for all the image files. Default is "jpg".
     * Please note: The file name will be structured as -
     * "${fileName}_${current date time in millis}_${imageNumber}.${imageFileExtension}"
     */
    var imageFileExtension: String = "jpg"

    /**
     * File extension for all the video files. Default is "mp4".
     * Please note: The file name will be structured as -
     * "${fileName}_${current date time in millis}_${imageNumber}.${videoFileExtension}"
     */
    var videoFileExtension: String = "mp4"

    /**
     * Use internal storage as location for saving images/videos.
     */
    var useInternalStorage = false

    /**
     * Start the camera preview in expanded full screen mode.
     */
    var startFullScreenPreview = true

    /**
     * Default flash mode. Options are FlashMode.ON, FlashMode.OFF, FlashMode.AUTO
     */
    var defaultFlashMode = FlashMode.AUTO

    /**
     * File URI for image to be overlayed on the camera preview.
     */
    var overlayImageUri: String? = null
        set(value) {
            field = value
            if (value != null) overlayImageResId = null
        }

    /**
     * Image resource ID for image to be overlayed on the camera preview.
     */
    var overlayImageResId: Int? = null
        set(value) {
            field = value
            if (value != null) overlayImageUri = null
        }

    /**
     * Text to be overlayed at the bottom of the camera preview.
     */
    var overlayLabel: String? = null

    /**
     * Calling activity context
     */
    fun with(context: Context): RzContext {
        this.context = WeakReference(context)
        return this
    }

    /**
     * Start ConvenientCamera with assigned configuration
     */
    fun startCameraFlow() {
        context?.get()?.startActivity(Intent(context?.get(), CaptureActivity::class.java))
    }
}