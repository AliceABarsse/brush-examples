package fr.caravellecode.brushexamples.captureImage

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp


private const val labelShareImage = "Share this image"

@Composable
fun ShareableContentWithCTA(
    modifier: Modifier = Modifier,
    usePixelCopy: Boolean,
    onBitmapCreated: (ImageBitmap) -> Unit,
    contentToShare: @Composable () -> Unit,
) {
    val bitmapCapture = remember {
        BitmapCapture()
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {

        val stroke = remember {
            Stroke(
                width = 8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
        CaptureContent(modifier = Modifier.drawBehind {
            drawRoundRect(
                color = Color.Green, style = stroke, cornerRadius = CornerRadius(
                    8.dp.toPx()
                )
            )
        }
            .clip(RoundedCornerShape(8.dp)), captureImage = bitmapCapture, usePixelCopy = usePixelCopy) {
            contentToShare()
        }


        Button(modifier = Modifier,
            onClick = {
            bitmapCapture.capture()
            bitmapCapture.imageBitmap?.let {
                onBitmapCreated(it)
            }}, content = {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(text = labelShareImage)}
        )
    }
}


@Composable
fun CaptureContent(
    modifier: Modifier = Modifier,
    captureImage: BitmapCapture,
    usePixelCopy: Boolean,
    content: @Composable () -> Unit,
) {
    val view: View = LocalView.current
    val maxHeight = 2500f

    var composableBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    DisposableEffect(usePixelCopy) {

        captureImage.callback = {
            composableBounds?.let { realBounds ->
                if (realBounds.width == 0f || realBounds.height == 0f) return@let
                val bounds =
                    if (realBounds.height > maxHeight) realBounds.copy(bottom = realBounds.top + maxHeight)
                    else realBounds
                view.capture(bounds = bounds, onCaptured = { state: CaptureState ->
                    captureImage.captureState.value = state

                    if (state is CaptureState.Success) {
                        captureImage.bitmapState.value = state.data
                    }
                }, usePixelCopy = usePixelCopy)
            }
        }

        onDispose {
            val bmp = captureImage.bitmapState.value
            bmp?.apply {
                if (!isRecycled) {
                    recycle()
                }
            }
            captureImage.bitmapState.value = null
            captureImage.callback = null
        }
    }

    Box(modifier = modifier.onGloballyPositioned {
        composableBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.boundsInWindow()
        } else {
            it.boundsInRoot()
        }
    }) {
        content()
    }
}


class BitmapCapture {
    val captureState = mutableStateOf<CaptureState>(CaptureState.Initial)
    val bitmapState = mutableStateOf<Bitmap?>(null)
    internal var callback: (() -> Unit)? = null

    fun capture() {
        callback?.invoke()
    }

    val imageBitmap: ImageBitmap?
        get() = bitmapState.value?.asImageBitmap()
}


sealed class CaptureState {
    data object Initial : CaptureState()
    data class Error(val exception: Exception) : CaptureState()
    data class Success(val data: Bitmap) : CaptureState()
}



/**
 * Currently only works to capture Composable
 * as Bitmap for Activity window, does not work for Dialog window.
 * By default, it will use View.draw(Canvas), unless usePixelCopy is true.
 */
fun View.capture(
    bounds: Rect,
    onCaptured: (CaptureState) -> Unit,
    usePixelCopy: Boolean,
) {

    val activity = this.context.findActivity()

    try {

        val bitmap = Bitmap.createBitmap(
            bounds.width.toInt(),
            bounds.height.toInt(),
            Bitmap.Config.ARGB_8888,
        )

        // PixelCopy does not suffer from bad compositing outcome like View.draw(Canvas) does
        if (usePixelCopy) {

            // Use window to capture display in Activity,
            // this does not work with a dialog window, so use SurfaceView instead

            if (activity != null) {
                PixelCopy.request(
                    /* source = */ activity.window,
                    /* srcRect = */ android.graphics.Rect(
                        bounds.left.toInt(),
                        bounds.top.toInt(),
                        bounds.right.toInt(),
                        bounds.bottom.toInt()
                    ), /* dest = */ bitmap, /* listener = */ { status ->
                        onCopyFinished(status, onCaptured, bitmap)

                    }, /* listenerThread = */ Handler(Looper.getMainLooper())
                )
            }
        } else {

            val canvas = Canvas(bitmap).apply {
                translate(-bounds.left, -bounds.top)
            }

            this.draw(canvas)
            canvas.setBitmap(null)
            onCaptured.invoke(CaptureState.Success(bitmap))
        }
    } catch (e: Exception) {
        onCaptured.invoke(CaptureState.Error(e))
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    else -> null
}

private fun onCopyFinished(
    status: Int,
    onCaptured: (CaptureState) -> Unit,
    bitmap: Bitmap,
) {
    when (status) {
        PixelCopy.SUCCESS -> {
            onCaptured(CaptureState.Success(bitmap))
        }

        PixelCopy.ERROR_DESTINATION_INVALID -> {
            onCaptured(
                CaptureState.Error(
                    Exception(
                        "The destination isn't a valid copy target. " + "If the destination is a bitmap this can occur " + "if the bitmap is too large for the hardware to " + "copy to. " + "It can also occur if the destination " + "has been destroyed"
                    )
                )
            )
        }

        PixelCopy.ERROR_SOURCE_INVALID -> {
            onCaptured(
                CaptureState.Error(
                    Exception(
                        "It is not possible to copy from the source. " + "This can happen if the source is " + "hardware-protected or destroyed."
                    )
                )
            )
        }

        PixelCopy.ERROR_TIMEOUT -> {
            onCaptured(
                CaptureState.Error(
                    Exception(
                        "A timeout occurred while trying to acquire a buffer " + "from the source to copy from."
                    )
                )
            )
        }

        PixelCopy.ERROR_SOURCE_NO_DATA -> {
            onCaptured(
                CaptureState.Error(
                    Exception(
                        "The source has nothing to copy from. " + "When the source is a Surface this means that " + "no buffers have been queued yet. " + "Wait for the source to produce " + "a frame and try again."
                    )
                )
            )
        }

        else -> {
            onCaptured(
                CaptureState.Error(
                    Exception(
                        "The pixel copy request failed with an unknown error."
                    )
                )
            )
        }
    }
}
