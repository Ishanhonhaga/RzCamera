# RzCamera
A wrapper over CameraView(https://github.com/natario1/CameraView) to click multiple images and record videos

Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```
dependencies {
    implementation 'com.github.rdsarna:RzCamera:v0.0.2'
}
```

# Usage
```
RzCamera.with(this).cameraInstanceInfoList(
            listOf(
                RzCameraInstanceInfo(
                    fieldName = "front_car", // REQUIRED & Unique
                    overlayImageResId = R.drawable.front_image_4w, // Optional, default is null
                    overlayLabel = "Front of Car", // Optional, default is null
                    fileName = "frontCar", // Optional, default is "$fieldName_image"
                    fileExtension = "jpg" // Optional, default is jpg
                )
            )
        ).apply {
            resolution = Resolution.R1920x1080
            camSuccessCallback = { map -> // Returns a HashMap that maps "fieldName" to it's associated List of ImageUris
                Log.d(LOG_TAG, "Map of Images: $map")
            }

            camCancelCallback = {
                Log.d(LOG_TAG, "Camera Cancelled")
            }

            camErrorCallback = { errorMsg ->
                Log.d(LOG_TAG, errorMsg)
            }
        }.start()
```
