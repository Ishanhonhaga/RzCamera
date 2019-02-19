package io.roadzen.rzcameraandroid.capture

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.otaliastudios.cameraview.PictureResult
import io.roadzen.rzcameraandroid.RzContext.fileName
import io.roadzen.rzcameraandroid.RzContext.imageFileExtension
import io.roadzen.rzcameraandroid.RzContext.startFullScreenPreview
import io.roadzen.rzcameraandroid.RzContext.useInternalStorage
import io.roadzen.rzcameraandroid.base.BaseViewModel
import io.roadzen.rzcameraandroid.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

internal class CaptureViewModel(
    private val fileDirectoryProvider: FileDirectoryProvider
) : BaseViewModel() {

    private var imageCounter: AtomicInteger = AtomicInteger(0)
    private var isPreviewExpanded = startFullScreenPreview

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

    fun onEvent(captureEvent: CaptureEvent) {
        when (captureEvent) {
            is CaptureEvent.ScreenLoadEvent -> screenLoad()
            is CaptureEvent.ImageCapturedEvent -> imageCaptured(captureEvent.result)
            is CaptureEvent.ChangeAspectRatioEvent -> changeAspectRatio()
            is CaptureEvent.ToggleFlashEvent -> toggleFlash()
            is CaptureEvent.EnlargeMinimiseOverlayEvent -> enlargeMinimiseOverlay()
            is CaptureEvent.NavigateToPreviewEvent -> navigateToPreview()
            is CaptureEvent.SystemUiVisibleEvent -> checkAndHideSystemUI()
            is CaptureEvent.CameraErrorEvent -> cameraError()
        }
    }

    private fun changeAspectRatio() {
        isPreviewExpanded = !isPreviewExpanded
        captureViewEffectLD.value = CaptureViewEffect.ExpandCameraPreviewEffect(isPreviewExpanded)
    }

    private fun screenLoad() {
        checkAndHideSystemUI()
        captureViewEffectLD.value = CaptureViewEffect.ExpandCameraPreviewEffect(isPreviewExpanded)
        currentViewState = currentViewState.copy(capturedImages = ImageCache.capturedImageUriList.toList())
    }

    private fun cameraError() {
        currentViewState = currentViewState.copy(error = ERROR_CAMERA)
    }

    private fun checkAndHideSystemUI() {
        if (isPreviewExpanded) {
            launch {
                delay(1000L)
                captureViewEffectLD.value = CaptureViewEffect.MakeImmersiveEffect
            }
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
        val fileName = "${fileName}_${Date().time}_${imageCounter.getAndIncrement()}.$imageFileExtension"

        try {
            val file = if (!useInternalStorage && isExternalStorageWritable()) {
                val externalDirectory = fileDirectoryProvider.getPublicDirectory()
                File(externalDirectory, fileName)

            } else {
                File(fileDirectoryProvider.getPrivateDirectory(), fileName)
            }

            pictureResult.toFile(file) { savedFile ->
                currentViewState = if (savedFile != null) {
                    val uriPath = savedFile.toURI()?.path!!

                    ImageCache.capturedImageUriList.add(0, uriPath)
                    currentViewState.copy(capturedImages = ImageCache.capturedImageUriList.toList())
                } else {
                    currentViewState.copy(error = ERROR_SAVE_FILE)
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage)

        }
    }
}