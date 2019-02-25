package io.roadzen.rzcameraandroid.capture

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.otaliastudios.cameraview.*
import io.roadzen.rzcameraandroid.R
import io.roadzen.rzcameraandroid.camera.RzCamera
import io.roadzen.rzcameraandroid.imagepreview.ImagePreviewActivity
import io.roadzen.rzcameraandroid.util.*
import kotlinx.android.synthetic.main.activity_capture.*


internal class CaptureActivity : AppCompatActivity(), FileDirectoryProvider {

    private val viewModel: CaptureViewModel by lazy { getViewModel { CaptureViewModel(this) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)

        viewModel.captureViewEffect.observe(this, Observer { handleViewEffect(it) })
        viewModel.captureViewState.observe(this, Observer { render(it) })

        setUpViews()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(CaptureEvent.ScreenLoadEvent)
    }

    override fun onBackPressed() {
        viewModel.onEvent(CaptureEvent.BackPressedEvent)
    }

    private fun setUpViews() {
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0 && hasWindowFocus()) {
                viewModel.onEvent(CaptureEvent.SystemUiVisibleEvent)
            }
        }

        cameraView.setLifecycleOwner(this)
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                viewModel.onEvent(CaptureEvent.ImageCapturedEvent(result))
            }

            override fun onCameraError(exception: CameraException) {
                Log.e(LOG_TAG, exception.localizedMessage)
                viewModel.onEvent(CaptureEvent.CameraErrorEvent(exception.localizedMessage))
            }
        })

        setCameraResolution()

        flashButton?.setOnClickListener { viewModel.onEvent(CaptureEvent.ToggleFlashEvent) }
        previewImage?.setOnClickListener { viewModel.onEvent(CaptureEvent.NavigateToPreviewEvent) }
        overlayImage?.setOnClickListener { viewModel.onEvent(CaptureEvent.EnlargeMinimiseOverlayEvent) }
        enlargedOverlayImage?.setOnClickListener { viewModel.onEvent(CaptureEvent.EnlargeMinimiseOverlayEvent) }
        captureButton?.setOnClickListener { cameraView.takePicture() }
    }

    private fun setCameraResolution() {
        val width = SizeSelectors.minWidth(RzCamera.resolution.width)
        val height = SizeSelectors.minHeight(RzCamera.resolution.height)
        val dimensions = SizeSelectors.and(width, height) // Matches sizes bigger than 1000x2000.
        val ratio = SizeSelectors.aspectRatio(AspectRatio.of(16, 9), 0f) // Matches 1:1 sizes.

        val result = if (RzCamera.resolution == Resolution.MAX) {
            SizeSelectors.or(SizeSelectors.biggest())
        } else {
            SizeSelectors.or(
                SizeSelectors.and(ratio, dimensions), // Try to match both constraints
                ratio, // If none is found, at least try to match the aspect ratio
                SizeSelectors.biggest() // If none is found, take the biggest
            )
        }
        cameraView.setPictureSize(result)
    }

    private fun render(viewState: CaptureViewState) {

        // FLASH MODE
        when (viewState.flashMode) {
            FlashMode.ON -> {
                cameraView.flash = Flash.ON
                flashButton?.setImageResource(R.drawable.ic_flash_on)
            }
            FlashMode.OFF -> {
                cameraView.flash = Flash.OFF
                flashButton?.setImageResource(R.drawable.ic_flash_off)
            }
            FlashMode.AUTO -> {
                cameraView.flash = Flash.AUTO
                flashButton?.setImageResource(R.drawable.ic_flash_auto)
            }
        }

        // OVERLAY LABEL
        viewState.overlayLabel?.let {
            overlayLabel?.visibility = View.VISIBLE
            overlayLabel?.text = it
        }

        // OVERLAY IMAGE
        if (viewState.overlayImageUri == null && viewState.overlayImageResId == null) {
            overlayImage?.visibility = View.GONE
            enlargedOverlayImage?.visibility = View.GONE
        } else {
            overlayImage?.visibility = View.VISIBLE
            if (viewState.overlayImageUri?.isNotEmpty() == true) {
                GlideApp.with(this).load(viewState.overlayImageUri).into(overlayImage as ImageButton)
                GlideApp.with(this).load(viewState.overlayImageUri).into(enlargedOverlayImage as ImageView)
            }
            viewState.overlayImageResId?.let {
                GlideApp.with(this).load(it).into(overlayImage as ImageButton)
                GlideApp.with(this).load(it).into(enlargedOverlayImage as ImageView)
            }
        }

        // PREVIEW IMAGE
        if (viewState.capturedImages.isNotEmpty()) {
            previewImage?.visibility = View.VISIBLE
            GlideApp.with(this).load(viewState.capturedImages[0]).into(previewImage as ImageButton)
        } else {
            previewImage?.visibility = View.GONE
        }

        // ENLARGE OVERLAY
        if (viewState.overlayEnlarged)
            enlargedOverlayImage?.visibility = View.VISIBLE
        else
            enlargedOverlayImage?.visibility = View.GONE

        // ERROR
        viewState.error?.let {
            showErrorDialog(it, this)
        }
    }

    private fun handleViewEffect(viewEffect: CaptureViewEffect) {
        when (viewEffect) {
            is CaptureViewEffect.MakeImmersiveEffect -> hideSystemUI()
            is CaptureViewEffect.NavigateToImagePreviewEffect -> navigateToImagePreview()
            is CaptureViewEffect.CloseScreenEffect -> { finish() }
            is CaptureViewEffect.ConfirmExitEffect -> showConfirmExitDialog(viewEffect.msg)
        }
    }

    private fun showConfirmExitDialog(msg: String) {
        showYesNoDialog(msg, this) {
            viewModel.onEvent(CaptureEvent.ExitEvent)
        }
    }

    private fun navigateToImagePreview() {
        startActivity(Intent(this, ImagePreviewActivity::class.java))
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)
    }

    override fun getPrivateDirectory(): String {
        return filesDir.absolutePath
    }

    override fun getPublicDirectory(): String? {
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
        // TODO: Change to -
        // Environment.getExternalStoragePublicDirectory(
        //            Environment.DIRECTORY_PICTURES)
    }
}
