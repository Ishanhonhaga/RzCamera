package io.roadzen.rzcameraandroid.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.otaliastudios.cameraview.PictureResult
import io.roadzen.rzcameraandroid.RzCamera.Companion.rzContext
import io.roadzen.rzcameraandroid.base.BaseViewModel
import io.roadzen.rzcameraandroid.util.ERROR_CAMERA
import io.roadzen.rzcameraandroid.util.ERROR_SAVE_FILE
import io.roadzen.rzcameraandroid.util.FileDirectoryProvider
import io.roadzen.rzcameraandroid.util.isExternalStorageWritable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

internal class CaptureViewModel(
    private val fileDirectoryProvider: FileDirectoryProvider
) : BaseViewModel() {

    private var imageCounter: AtomicInteger = AtomicInteger(0)

    private val captureViewStateLD = MutableLiveData<CaptureViewState>()
    private val captureViewEffectLD = MutableLiveData<CaptureViewEffect>()
    val captureViewState: LiveData<CaptureViewState>
        get() = captureViewStateLD
    val captureViewEffect: LiveData<CaptureViewEffect>
        get() = captureViewEffectLD

    private var currentViewState = initCaptureViewState()
        set(value) {
            field = value
            captureViewStateLD.value = value
        }

    private val closeScreenCallback = {
        closeScreen()
    }

    init {
        rzContext.endCameraFlow.add(closeScreenCallback)
    }

    override fun onCleared() {
        rzContext.endCameraFlow.remove(closeScreenCallback)
        super.onCleared()
    }

    fun onEvent(captureEvent: CaptureEvent) {
        when (captureEvent) {
            is CaptureEvent.ScreenLoadEvent -> screenLoad()
            is CaptureEvent.ImageCapturedEvent -> imageCaptured(captureEvent.result)
            is CaptureEvent.ToggleFlashEvent -> toggleFlash()
            is CaptureEvent.EnlargeMinimiseOverlayEvent -> enlargeMinimiseOverlay()
            is CaptureEvent.NavigateToPreviewEvent -> navigateToPreview()
            is CaptureEvent.SystemUiVisibleEvent -> hideSystemUI()
            is CaptureEvent.CameraErrorEvent -> cameraError()
            is CaptureEvent.ExitEvent -> cancelAndClose()
        }
    }

    private fun screenLoad() {
        hideSystemUI()
        currentViewState = currentViewState.copy(capturedImages = rzContext.imageCache.capturedImageUriList.toList())
    }

    private fun cameraError() {
        currentViewState = currentViewState.copy(error = ERROR_CAMERA)
    }

    private fun hideSystemUI() {
        launch {
            delay(1000L)
            captureViewEffectLD.value = CaptureViewEffect.MakeImmersiveEffect
        }
    }

    private fun navigateToPreview() {
        captureViewEffectLD.value = CaptureViewEffect.NavigateToImagePreviewEffect
    }

    private fun enlargeMinimiseOverlay() {
        currentViewState = currentViewState.copy(overlayEnlarged = !currentViewState.overlayEnlarged)
    }

    private fun toggleFlash() {
        currentViewState = currentViewState.copy(
            flashMode = when (currentViewState.flashMode) {
                FlashMode.ON -> FlashMode.OFF
                FlashMode.OFF -> FlashMode.AUTO
                FlashMode.AUTO -> FlashMode.ON
            }
        )
    }

    private fun imageCaptured(pictureResult: PictureResult) {
        val fileName = "${rzContext.fileName}_${Date().time}_${imageCounter.getAndIncrement()}.${rzContext.imageFileExtension}"

        val file = if (!rzContext.useInternalStorage && isExternalStorageWritable()) {
            val externalDirectory = fileDirectoryProvider.getPublicDirectory()
            File(externalDirectory, fileName)

        } else {
            File(fileDirectoryProvider.getPrivateDirectory(), fileName)
        }

        pictureResult.toFile(file) { savedFile ->
            if (savedFile != null) {
                val uriPath = savedFile.toURI()?.path!!

                rzContext.imageCache.capturedImageUriList.add(0, uriPath)

                if (rzContext.imageCache.capturedImageUriList.size > 1) {
                    currentViewState = currentViewState.copy(capturedImages = rzContext.imageCache.capturedImageUriList.toList())
                } else {
                    captureViewEffectLD.value = CaptureViewEffect.NavigateToImagePreviewEffect
                }

            } else {
                currentViewState = currentViewState.copy(error = ERROR_SAVE_FILE)
            }
        }
    }

    private fun closeScreen() {
        captureViewEffectLD.value = CaptureViewEffect.CloseScreenEffect
    }

    private fun cancelAndClose() {
        rzContext.cleanUpAndEnd(isCancel = true)
        captureViewEffectLD.value = CaptureViewEffect.CloseScreenEffect
    }
}