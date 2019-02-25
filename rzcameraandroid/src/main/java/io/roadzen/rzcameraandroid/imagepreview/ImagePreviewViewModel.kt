package io.roadzen.rzcameraandroid.imagepreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.roadzen.rzcameraandroid.RzCamera.Companion.rzContext
import io.roadzen.rzcameraandroid.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ImagePreviewViewModel : BaseViewModel() {

    private var currentImageUri: String? = rzContext.imageCache.capturedImageUriList[0]

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
        rzContext.endCameraFlow.add(closeScreenCallback)
    }

    override fun onCleared() {
        rzContext.endCameraFlow.remove(closeScreenCallback)
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
        currentViewState = currentViewState.copy(imageUriList = rzContext.imageCache.capturedImageUriList.toList())
    }

    private fun imageTapped(uri: String) {
        currentImageUri = uri
        currentViewState = currentViewState.copy(imagePreviewUri = currentImageUri)
    }

    private fun deleteCurrentImage() {
        val currentImageIndex = rzContext.imageCache.capturedImageUriList.indexOf(currentImageUri)
        rzContext.imageCache.capturedImageUriList.removeAt(currentImageIndex)

        val fileUriToBeDeleted = currentImageUri

        if (rzContext.imageCache.capturedImageUriList.isEmpty()) {
            deleteFile(fileUriToBeDeleted)
            closeScreen()
            return
        }

        currentImageUri = if (currentImageIndex == rzContext.imageCache.capturedImageUriList.size) {
            rzContext.imageCache.capturedImageUriList[currentImageIndex - 1]
        } else {
            rzContext.imageCache.capturedImageUriList[currentImageIndex]
        }

        currentViewState = currentViewState.copy(
            imagePreviewUri = currentImageUri,
            imageUriList = rzContext.imageCache.capturedImageUriList.toList(),
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
        rzContext.cleanUpAndEnd(isCancel = false)
    }

    private fun hideSystemUIWithDelay() {
        launch {
            delay(1000L)
            captureViewEffectLD.value = ImagePreviewViewEffect.MakeImmersiveEffect
        }
    }
}