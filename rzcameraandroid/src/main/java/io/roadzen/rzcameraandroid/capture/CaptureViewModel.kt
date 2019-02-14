package io.roadzen.rzcameraandroid.capture

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otaliastudios.cameraview.PictureResult
import io.roadzen.rzcameraandroid.RzContext.fileExtension
import io.roadzen.rzcameraandroid.RzContext.filePrefix
import io.roadzen.rzcameraandroid.RzContext.fileSuffix
import io.roadzen.rzcameraandroid.RzContext.startFullScreenPreview
import io.roadzen.rzcameraandroid.RzContext.useInternalStorage
import io.roadzen.rzcameraandroid.util.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger

internal class CaptureViewModel(
    captureViewState: CaptureViewState,
    private val fileDirectoryProvider: FileDirectoryProvider
) : ViewModel() {

    private val capturedImages = mutableListOf<String>()
    private var imageCounter: AtomicInteger = AtomicInteger(0)
    private var isPreviewExpanded = startFullScreenPreview

    private val captureViewStateLD = MutableLiveData<CaptureViewState>()
    private val captureViewEffectLD = MutableLiveData<CaptureViewEffect>()
    val captureViewState: LiveData<CaptureViewState>
        get() = captureViewStateLD
    val captureViewEffect: LiveData<CaptureViewEffect>
        get() = captureViewEffectLD

    private var currentViewState = captureViewState
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
        captureViewEffectLD.value = CaptureViewEffect.ExpandCameraPreview(isPreviewExpanded)
    }

    private fun screenLoad() {
        if (isPreviewExpanded) {
            // TODO
        }
        checkAndHideSystemUI()
    }

    private fun cameraError() {
        currentViewState = currentViewState.copy(error = ERROR_CAMERA)
    }

    private fun checkAndHideSystemUI() {
        if (isPreviewExpanded) {
            captureViewEffectLD.value = CaptureViewEffect.MakeImmersiveEffect
        }
    }

    private fun navigateToPreview() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        val fileName = "${filePrefix}_${imageCounter.getAndIncrement()}_${fileSuffix ?: ""}.$fileExtension"

        try {
            val file = if (useInternalStorage) {
                File(fileDirectoryProvider.getPrivateDirectory(), fileName)

            } else if (isExternalStorageWritable()) {
                val externalDirectory = fileDirectoryProvider.getPublicDirectory()
                if (externalDirectory == null) throw FileNotFoundException("Unable to get public directory")
                else File(externalDirectory, fileName)

            } else {
                throw FileNotFoundException("Unable to write to external storage")
            }

            pictureResult.toFile(file) { savedFile ->
                savedFile?.let {
                    val uriPath = it.toURI()?.path!!
                    capturedImages.add(uriPath)
                    currentViewState = currentViewState.copy(previewImageUri = uriPath)
                }
                    ?: throw RuntimeException("Lib unable to save image file to disk")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage)
            currentViewState = currentViewState.copy(error = ERROR_SAVE_FILE)
        }
    }
}