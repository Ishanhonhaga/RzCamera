package io.roadzen.rzcameraandroid.camera

import android.content.Context
import android.util.Log
import io.roadzen.rzcameraandroid.capture.FlashMode
import io.roadzen.rzcameraandroid.model.RzCameraInstanceInfo
import io.roadzen.rzcameraandroid.util.LOG_TAG
import io.roadzen.rzcameraandroid.util.Resolution
import io.roadzen.rzcameraandroid.util.initOnce
import java.lang.ref.WeakReference

interface RzCameraFlow {

    /**
     * Callback fired when the flow ends. It is called with the Map of
     * fieldName to imageUriList
     */
    var camSuccessCallback: CamSuccessListener?

    /**
     * Callback fired when the flow is cancelled
     */
    var camCancelCallback: CamCancelListener?

    /**
     * Callback fired when the flow ends in an error
     */
    var camErrorCallback: CamErrorListener?

    /**
     * Use internal storage as location for saving images/videos.
     */
    var useInternalStorage: Boolean

    /**
     * Default flash mode. Options are FlashMode.ON, FlashMode.OFF, FlashMode.AUTO
     */
    var defaultFlashMode: FlashMode

    /**
     * Resolution of the images to be captured.
     * Please note, the resolution will tried to be matched as close as the
     * device camera will allow.
     */
    var resolution: Resolution

    /**
     * Start the camera instance flow
     */
    fun start()
}

internal class RzCameraFlowImpl(
    private val context: WeakReference<Context>,
    private val cameraInstanceInfoList: List<RzCameraInstanceInfo>
) : RzCameraFlow {

    internal lateinit var rzContext: RzContext
        private set

    private var isStarted: Boolean = false
    private var fieldNameToImageUriList: HashMap<String, List<String>>? = null
    private var counter: Int = 0
    private var shouldSetNewValue = true

    init {
        shouldSetNewValue = true
        counter = 0
        fieldNameToImageUriList = HashMap()
    }

    override var camSuccessCallback: CamSuccessListener? = null
    override var camCancelCallback: CamCancelListener? = null
    override var camErrorCallback: CamErrorListener? = null
    override var useInternalStorage: Boolean by initOnce(shouldSetNewValue, false)
    override var defaultFlashMode: FlashMode by initOnce(shouldSetNewValue, FlashMode.AUTO)
    override var resolution: Resolution by initOnce(shouldSetNewValue, Resolution.MAX)

    override fun start() {
        if (!isStarted) {
            isStarted = true
            rzContext = RzContext(
                context = context,
                cameraInstanceInfo = cameraInstanceInfoList[counter],
                rzCameraFlow = this@RzCameraFlowImpl
            )
            rzContext.startCameraFlow()
        } else {
            Log.e(LOG_TAG, "Cannot start an already started instance of RzCamera")
        }
    }

    internal fun stop(flowEnd: FlowEnd, errorMsg: String?) {
        rzContext.endCameraFlow.forEach { it() }

        isStarted = false

        when (flowEnd) {
            FlowEnd.CANCEL -> camCancelCallback?.invoke()
            FlowEnd.ERROR -> camErrorCallback?.invoke(errorMsg!!)
            FlowEnd.COMPLETE -> {
                fieldNameToImageUriList!![cameraInstanceInfoList[counter].fieldName] = rzContext.imageCache.capturedImageUriList
                counter += 1
                if (counter == cameraInstanceInfoList.size) {
                    camSuccessCallback?.invoke(fieldNameToImageUriList!!)

                } else {
                    start()
                }
            }
        }
    }
}

typealias CamSuccessListener = (Map<String, List<String>>) -> Unit
typealias CamCancelListener = () -> Unit
typealias CamErrorListener = (String) -> Unit

enum class FlowEnd {
    CANCEL, ERROR, COMPLETE
}