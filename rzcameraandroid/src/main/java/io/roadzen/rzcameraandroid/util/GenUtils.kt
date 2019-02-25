package io.roadzen.rzcameraandroid.util

import android.content.Context
import android.os.Environment
import android.view.Surface
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import io.roadzen.rzcameraandroid.camera.NoArgCallback

const val LOG_TAG = "SPECIALTY"
const val ERROR_SAVE_FILE = "Unable to save image to file. Please try again or contact support."
const val ERROR_CAMERA = "Unable to use your device camera. Please try again."

fun showErrorDialog(msg: String, context: Context) {
    val builder: AlertDialog.Builder? = context.let {
        AlertDialog.Builder(it)
    }
    builder
        ?.setMessage(msg)
        ?.setTitle("Error")
        ?.setPositiveButton("OK", null)

    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
}

fun showYesNoDialog(msg: String, context: Context, positiveCallback: NoArgCallback) {
    val builder: AlertDialog.Builder? = context.let {
        AlertDialog.Builder(it)
    }
    builder
        ?.setMessage(msg)
        ?.setTitle("Confirm")
        ?.setPositiveButton("Yes") { _, _ -> positiveCallback() }
        ?.setNegativeButton("No", null)

    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
}

/* Checks if external storage is available for read and write */
fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

enum class Orientation {
    LANDSCAPE, LANDSCAPE_REVERSE, PORTRAIT, PORTRAIT_REVERSE
}

fun getRotation(context: Context): Orientation {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val rotation = display.rotation
    return when(rotation){
        Surface.ROTATION_90 -> Orientation.LANDSCAPE
        Surface.ROTATION_270 -> Orientation.LANDSCAPE_REVERSE
        Surface.ROTATION_180 -> Orientation.PORTRAIT_REVERSE
        Surface.ROTATION_0 -> Orientation.PORTRAIT
        else ->
            Orientation.PORTRAIT
    }
}

enum class Resolution(val width: Int, val height: Int) {
    R720x480(720, 480),
    R1280x720(1280, 720),
    R1920x1080(1920, 1080),
    R2560x1440(2560, 1440),
    R3840x2160(3840, 2160),
    MAX(0, 0),
    MIN(640, 480)
}