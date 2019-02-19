package io.roadzen.rzcameraandroid.imagepreview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.roadzen.rzcameraandroid.base.BaseViewModel
import io.roadzen.rzcameraandroid.util.ImageCache
import io.roadzen.rzcameraandroid.util.LOG_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ImagePreviewViewModel : BaseViewModel() {

    private var currentImageUri: String = ImageCache.capturedImageUriList[0]

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

    fun onEvent(captureEvent: ImagePreviewEvent) {
        when (captureEvent) {
            is ImagePreviewEvent.ScreenLoadEvent -> onScreenLoad()
            is ImagePreviewEvent.ImageTappedEvent -> imageTapped(captureEvent.uri)
            is ImagePreviewEvent.DeleteCurrentImage -> deleteCurrentImage()
        }
    }

    private fun onScreenLoad() {
        currentViewState = currentViewState.copy(imageUriList = ImageCache.capturedImageUriList.toList())
    }

    private fun imageTapped(uri: String) {
        currentImageUri = uri
        currentViewState = currentViewState.copy(imagePreviewUri = currentImageUri)
    }

    private fun deleteCurrentImage() {
        val currentImageIndex = ImageCache.capturedImageUriList.indexOf(currentImageUri)
        Log.d(LOG_TAG, "index of to be deleted: $currentImageIndex\nUri to be deleted: $currentImageUri")
        ImageCache.capturedImageUriList.removeAt(currentImageIndex)

        val fileUriToBeDeleted = currentImageUri

        if (ImageCache.capturedImageUriList.isEmpty()) {
            deleteFile(fileUriToBeDeleted)
            captureViewEffectLD.value = ImagePreviewViewEffect.CloseScreenEffect
            return
        }

        currentImageUri = if (currentImageIndex == ImageCache.capturedImageUriList.size) {
            ImageCache.capturedImageUriList[currentImageIndex - 1]
        } else {
            ImageCache.capturedImageUriList[currentImageIndex]
        }

        currentViewState = currentViewState.copy(
            imagePreviewUri = currentImageUri,
            imageUriList = ImageCache.capturedImageUriList.toList(),
            isDeleting = true
        )

        launch {
            deleteFile(fileUriToBeDeleted)
            delay(1000L)
            currentViewState = currentViewState.copy(isDeleting = false)
        }
    }

    private fun deleteFile(fileUriToBeDeleted: String) {
        val file = File(fileUriToBeDeleted)
        if (file.delete()) Log.d(LOG_TAG, "$fileUriToBeDeleted deleted")
    }
}