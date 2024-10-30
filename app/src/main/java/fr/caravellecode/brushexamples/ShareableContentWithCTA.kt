package fr.caravellecode.brushexamples

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.hardware.camera2.CaptureResult
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@Composable
fun ShareableContentWithCTA(
    modifier: Modifier = Modifier,
    onBitmapCreated: (ImageBitmap) -> Unit,
    contentToShare: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val bitmapCapture = rememberBitmapCapture()

    Column(modifier = modifier) {
        CaptureContent(captureImage = bitmapCapture) {
            contentToShare()
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Share this image"
            }
            .clickable(onClick = {
                bitmapCapture.capture()
                bitmapCapture.imageBitmap?.let {
                    onBitmapCreated(it)
                }
            }), horizontalArrangement = Arrangement.End) {
            Text(text = "Share this image")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Share, contentDescription = null)
        }
    }
}


@Composable
fun CaptureContent(
    modifier: Modifier = Modifier,
    captureImage: BitmapCapture,
    content: @Composable () -> Unit,
) {
    val view: View = LocalView.current
    val maxHeight = 2500f

    var composableBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    DisposableEffect(Unit) {

        captureImage.callback = {
            composableBounds?.let { realBounds ->
                if (realBounds.width == 0f || realBounds.height == 0f) return@let
                val bounds =
                    if (realBounds.height > maxHeight) realBounds.copy(bottom = realBounds.top + maxHeight)
                    else realBounds
                view.capture(bounds) { state: CaptureState ->
                    captureImage.imageState.value = state

                    if (state is CaptureState.Success) {
                        captureImage.bitmapState.value = state.data
                    }
                }
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


@Composable
fun rememberBitmapCapture() = remember {
    BitmapCapture()
}


/**
 * Image of composable to be shared
 */
class BitmapCapture {
    val imageState = mutableStateOf<CaptureState>(CaptureState.Initial)

    val bitmapState = mutableStateOf<Bitmap?>(null)

    internal var callback: (() -> Unit)? = null

    fun capture() {
        callback?.invoke()
    }

    private val bitmap: Bitmap?
        get() = bitmapState.value

    val imageBitmap: ImageBitmap?
        get() = bitmap?.asImageBitmap()
}


sealed class CaptureState {
    data object Initial : CaptureState()
    data class Error(val exception: Exception) : CaptureState()
    data class Success(val data: Bitmap) : CaptureState()
}



/**
 * Currently only works to capture Composable
 * as Bitmap for Activity window, does not work for Dialog window.
 */
fun View.capture(
    bounds: Rect,
    bitmapCallback: (CaptureState) -> Unit,
) {

    val activity = this.context.findActivity()

    try {

        val bitmap = Bitmap.createBitmap(
            bounds.width.toInt(),
            bounds.height.toInt(),
            Bitmap.Config.ARGB_8888,
        )

        // PixelCopy does not suffer from bad compositing outcome like View.draw(Canvas) does
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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
                        onCopyFinished(status, bitmapCallback, bitmap)

                    }, /* listenerThread = */ Handler(Looper.getMainLooper())
                )
            }

        } else {

            val canvas = Canvas(bitmap).apply {
                translate(-bounds.left, -bounds.top)
            }

            this.draw(canvas)
            canvas.setBitmap(null)
            bitmapCallback.invoke(CaptureState.Success(bitmap))
        }
    } catch (e: Exception) {
        bitmapCallback.invoke(CaptureState.Error(e))
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    else -> null
}

private fun onCopyFinished(
    status: Int,
    bitmapCallback: (CaptureState) -> Unit,
    bitmap: Bitmap,
) {
    when (status) {
        PixelCopy.SUCCESS -> {
            bitmapCallback.invoke(CaptureState.Success(bitmap))
        }

        PixelCopy.ERROR_DESTINATION_INVALID -> {
            bitmapCallback.invoke(
                CaptureState.Error(
                    Exception(
                        "The destination isn't a valid copy target. " + "If the destination is a bitmap this can occur " + "if the bitmap is too large for the hardware to " + "copy to. " + "It can also occur if the destination " + "has been destroyed"
                    )
                )
            )
        }

        PixelCopy.ERROR_SOURCE_INVALID -> {
            bitmapCallback.invoke(
                CaptureState.Error(
                    Exception(
                        "It is not possible to copy from the source. " + "This can happen if the source is " + "hardware-protected or destroyed."
                    )
                )
            )
        }

        PixelCopy.ERROR_TIMEOUT -> {
            bitmapCallback.invoke(
                CaptureState.Error(
                    Exception(
                        "A timeout occurred while trying to acquire a buffer " + "from the source to copy from."
                    )
                )
            )
        }

        PixelCopy.ERROR_SOURCE_NO_DATA -> {
            bitmapCallback.invoke(
                CaptureState.Error(
                    Exception(
                        "The source has nothing to copy from. " + "When the source is a Surface this means that " + "no buffers have been queued yet. " + "Wait for the source to produce " + "a frame and try again."
                    )
                )
            )
        }

        else -> {
            bitmapCallback.invoke(
                CaptureState.Error(
                    Exception(
                        "The pixel copy request failed with an unknown error."
                    )
                )
            )
        }
    }
}
