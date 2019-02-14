package io.roadzen.rzcameraandroid.util

import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AlertDialog

const val LOG_TAG = "RZCameraAndroid"
const val ERROR_SAVE_FILE = "Unable to save image to file. Please try again or contact support."
const val ERROR_CAMERA = "Unable to use your device camera. Please try again."

fun showErrorDialog(msg: String, context: Context) {
    // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
    val builder: AlertDialog.Builder? = context.let {
        AlertDialog.Builder(it)
    }
    builder?.setMessage(msg)?.setTitle("Error")

    val dialog: AlertDialog? = builder?.create()
    dialog?.show()
}

/* Checks if external storage is available for read and write */
fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}