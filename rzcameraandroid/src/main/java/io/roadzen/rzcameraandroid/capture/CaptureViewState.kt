package io.roadzen.rzcameraandroid.capture

import com.otaliastudios.cameraview.PictureResult
import io.roadzen.rzcameraandroid.RzContext.defaultFlashMode
import io.roadzen.rzcameraandroid.RzContext.overlayImageResId
import io.roadzen.rzcameraandroid.RzContext.overlayImageUri
import io.roadzen.rzcameraandroid.RzContext.overlayLabel
import io.roadzen.rzcameraandroid.RzContext.prevCapturedImageUriList

enum class FlashMode { ON, OFF, AUTO }

data class CaptureViewState(
    val flashMode: FlashMode = FlashMode.AUTO,
    val overlayImageUri: String? = null,
    val overlayImageResId: Int? = null,
    val previewImageUri: String? = null,
    val overlayEnlarged: Boolean = false,
    val overlayLabel: String? = null,
    val error: String? = null
)

sealed class CaptureViewEffect {
    object MakeImmersiveEffect : CaptureViewEffect()
    class ExpandCameraPreview(val expand: Boolean) : CaptureViewEffect()
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
        previewImageUri = prevCapturedImageUriList?.get(0),
        overlayEnlarged = false,
        overlayLabel = overlayLabel,
        error = null
    )
}

