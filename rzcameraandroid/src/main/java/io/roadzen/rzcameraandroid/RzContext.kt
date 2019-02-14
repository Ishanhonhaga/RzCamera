package io.roadzen.rzcameraandroid

import android.content.Context
import android.content.Intent
import io.roadzen.rzcameraandroid.capture.CaptureActivity
import io.roadzen.rzcameraandroid.capture.FlashMode
import java.lang.ref.WeakReference

object RzContext {

    var context: WeakReference<Context>? = null
    var filePrefix = "imageFile"
    var fileSuffix: String? = null
    var fileExtension = ".jpg"
    var useInternalStorage = false
    var startFullScreenPreview = false

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

    var prevCapturedImageUriList: List<String>? = null

    fun with(context: Context): RzContext {
        this.context = WeakReference(context)
        return this
    }

    fun startCameraFlow() {
        context?.get()?.startActivity(Intent(context?.get(), CaptureActivity::class.java))
    }
}