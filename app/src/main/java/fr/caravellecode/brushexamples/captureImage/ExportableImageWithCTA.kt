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


private const val labelExportButton = "Export this image"

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
    val bitmapExport = remember {
        BitmapExport()
    }
    val onExport: () -> Unit = {
        bitmapExport.performExport()
    }

    // detect changes in export state
    LaunchedEffect(bitmapExport.exportState.value) {
        if (bitmapExport.exportState.value is ExportState.Success) {
            onBitmapCreated((bitmapExport.exportState.value as ExportState.Success).data.asImageBitmap())
        }
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {

        val stroke = remember {
            Stroke(
                width = 8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
        ContentToExport(modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    color = Color.Green, style = stroke, cornerRadius = CornerRadius(
                        8.dp.toPx()
                    )
                )
            }
            .clip(RoundedCornerShape(8.dp)),
            exportHandler = bitmapExport,
            exportMethod = exportMethod) {
            inputComposable()
        }

        ExportButton(isEnabled = (exportMethod != null), onClick = onExport)
    }
}

@Composable
private fun ExportButton(isEnabled: Boolean, onClick: () -> Unit) {
    Button(modifier = Modifier, enabled = isEnabled, onClick = onClick) {
        Icon(
            Icons.Default.Share, contentDescription = null, modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = labelExportButton)
    }
}

@Composable
private fun ContentToExport(
    modifier: Modifier = Modifier,
    exportHandler: BitmapExport,
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

            exportHandler.exportBlock = {

                if (method == ExportMethod.GraphicsLayer) {

                    coroutineScope.launch {
                        val image = deferredImage?.await()
                        image?.let {
                            view.performExport(
                                bounds = Rect.Zero, onExported = { _: ExportState ->
                                    exportHandler.exportState.value =
                                        ExportState.Success(data = it.asAndroidBitmap())
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
                        view.performExport(
                            bounds = bounds, onExported = { state: ExportState ->
                                exportHandler.exportState.value = state
                            }, exportMethod = method
                        )
                    }
                }
            }

            // Clean up
            onDispose {
                exportHandler.bitmapState.value?.apply {
                    if (!isRecycled) {
                        recycle()
                    }
                }
                exportHandler.bitmapState.value = null
                exportHandler.exportBlock = null
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

                    // call record to export the content in the graphics layer
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

private class BitmapExport {
    val exportState = mutableStateOf<ExportState>(ExportState.Initial)
    val bitmapState = mutableStateOf<Bitmap?>(null)
    var exportBlock: (() -> Unit)? = null

    fun performExport() {
        exportState.value = ExportState.Initial // reset
        exportBlock?.invoke()
    }
}


/**
 * Currently only works to export Composable
 * as Bitmap for Activity window, does not work for Dialog window.
 */
private fun View.performExport(
    bounds: Rect,
    onExported: (ExportState) -> Unit,
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

            ExportMethod.PixelCopy -> performPixelCopy(activity, bounds, bitmap, onExported)

            ExportMethod.ViewDrawCanvas -> drawToCanvas(bitmap, bounds, onExported)

            // actual export performed earlier
            ExportMethod.GraphicsLayer -> onExported(ExportState.Initial)

        }
    } catch (e: Exception) {
        onExported(ExportState.Error(e))
    }
}

private fun View.drawToCanvas(
    bitmap: Bitmap,
    bounds: Rect,
    onExported: (ExportState) -> Unit,
) {
    val canvas = Canvas(bitmap).apply {
        scale(0.8f, 0.8f)
        translate(-bounds.left * 0.8f, -bounds.top / 2)
    }

    this.draw(canvas)
    canvas.setBitmap(null)
    onExported(ExportState.Success(bitmap))
}

private fun performPixelCopy(
    activity: Activity?,
    bounds: Rect,
    bitmap: Bitmap,
    onExported: (ExportState) -> Unit,
) {
    if (activity != null) {

        PixelCopy.request(/* source = */ activity.window,/* srcRect = */
            android.graphics.Rect(
                bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()
            ), /* dest = */
            bitmap, /* listener = */
            { status ->
                onCopyFinished(status, onExported, bitmap)

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
    onExported: (ExportState) -> Unit,
    bitmap: Bitmap,
) {
    Log.i("Example", "on copy finished with status $status")

    when (status) {
        PixelCopy.SUCCESS -> onExported(ExportState.Success(bitmap))
        PixelCopy.ERROR_DESTINATION_INVALID -> onExported(
            ExportState.Error(
                Exception(
                    "The destination isn't a valid copy target. " + "If the destination is a bitmap this can occur " + "if the bitmap is too large for the hardware to " + "copy to. " + "It can also occur if the destination " + "has been destroyed"
                )
            )
        )

        PixelCopy.ERROR_SOURCE_INVALID -> onExported(
            ExportState.Error(
                Exception(
                    "It is not possible to copy from the source. " + "This can happen if the source is " + "hardware-protected or destroyed."
                )
            )
        )

        PixelCopy.ERROR_TIMEOUT -> onExported(
            ExportState.Error(
                Exception(
                    "A timeout occurred while trying to acquire a buffer " + "from the source to copy from."
                )
            )
        )

        PixelCopy.ERROR_SOURCE_NO_DATA -> onExported(
            ExportState.Error(
                Exception(
                    "The source has nothing to copy from. " + "When the source is a Surface this means that " + "no buffers have been queued yet. " + "Wait for the source to produce " + "a frame and try again."
                )
            )
        )

        else -> onExported(
            ExportState.Error(
                Exception(
                    "The PixelCopy request failed with an unknown error."
                )
            )
        )
    }
}
