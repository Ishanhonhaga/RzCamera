package io.roadzen.rzcameraandroid.model

/**
 * Holds the details for one Camera Instance which can include one or more images.
 */
data class RzCameraInstanceDetails(

    /**
     * Image resource ID for image to be overlayed on the camera preview.
     * Note - If both URI and Res Id are set then, Res Id shall be used
     */
    val overlayImageResId: Int? = null,

    /**
     * File URI for image to be overlayed on the camera preview.
     * Note - If both URI and Res Id are set then, Res Id shall be used
     */
    val overlayImageUri: String? = null,

    /**
     * Text to be overlayed at the bottom of the camera preview.
     */
    val overlayLabel: String? = null,

    /**
     * Name of the field, that uniquely identifies one set of images.
     */
    val fieldName: String,

    /**
     * Prefix for all the image/video files. Default is "$fieldName_image".
     * Please note: The file name will be structured as -
     * "${fileName}_${current date time in millis}_${imageNumber}.${fileExtension}"
     */
    val fileName: String = "$fieldName + _image",

    /**
     * File extension for all the image files. Default is "jpg".
     * Please note: The file name will be structured as -
     * "${fileName}_${current date time in millis}_${imageNumber}.${imageFileExtension}"
     */
    val fileExtension: String = "jpg",

    /**
     * Any previously captured images
     */
    val prevCapturedImageUris: List<String>? = null
)