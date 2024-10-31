package fr.caravellecode.brushexamples.captureImage

import android.graphics.Bitmap

internal sealed class CaptureState {
    data object Initial : CaptureState()
    data class Error(val exception: Exception) : CaptureState()
    data class Success(val data: Bitmap) : CaptureState()
}