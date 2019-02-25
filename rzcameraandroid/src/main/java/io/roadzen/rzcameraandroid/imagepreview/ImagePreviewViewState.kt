package io.roadzen.rzcameraandroid.imagepreview

import io.roadzen.rzcameraandroid.RzCamera.Companion.rzContext

data class ImagePreviewViewState(
    val imagePreviewUri: String?,
    val imageUriList: List<String>?,
    val isDeleting: Boolean
)

fun initImagePreviewViewState(): ImagePreviewViewState {
    return ImagePreviewViewState(
        imagePreviewUri = rzContext.imageCache.capturedImageUriList[0],
        imageUriList = rzContext.imageCache.capturedImageUriList,
        isDeleting = false
    )
}

sealed class ImagePreviewEvent {
    object ScreenLoadEvent : ImagePreviewEvent()
    data class ImageTappedEvent(val uri: String) : ImagePreviewEvent()
    object DeleteCurrentImage : ImagePreviewEvent()
    object AddImageEvent : ImagePreviewEvent()
    object DoneCapturingEvent : ImagePreviewEvent()
    object SystemUiVisibleEvent : ImagePreviewEvent()
}

sealed class ImagePreviewViewEffect {
    object CloseScreenEffect : ImagePreviewViewEffect()
    object MakeImmersiveEffect : ImagePreviewViewEffect()
}