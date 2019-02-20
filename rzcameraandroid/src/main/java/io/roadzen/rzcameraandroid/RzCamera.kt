package io.roadzen.rzcameraandroid

import android.content.Context
import android.util.Log
import io.roadzen.rzcameraandroid.capture.FlashMode
import io.roadzen.rzcameraandroid.util.LOG_TAG

class RzCamera {
    companion object {

        internal lateinit var rzContext: RzContext
            private set

        private var isStarted: Boolean = false

        fun with(context: Context): Companion {
            rzContext = RzContext(context)
            return this
        }

        /**
         * Callback fired when the flow ends or gets cancelled.
         */
        fun setImageCaptureCallback(callback: ImageCaptureCallback): Companion {
            rzContext.callback = callback
            return this
        }

        /**
         * Prefix for all the image/video files. Default is "mediaFile".
         * Please note: The file name will be structured as -
         * "${fileName}_${current date time in millis}_${imageNumber}.${fileExtension}"
         */
        fun setFileName(fileName: String): Companion {
            rzContext.fileName = fileName
            return this
        }

        /**
         * File extension for all the image files. Default is "jpg".
         * Please note: The file name will be structured as -
         * "${fileName}_${current date time in millis}_${imageNumber}.${imageFileExtension}"
         */
        fun setImageFileExtension(imageFileExtension: String): Companion {
            rzContext.imageFileExtension = imageFileExtension
            return this
        }

        /**
         * Use internal storage as location for saving images/videos.
         */
        fun shouldUseInternalStorage(flag: Boolean): Companion {
            rzContext.useInternalStorage = true
            return this
        }

        /**
         * Default flash mode. Options are FlashMode.ON, FlashMode.OFF, FlashMode.AUTO
         */
        fun setDefaultFlashMode(flashMode: FlashMode): Companion {
            rzContext.defaultFlashMode = flashMode
            return this
        }

        /**
         * File URI for image to be overlayed on the camera preview.
         */
        fun setOverlayImageUri(imageUri: String): Companion {
            rzContext.overlayImageUri = imageUri
            return this
        }

        /**
         * Image resource ID for image to be overlayed on the camera preview.
         */
        fun setOverlayImageResId(resId: Int): Companion {
            rzContext.overlayImageResId = resId
            return this
        }

        /**
         * Text to be overlayed at the bottom of the camera preview.
         */
        fun setOverlayLabel(label: String): Companion {
            rzContext.overlayLabel = label
            return this
        }

        /**
         * Any previously captured images
         */
        fun setPrevImageUriList(imageUriList: List<String>): Companion {
            rzContext.prevImageUriList = imageUriList
            return this
        }

        /**
         * Calling activity context
         */
        fun start() {
            if (!isStarted) {
                isStarted = true
                rzContext.startCameraFlow()
            } else {
                Log.e(LOG_TAG, "Cannot start an already started instance of RzCamera")
            }
        }

        internal fun stop() {
            isStarted = false
        }
    }
}

interface ImageCaptureCallback {
    fun onImagesCaptured(imageUriList: List<String>)
    fun onCancelled()
}