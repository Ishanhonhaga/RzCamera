package io.roadzen.rzcameraandroid.capture

import com.otaliastudios.cameraview.PictureResult
import io.roadzen.rzcameraandroid.camera.RzCamera

enum class FlashMode { ON, OFF, AUTO }

data class CaptureViewState(
    val flashMode: FlashMode,
    val overlayImageUri: String?,
    val overlayImageResId: Int?,
    val capturedImages: List<String>,
    val overlayEnlarged: Boolean,
    val overlayLabel: String?,
    val error: String?
)

sealed class CaptureViewEffect {
    object MakeImmersiveEffect : CaptureViewEffect()
    object NavigateToImagePreviewEffect : CaptureViewEffect()
    object CloseScreenEffect : CaptureViewEffect()
    class ConfirmExitEffect(val msg: String) : CaptureViewEffect()
}

sealed class CaptureEvent {
    class ImageCapturedEvent(val result: PictureResult) : CaptureEvent()
    object ScreenLoadEvent : CaptureEvent()
    object ToggleFlashEvent : CaptureEvent()
    object EnlargeMinimiseOverlayEvent : CaptureEvent()
    object NavigateToPreviewEvent : CaptureEvent()
    object SystemUiVisibleEvent : CaptureEvent()
    class CameraErrorEvent(val errorMsg: String) : CaptureEvent()
    object ExitEvent : CaptureEvent()
    object BackPressedEvent : CaptureEvent()
}

fun initCaptureViewState(): CaptureViewState {
    return CaptureViewState(
        flashMode = RzCamera.defaultFlashMode,
        overlayImageUri = RzCamera.cameraInstanceInfo?.overlayImageUri,
        overlayImageResId = RzCamera.cameraInstanceInfo?.overlayImageResId,
        capturedImages = RzCamera.imageCache.capturedImageUriList.toList(),
        overlayEnlarged = false,
        overlayLabel = RzCamera.cameraInstanceInfo?.overlayLabel,
        error = null
    )
}

