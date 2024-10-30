package fr.caravellecode.brushexamples.captureImage

import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import fr.caravellecode.brushexamples.R
import fr.caravellecode.brushexamples.ui.theme.BrushExamplesTheme
import kotlin.math.roundToInt

@Composable
fun ExampleCaptureImage(modifier: Modifier = Modifier) {

    var bitmapToShow by remember { mutableStateOf<ImageBitmap?>(null) }
    var usePixelCopy by remember { mutableStateOf(false) }

    Column (modifier = Modifier.fillMaxWidth())
    {
        LabelledCheckbox(
            modifier,
            value = usePixelCopy,
            onSetValue = { usePixelCopy = it },
            label = "Check to use PixelCopy, uncheck to use View.draw(Canvas)"
        )
        ShareableContentWithCTA(modifier = modifier.padding(8.dp),
            usePixelCopy = usePixelCopy,
            onBitmapCreated = { bitmapToShow = it }) {
            DrawImage()
        }
        if (bitmapToShow == null) {
            Text(
                "(No bitmap captured right now)",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Cursive, fontSize = 30.sp
            )
        } else {
            Text(
                "Captured image:",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge,
                textDecoration = TextDecoration.Underline
            )
        }
        bitmapToShow?.let { image ->
            Box(
                Modifier
                    .size(250.dp)
                    .border(20.dp, MaterialTheme.colorScheme.secondary)
                    .drawWithContent {
                        drawImage(
                            image, dstSize = IntSize(
                                this@drawWithContent.size.width.roundToInt(),
                                this@drawWithContent.size.height.roundToInt()
                            )
                        )
                    })
            Button(onClick = { bitmapToShow = null }) { Text("clear image") }

        }
    }
}

@Preview
@Composable
private fun ImagePreview() {
    BrushExamplesTheme {
        DrawImage()
    }
}

@Composable
private fun DrawImage() {
    val objectToFillImage =
        (LocalContext.current.resources.getDrawable(R.drawable.ic_work_24) as VectorDrawable).toBitmap()
            .asImageBitmap()
    val polkaDotAsDrawable =
        ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_circle_24)

    val polkaDotImageBitmap =
        (polkaDotAsDrawable as VectorDrawable).toBitmap().asImageBitmap()
    val polkaDotBrush = remember(polkaDotImageBitmap) {
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

@Composable
private fun LabelledCheckbox(
    modifier: Modifier,
    value: Boolean,
    onSetValue: (Boolean) -> Unit,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .padding(4.dp)
    ) {
        Checkbox(
            checked = value, onCheckedChange = onSetValue
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = label,
        )
    }
}
