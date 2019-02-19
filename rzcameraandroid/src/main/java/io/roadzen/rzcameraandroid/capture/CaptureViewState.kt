package io.roadzen.rzcameraandroid.capture

import com.otaliastudios.cameraview.PictureResult
import io.roadzen.rzcameraandroid.RzContext.defaultFlashMode
import io.roadzen.rzcameraandroid.RzContext.overlayImageResId
import io.roadzen.rzcameraandroid.RzContext.overlayImageUri
import io.roadzen.rzcameraandroid.RzContext.overlayLabel
import io.roadzen.rzcameraandroid.util.ImageCache

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
    data class ExpandCameraPreviewEffect(val expand: Boolean) : CaptureViewEffect()
    object NavigateToImagePreviewEffect : CaptureViewEffect()
}

sealed class CaptureEvent {
    class ImageCapturedEvent(val result: PictureResult) : CaptureEvent()
    object ScreenLoadEvent : CaptureEvent()
    object ChangeAspectRatioEvent : CaptureEvent()
    object ToggleFlashEvent : CaptureEvent()
    object EnlargeMinimiseOverlayEvent : CaptureEvent()
    object NavigateToPreviewEvent : CaptureEvent()
    object SystemUiVisibleEvent : CaptureEvent()
    object CameraErrorEvent : CaptureEvent()
}

fun initCaptureViewState(): CaptureViewState {
    return CaptureViewState(
        flashMode = defaultFlashMode,
        overlayImageUri = overlayImageUri,
        overlayImageResId = overlayImageResId,
        capturedImages = ImageCache.capturedImageUriList.toList(),
        overlayEnlarged = false,
        overlayLabel = overlayLabel,
        error = null
    )
}

