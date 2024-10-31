package fr.caravellecode.brushexamples.captureImage

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


private const val labelShareImage = "Share this image"

enum class ExportMethod {
    ViewDrawCanvas, PixelCopy, GraphicsLayer,
}

/**
 * This Composable is responsible for showing the input content and
 * adding a button effector (CTA, ie Call To Action)
 * under the content so that the user can choose to export it.
 * @param exportMethod Choose among available methods
 */
@Composable
fun ExportableImageWithCTA(
    modifier: Modifier = Modifier,
    exportMethod: ExportMethod?,
    onBitmapCreated: (ImageBitmap) -> Unit,
    inputComposable: @Composable () -> Unit,
) {
    val bitmapCapture = remember {
        BitmapCapture()
    }
    val onShare: () -> Unit = {
        bitmapCapture.capture()
    }

    // detect changes in capture state
    LaunchedEffect(bitmapCapture.captureState.value) {
        if (bitmapCapture.captureState.value is CaptureState.Success) {
            onBitmapCreated((bitmapCapture.captureState.value as CaptureState.Success).data.asImageBitmap())
        }
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {

        val stroke = remember {
            Stroke(
                width = 8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
        CaptureContent(modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    color = Color.Green, style = stroke, cornerRadius = CornerRadius(
                        8.dp.toPx()
                    )
                )
            }
            .clip(RoundedCornerShape(8.dp)),
            captureImage = bitmapCapture,
            exportMethod = exportMethod) {
            inputComposable()
        }

        ShareButton(isEnabled = (exportMethod != null), onShare = onShare)
    }
}

@Composable
private fun ShareButton(isEnabled: Boolean, onShare: () -> Unit) {
    Button(modifier = Modifier, enabled = isEnabled, onClick = onShare) {
        Icon(
            Icons.Default.Share, contentDescription = null, modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = labelShareImage)
    }
}

@Composable
private fun CaptureContent(
    modifier: Modifier = Modifier,
    captureImage: BitmapCapture,
    exportMethod: ExportMethod?,
    content: @Composable () -> Unit,
) {
    val view: View = LocalView.current
    val maxHeight = 2500f

    // these bounds are only available once the content has been positioned
    var positionedComposableBounds by remember {
        mutableStateOf<Rect?>(null)
    }
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var deferredImage: Deferred<ImageBitmap>? = null

    exportMethod?.let { method ->

        DisposableEffect(method) {

            captureImage.captureBlock = {

                if (method == ExportMethod.GraphicsLayer) {

                    coroutineScope.launch {
                        val image = deferredImage?.await()
                        image?.let {
                            view.capture(
                                bounds = Rect.Zero, onCaptured = { _: CaptureState ->
                                    captureImage.captureState.value =
                                        CaptureState.Success(data = it.asAndroidBitmap())
                                }, exportMethod = method
                            )
                        }
                    }
                } else {

                    positionedComposableBounds?.let bounds@{ realBounds ->
                        if (realBounds.width == 0f || realBounds.height == 0f) return@bounds
                        val bounds =
                            if (realBounds.height > maxHeight) realBounds.copy(bottom = realBounds.top + maxHeight)
                            else realBounds
                        view.capture(
                            bounds = bounds, onCaptured = { state: CaptureState ->
                                captureImage.captureState.value = state
                            }, exportMethod = method
                        )
                    }
                }
            }

            // Clean up our BitmapCapture
            onDispose {
                captureImage.bitmapState.value?.apply {
                    if (!isRecycled) {
                        recycle()
                    }
                }
                captureImage.bitmapState.value = null
                captureImage.captureBlock = null
            }
        }
    }

    Box(modifier = modifier
        .onGloballyPositioned { layoutCoordinates: LayoutCoordinates ->
            positionedComposableBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutCoordinates.boundsInWindow()
            } else {
                layoutCoordinates.boundsInRoot()
            }
        }
        .then(
            // do not do this for the other export methods
            // View.drawCanvas in particular will fail if graphics Layer is used
            if (exportMethod == ExportMethod.GraphicsLayer) {
                Modifier.drawWithContent {

                    // call record to capture the content in the graphics layer
                    graphicsLayer.record {
                        // draw the contents of the composable into the graphics layer
                        this@drawWithContent.drawContent()
                    }
                    // draw the graphics layer on the visible canvas
                    drawLayer(graphicsLayer)
                    deferredImage = coroutineScope.async { graphicsLayer.toImageBitmap() }
                }
            } else {
                Modifier
            })) {
        content()
    }
}

private class BitmapCapture {
    val captureState = mutableStateOf<CaptureState>(CaptureState.Initial)
    val bitmapState = mutableStateOf<Bitmap?>(null)
    var captureBlock: (() -> Unit)? = null

    fun capture() {
        captureState.value = CaptureState.Initial // reset
        captureBlock?.invoke()
    }
}


/**
 * Currently only works to capture Composable
 * as Bitmap for Activity window, does not work for Dialog window.
 */
private fun View.capture(
    bounds: Rect,
    onCaptured: (CaptureState) -> Unit,
    exportMethod: ExportMethod,
) {

    val activity = this.context.findActivity()

    try {
        val bitmap = Bitmap.createBitmap(
            bounds.width.toInt(),
            bounds.height.toInt(),
            Bitmap.Config.ARGB_8888,
        )
        when (exportMethod) {

            ExportMethod.PixelCopy -> performPixelCopy(activity, bounds, bitmap, onCaptured)

            ExportMethod.ViewDrawCanvas -> drawToCanvas(bitmap, bounds, onCaptured)

            // already captured
            ExportMethod.GraphicsLayer -> onCaptured(CaptureState.Initial)

        }
    } catch (e: Exception) {
        onCaptured(CaptureState.Error(e))
    }
}

private fun View.drawToCanvas(
    bitmap: Bitmap,
    bounds: Rect,
    onCaptured: (CaptureState) -> Unit,
) {
    val canvas = Canvas(bitmap).apply {
        scale(0.8f, 0.8f)
        translate(-bounds.left * 0.8f, -bounds.top / 2)
    }

    this.draw(canvas)
    canvas.setBitmap(null)
    onCaptured(CaptureState.Success(bitmap))
}

private fun performPixelCopy(
    activity: Activity?,
    bounds: Rect,
    bitmap: Bitmap,
    onCaptured: (CaptureState) -> Unit,
) {
    if (activity != null) {

        PixelCopy.request(/* source = */ activity.window,/* srcRect = */
            android.graphics.Rect(
                bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()
            ), /* dest = */
            bitmap, /* listener = */
            { status ->
                onCopyFinished(status, onCaptured, bitmap)

            }, /* listenerThread = */
            Handler(Looper.getMainLooper())
        )
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
    Log.i("Example", "on copy finished with status $status")

    when (status) {
        PixelCopy.SUCCESS -> onCaptured(CaptureState.Success(bitmap))
        PixelCopy.ERROR_DESTINATION_INVALID -> onCaptured(
            CaptureState.Error(
                Exception(
                    "The destination isn't a valid copy target. " + "If the destination is a bitmap this can occur " + "if the bitmap is too large for the hardware to " + "copy to. " + "It can also occur if the destination " + "has been destroyed"
                )
            )
        )

        PixelCopy.ERROR_SOURCE_INVALID -> onCaptured(
            CaptureState.Error(
                Exception(
                    "It is not possible to copy from the source. " + "This can happen if the source is " + "hardware-protected or destroyed."
                )
            )
        )

        PixelCopy.ERROR_TIMEOUT -> onCaptured(
            CaptureState.Error(
                Exception(
                    "A timeout occurred while trying to acquire a buffer " + "from the source to copy from."
                )
            )
        )

        PixelCopy.ERROR_SOURCE_NO_DATA -> onCaptured(
            CaptureState.Error(
                Exception(
                    "The source has nothing to copy from. " + "When the source is a Surface this means that " + "no buffers have been queued yet. " + "Wait for the source to produce " + "a frame and try again."
                )
            )
        )

        else -> onCaptured(
            CaptureState.Error(
                Exception(
                    "The PixelCopy request failed with an unknown error."
                )
            )
        )
    }
}
