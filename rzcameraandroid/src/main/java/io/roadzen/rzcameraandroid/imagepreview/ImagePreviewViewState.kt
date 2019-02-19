package io.roadzen.rzcameraandroid.imagepreview

import io.roadzen.rzcameraandroid.util.ImageCache

data class ImagePreviewViewState(
    val imagePreviewUri: String,
    val imageUriList: List<String>,
    val isDeleting: Boolean
)

fun initImagePreviewViewState(): ImagePreviewViewState {
    return ImagePreviewViewState(
        imagePreviewUri = ImageCache.capturedImageUriList[0],
        imageUriList = ImageCache.capturedImageUriList,
        isDeleting = false
    )
}

sealed class ImagePreviewEvent {
    object ScreenLoadEvent : ImagePreviewEvent()
    data class ImageTappedEvent(val uri: String) : ImagePreviewEvent()
    object DeleteCurrentImage : ImagePreviewEvent()
}

sealed class ImagePreviewViewEffect {
    object CloseScreenEffect : ImagePreviewViewEffect()
}