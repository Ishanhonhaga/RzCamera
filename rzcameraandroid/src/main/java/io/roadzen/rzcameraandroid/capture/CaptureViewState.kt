package io.roadzen.rzcameraandroid.capture

import com.otaliastudios.cameraview.PictureResult
import io.roadzen.rzcameraandroid.RzCamera.Companion.rzContext

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
}

sealed class CaptureEvent {
    class ImageCapturedEvent(val result: PictureResult) : CaptureEvent()
    object ScreenLoadEvent : CaptureEvent()
    object ToggleFlashEvent : CaptureEvent()
    object EnlargeMinimiseOverlayEvent : CaptureEvent()
    object NavigateToPreviewEvent : CaptureEvent()
    object SystemUiVisibleEvent : CaptureEvent()
    object CameraErrorEvent : CaptureEvent()
    object ExitEvent : CaptureEvent()
}

fun initCaptureViewState(): CaptureViewState {
    return CaptureViewState(
        flashMode = rzContext.defaultFlashMode,
        overlayImageUri = rzContext.rzCameraInstanceDetails?.overlayImageUri,
        overlayImageResId = rzContext.rzCameraInstanceDetails?.overlayImageResId,
        capturedImages = rzContext.imageCache.capturedImageUriList.toList(),
        overlayEnlarged = false,
        overlayLabel = rzContext.rzCameraInstanceDetails?.overlayLabel,
        error = null
    )
}

