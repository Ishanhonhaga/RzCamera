package io.roadzen.rzcameraandroid

import android.content.Context
import android.util.Log
import io.roadzen.rzcameraandroid.capture.FlashMode
import io.roadzen.rzcameraandroid.model.RzCameraInstanceDetails
import io.roadzen.rzcameraandroid.util.LOG_TAG
import io.roadzen.rzcameraandroid.util.Resolution
import io.roadzen.rzcameraandroid.util.initOnce
import java.lang.ref.WeakReference

class RzCamera {
    companion object {

        internal lateinit var rzContext: RzContext
            private set

        private lateinit var context: WeakReference<Context>
        private var isStarted: Boolean = false
        private var fieldNameToImageUriList: HashMap<String, List<String>>? = null
        private var counter: Int = 0
        private var shouldSetNewValue = true

        /**
         * Calling activity context
         */
        fun with(context: Context): Companion {
            shouldSetNewValue = true
            counter = 0
            fieldNameToImageUriList = HashMap()
            this.context = WeakReference(context)

            return this
        }

        /**
         * List of RzCameraInstanceDetails. The length of this list will be the number of Camera Instances started.
         */
        var rzCameraInstanceDetails: List<RzCameraInstanceDetails> by initOnce(shouldSetNewValue, listOf())

        /**
         * Callback fired when the flow ends. It is called with the Map of
         * fieldName to imageUriList
         */
        var successCallback: SuccessListener? = null

        /**
         * Callback fired when the flow is cancelled
         */
        var cancelCallback: CancelListener? = null

        /**
         * Callback fired when the flow ends in an error
         */
        var errorCallback: ErrorListener? = null

        /**
         * Use internal storage as location for saving images/videos.
         */
        var useInternalStorage: Boolean by initOnce(shouldSetNewValue, false)

        /**
         * Default flash mode. Options are FlashMode.ON, FlashMode.OFF, FlashMode.AUTO
         */
        var defaultFlashMode: FlashMode by initOnce(shouldSetNewValue, FlashMode.AUTO)

        /**
         * Resolution of the images to be captured.
         * Please note, the resolution will tried to be matched as close as the
         * device camera will allow.
         */
        var resolution: Resolution by initOnce(shouldSetNewValue, Resolution.MAX)


        /**
         * Start the camera instance flow
         */
        fun start() {
            try {
                val ctr = counter
                if (!isStarted) {
                    isStarted = true
                    rzContext = RzContext(
                        context = context,
                        rzCameraInstanceDetails = rzCameraInstanceDetails[counter]
                    ).apply {
                        useInternalStorage = this@Companion.useInternalStorage
                        defaultFlashMode = this@Companion.defaultFlashMode
                        resolution = this@Companion.resolution
                    }
                    rzContext.startCameraFlow()
                } else {
                    Log.e(LOG_TAG, "Cannot start an already started instance of RzCamera")
                }
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
                errorCallback?.let {
                    it("List of RzCameraInstanceDetails is not set")
                }
                    ?: throw RuntimeException("Error callback cannot be null")
            }
        }

        internal fun stop(isCancel: Boolean, imageUriList: List<String>?) {
            isStarted = false
            if (isCancel) {
                cancelCallback?.invoke()
                return
            }

            fieldNameToImageUriList!![rzCameraInstanceDetails[counter].fieldName] = imageUriList!!
            counter += 1
            if (counter == rzCameraInstanceDetails.size) {
                successCallback?.invoke(fieldNameToImageUriList!!)

            } else {
                start()
            }

        }
    }
}

typealias SuccessListener = (Map<String, List<String>>) -> Unit
typealias CancelListener = () -> Unit
typealias ErrorListener = (String) -> Unit