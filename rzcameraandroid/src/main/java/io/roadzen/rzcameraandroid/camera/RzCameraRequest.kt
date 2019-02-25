package io.roadzen.rzcameraandroid.camera

import android.content.Context
import io.roadzen.rzcameraandroid.model.RzCameraInstanceInfo
import java.lang.ref.WeakReference

interface RzCameraRequest {

    /**
     * List of RzCameraInstanceInfo. The length of this list will be the number of Camera Instances started.
     */
    fun cameraInstanceInfoList(cameraInstanceInfoList: List<RzCameraInstanceInfo>): RzCameraFlow
}

internal class RzCameraRequestImpl(private val context: WeakReference<Context>) : RzCameraRequest {

    internal lateinit var rzCameraFlow: RzCameraFlowImpl

    override fun cameraInstanceInfoList(cameraInstanceInfoList: List<RzCameraInstanceInfo>): RzCameraFlow {
        rzCameraFlow = RzCameraFlowImpl(context, cameraInstanceInfoList)
        return rzCameraFlow
    }
}