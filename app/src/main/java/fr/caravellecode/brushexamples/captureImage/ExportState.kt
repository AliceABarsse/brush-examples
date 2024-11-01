package fr.caravellecode.brushexamples.captureImage

import android.graphics.Bitmap

internal sealed class ExportState {
    data object Initial : ExportState()
    data class Error(val exception: Exception) : ExportState()
    data class Success(val data: Bitmap) : ExportState()
}