package io.roadzen.rzcameraandroid.imagepreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.roadzen.rzcameraandroid.base.BaseViewModel
import io.roadzen.rzcameraandroid.camera.FlowEnd
import io.roadzen.rzcameraandroid.camera.RzCamera
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ImagePreviewViewModel : BaseViewModel() {

    private var currentImageUri: String? = RzCamera.imageCache.capturedImageUriList[0]

    private val captureViewStateLD = MutableLiveData<ImagePreviewViewState>()
    private val captureViewEffectLD = MutableLiveData<ImagePreviewViewEffect>()
    val captureViewState: LiveData<ImagePreviewViewState>
        get() = captureViewStateLD
    val captureViewEffect: LiveData<ImagePreviewViewEffect>
        get() = captureViewEffectLD

    private var currentViewState = initImagePreviewViewState()
        set(value) {
            field = value
            captureViewStateLD.value = value
        }

    private val closeScreenCallback = {
        closeScreen()
    }

    init {
        RzCamera.endCameraFlow.add(closeScreenCallback)
    }

    override fun onCleared() {
        RzCamera.endCameraFlow.remove(closeScreenCallback)
        super.onCleared()
    }

    fun onEvent(captureEvent: ImagePreviewEvent) {
        when (captureEvent) {
            is ImagePreviewEvent.ScreenLoadEvent -> onScreenLoad()
            is ImagePreviewEvent.ImageTappedEvent -> imageTapped(captureEvent.uri)
            is ImagePreviewEvent.DeleteCurrentImage -> deleteCurrentImage()
            is ImagePreviewEvent.AddImageEvent -> closeScreen()
            is ImagePreviewEvent.DoneCapturingEvent -> doneCapturingImages()
            is ImagePreviewEvent.SystemUiVisibleEvent -> hideSystemUIWithDelay()
        }
    }

    private fun onScreenLoad() {
        currentViewState = currentViewState.copy(imageUriList = RzCamera.imageCache.capturedImageUriList.toList())
    }

    private fun imageTapped(uri: String) {
        currentImageUri = uri
        currentViewState = currentViewState.copy(imagePreviewUri = currentImageUri)
    }

    private fun deleteCurrentImage() {
        val currentImageIndex = RzCamera.imageCache.capturedImageUriList.indexOf(currentImageUri)
        RzCamera.imageCache.capturedImageUriList.removeAt(currentImageIndex)

        val fileUriToBeDeleted = currentImageUri

        if (RzCamera.imageCache.capturedImageUriList.isEmpty()) {
            deleteFile(fileUriToBeDeleted)
            closeScreen()
            return
        }

        currentImageUri = if (currentImageIndex == RzCamera.imageCache.capturedImageUriList.size) {
            RzCamera.imageCache.capturedImageUriList[currentImageIndex - 1]
        } else {
            RzCamera.imageCache.capturedImageUriList[currentImageIndex]
        }

        currentViewState = currentViewState.copy(
            imagePreviewUri = currentImageUri,
            imageUriList = RzCamera.imageCache.capturedImageUriList.toList(),
            isDeleting = true
        )

        launch {
            deleteFile(fileUriToBeDeleted)
            delay(500L)
            currentViewState = currentViewState.copy(isDeleting = false)
        }
    }

    private fun deleteFile(fileUriToBeDeleted: String?) {
        val file = File(fileUriToBeDeleted)
        file.delete()
    }

    private fun closeScreen() {
        captureViewEffectLD.value = ImagePreviewViewEffect.CloseScreenEffect
    }

    private fun doneCapturingImages() {
        RzCamera.stop(FlowEnd.COMPLETE)
    }

    private fun hideSystemUIWithDelay() {
        launch {
            delay(1000L)
            captureViewEffectLD.value = ImagePreviewViewEffect.MakeImmersiveEffect
        }
    }
}