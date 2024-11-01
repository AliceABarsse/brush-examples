package fr.caravellecode.brushexamples.captureImage

import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.drawWithCache
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
internal fun ExampleExportImage(modifier: Modifier = Modifier, content: @Composable () -> Unit) {

    var bitmapToShow by remember { mutableStateOf<ImageBitmap?>(null) }
    var exportMethod by remember { mutableStateOf<ExportMethod?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Parameter describing the export method
            MethodSelectorRadioButtons(
                Modifier.padding(4.dp),
                value = exportMethod,
                onSetValue = { exportMethod = it },
                label = "Select an export method"
            )

            // Wrap the content to share in this composable
            ExportableImageWithCTA(
                modifier = modifier.padding(8.dp),
                exportMethod = exportMethod,
                onBitmapCreated = { bitmapToShow = it },
                inputComposable = content
            )
        }

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

@Stable // because this image does not change and does need to be composed more than once
@Composable
internal fun InputContentVectorComposable() {
    val objectToFillImage =
        (LocalContext.current.resources.getDrawable(R.drawable.ic_work_24) as VectorDrawable).toBitmap()
            .asImageBitmap()
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

    Box(modifier = Modifier
        .background(Color.White)
        .padding(4.dp)
        .size(200.dp)
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {

            drawImage(
                image = objectToFillImage,
                dstSize = IntSize(
                    this@drawWithContent.size.width.roundToInt(),
                    this@drawWithContent.size.height.roundToInt()
                ),
                colorFilter = ColorFilter.tint(Color.Red),
            )

            drawRect(
                brush = polkaDotBrush,
                alpha = 1f,
                colorFilter = ColorFilter.tint(polkaDotColor),
                blendMode = BlendMode.SrcAtop,
            )
        })
}


@Stable // because this image does not change and does need to be composed more than once
@Composable
internal fun InputContentRasterComposable() {

    val objectToFillImage = ImageBitmap.imageResource(id = R.drawable.ic_chair_foreground)
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
    val polkaDotColor = Color.Blue

    Box(modifier = Modifier
        .background(Color.Green)
        .padding(4.dp)
        .size(200.dp)
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithCache {

            val objectColor = Color.DarkGray

            onDrawWithContent {
                drawRect(
                    brush = polkaDotBrush,
                    alpha = 1f,
                    colorFilter = ColorFilter.tint(polkaDotColor),
                )
                drawImage(
                    image = objectToFillImage,
                    blendMode = BlendMode.DstAtop,
                    colorFilter = ColorFilter.tint(objectColor, BlendMode.Modulate),
                    dstSize = IntSize(
                        this@onDrawWithContent.size.width.roundToInt(),
                        this@onDrawWithContent.size.height.roundToInt()
                    ),
                )

            }
        })
}
