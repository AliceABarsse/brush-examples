package fr.caravellecode.brushexamples.captureImage

import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import fr.caravellecode.brushexamples.R
import kotlin.math.roundToInt

@Composable
internal fun ExampleExportImage(modifier: Modifier = Modifier, content: @Composable (Boolean) -> Unit) {

    var bitmapToShow by remember { mutableStateOf<ImageBitmap?>(null) }
    var exportMethod by remember { mutableStateOf<ExportMethod?>(null) }
    var addBlendedMotif by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

            Column (modifier = Modifier.fillMaxHeight().weight(0.5f),
                verticalArrangement = Arrangement.SpaceBetween) {
                // Parameter describing the export method
                MethodSelectorRadioButtons(
                    Modifier.padding(4.dp),
                    value = exportMethod,
                    onSetValue = { exportMethod = it },
                    label = "Select an export method"
                )
                AddMotifLayerCheckBox(
                    modifier = Modifier.padding(4.dp),
                    value = addBlendedMotif,
                    onSetValue = { addBlendedMotif = it},
                    label = "Additional blending"
                )
            }

            // Wrap the content to share in this composable
            ExportableImageWithCTA(
                modifier = Modifier.weight(0.5f).padding(8.dp),
                exportMethod = exportMethod,
                onBitmapCreated = { bitmapToShow = it },
                inputComposable = { content(addBlendedMotif) }
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(4.dp))

        // do something with the exported image
        if (bitmapToShow == null) {
            Text(
                "(No exported bitmap available)",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Cursive,
                fontSize = 30.sp
            )
        } else {
            Text(
                "Exported image:",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge,
                textDecoration = TextDecoration.Underline
            )
        }

        bitmapToShow?.let { image ->

            if (image.asAndroidBitmap().isRecycled) {
                bitmapToShow = null
                Text("Image was recycled")
            } else {
                Box(
                    Modifier
                        .border(Dp.Hairline, MaterialTheme.colorScheme.secondary)
                        .size(200.dp)
                        .drawWithContent {
                            if (image.asAndroidBitmap().isRecycled) {
                                bitmapToShow = null
                            } else {
                                drawImage(
                                    image = image,
                                    dstSize = IntSize(
                                        this@drawWithContent.size.width.roundToInt(),
                                        this@drawWithContent.size.height.roundToInt()
                                    )
                                )
                            }
                        }
                )
            }
            Button(onClick = { bitmapToShow = null }) { Text("clear image") }
        }
    }
}

@Composable
fun AddMotifLayerCheckBox(modifier: Modifier, value: Boolean, onSetValue: (Boolean) -> Unit, label: String) {
    Text(modifier = modifier, text = label, style = MaterialTheme.typography.titleMedium)
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = value, onCheckedChange = onSetValue)
        Text("Add a blended motif to image")
    }
}

@Composable
fun MethodSelectorRadioButtons(
    modifier: Modifier,
    value: ExportMethod?,
    onSetValue: (ExportMethod?) -> Unit,
    label: String,
) {
    var selectedOption: ExportMethod? by remember { mutableStateOf<ExportMethod?>(value) }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        ExportMethod.entries.map { exportMethod ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = exportMethod == selectedOption, onClick = {
                    selectedOption = exportMethod
                    onSetValue(exportMethod)
                })
                Text(exportMethod.name, style = MaterialTheme.typography.bodyMedium)
            }
        }

    }
}

@Composable
internal fun InputContentVectorComposable(drawObjectFirst: Boolean, addBlendedMotif: Boolean) {

    val vectorImageBitmap = (LocalContext.current.getDrawable(R.drawable.ic_work_24) as VectorDrawable).toBitmap()
            .asImageBitmap()
    InputContentComposable(drawObjectFirst, vectorImageBitmap, addBlendedMotif = addBlendedMotif)
}

@Stable // because this image does not change and does need to be composed more than once
@Composable
internal fun InputContentRasterComposable(drawObjectFirst: Boolean, addBlendedMotif: Boolean) {

    val rasterImageBitmap = ImageBitmap.imageResource(id = R.drawable.ic_chair_foreground)
    InputContentComposable(drawObjectFirst, rasterImageBitmap, addBlendedMotif = addBlendedMotif)
}

@Stable
@Composable
private fun InputContentComposable(drawObjectFirst: Boolean, sourceImageBitmap: ImageBitmap, addBlendedMotif: Boolean) {

    val polkaDotAsDrawable =
        ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_circle_24)

    val polkaDotBrush = remember(polkaDotAsDrawable) {
        val polkaDotImageBitmap = (polkaDotAsDrawable as VectorDrawable).toBitmap().asImageBitmap()
        ShaderBrush(
            shader = ImageShader(
                image = polkaDotImageBitmap,
                tileModeX = TileMode.Repeated,
                tileModeY = TileMode.Repeated
            )
        )
    }
    val polkaDotColor = Color.Black
    val objectColor = Color.Red

    Box(modifier = Modifier
        .background(Color.Green)
        .padding(4.dp)
        .size(200.dp)
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {

            if (drawObjectFirst) {
                drawImage(
                    image = sourceImageBitmap,
                    dstSize = IntSize(
                        this@drawWithContent.size.width.roundToInt(),
                        this@drawWithContent.size.height.roundToInt()
                    ),
                    colorFilter = ColorFilter.tint(objectColor, BlendMode.Modulate),
                )
                if (addBlendedMotif) {
                    drawRect(
                        brush = polkaDotBrush,
                        alpha = 1f,
                        colorFilter = ColorFilter.tint(polkaDotColor),
                        blendMode = BlendMode.SrcAtop,
                    )
                }
            } else {

                if (addBlendedMotif) {
                    drawRect(
                        brush = polkaDotBrush,
                        alpha = 1f,
                        colorFilter = ColorFilter.tint(polkaDotColor),
                    )
                }

                drawImage(
                    image = sourceImageBitmap,
                    blendMode = BlendMode.DstAtop,
                    colorFilter = ColorFilter.tint(objectColor, BlendMode.Modulate),
                    dstSize = IntSize(
                        this@drawWithContent.size.width.roundToInt(),
                        this@drawWithContent.size.height.roundToInt()
                    ),
                )

            }
        })
}

