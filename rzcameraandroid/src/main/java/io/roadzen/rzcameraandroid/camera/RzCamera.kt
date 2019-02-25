package io.roadzen.rzcameraandroid.camera

import android.content.Context
import io.roadzen.rzcameraandroid.capture.FlashMode
import io.roadzen.rzcameraandroid.model.RzCameraInstanceInfo
import io.roadzen.rzcameraandroid.util.ImageCache
import io.roadzen.rzcameraandroid.util.Resolution
import java.lang.ref.WeakReference

class RzCamera {
    companion object {

        private lateinit var rzCameraRequest: RzCameraRequestImpl

        /**
         * Calling activity context
         */
        fun with(context: Context): RzCameraRequest {
            rzCameraRequest = RzCameraRequestImpl(WeakReference(context))
            return rzCameraRequest
        }

        internal val imageCache: ImageCache
            get() = rzCameraRequest.rzCameraFlow.rzContext.imageCache
        internal val endCameraFlow: MutableList<NoArgCallback>
            get() = rzCameraRequest.rzCameraFlow.rzContext.endCameraFlow
        internal val cameraInstanceInfo: RzCameraInstanceInfo?
            get() = rzCameraRequest.rzCameraFlow.rzContext.cameraInstanceInfo
        internal val useInternalStorage: Boolean
            get() = rzCameraRequest.rzCameraFlow.useInternalStorage
        internal val defaultFlashMode: FlashMode
            get() = rzCameraRequest.rzCameraFlow.defaultFlashMode
        internal val resolution: Resolution
            get() = rzCameraRequest.rzCameraFlow.resolution

        internal fun stop(flowEnd: FlowEnd, errorMsg: String? = null) {
            rzCameraRequest.rzCameraFlow.stop(flowEnd, errorMsg)
        }
    }
}