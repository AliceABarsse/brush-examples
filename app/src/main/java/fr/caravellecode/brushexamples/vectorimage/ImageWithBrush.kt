package fr.caravellecode.brushexamples.vectorimage

import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import fr.caravellecode.brushexamples.R
import fr.caravellecode.brushexamples.ui.theme.BrushExamplesTheme
import kotlin.math.roundToInt


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrushPatternImage(modifier: Modifier = Modifier) {

    val objectToDrawOnAsDrawable = LocalContext.current.resources.getDrawable(R.drawable.ic_work_24)
    val polkaDotAsDrawable =
        LocalContext.current.resources.getDrawable(R.drawable.baseline_circle_4)
    val objectToDrawOnAsImageBitmap =
        (objectToDrawOnAsDrawable as VectorDrawable).toBitmap().asImageBitmap()
    val polkaDotImageBitmap = (polkaDotAsDrawable as VectorDrawable).toBitmap().asImageBitmap()
    val polkaDotBrush = remember(polkaDotImageBitmap) {
        ShaderBrush(
            shader = ImageShader(
                image = polkaDotImageBitmap,
                tileModeX = TileMode.Repeated,
                tileModeY = TileMode.Repeated
            )
        )
    }
    val polkaDotColor = MaterialTheme.colorScheme.onSurface

    Column {
        WriteTitleName(titleName = "Draw Image with an all-over dot pattern \nand Red tint, varying BlendMode")

        FlowRow(
            modifier = Modifier.background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (blendMode in listOfAllBlendModes()) {
                Column(
                    modifier = Modifier
                        .width(intrinsicSize = IntrinsicSize.Min)
                        .padding(4.dp)
                        .border(Dp.Hairline, Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    WriteValue("$blendMode")
                    Box(modifier = Modifier
                        .padding(4.dp)
                        .size(50.dp)
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawRect(
                                brush = polkaDotBrush,
                                alpha = 1f,
                                colorFilter = ColorFilter.tint(polkaDotColor),
                                )
                            drawImage(
                                image = objectToDrawOnAsImageBitmap,
                                dstSize = IntSize(
                                    this@drawWithContent.size.width.roundToInt(),
                                    this@drawWithContent.size.height.roundToInt()
                                ),
                                colorFilter = ColorFilter.tint(Color.Red),
                                blendMode = blendMode,
                                )


                        }) {

                        // Box content intentionally left blank

                        /*
                         // NB: th following does not work, BlendMode is only applied to tint,
                         // and the Image composable is not visible under opaque rectangle
                        Image(painter = painterResource(R.drawable.ic_work_24),
                            contentDescription = "briefcase",
                            colorFilter = ColorFilter.tint(Color.Red, blendMode = blendmode),
                        )
                        */
                    }

                }
            }
        }
    }
}

@Composable
fun WriteValue(value: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(4.dp),
        text = value,
        color = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
fun WriteTitleName(titleName: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(4.dp),
        text = titleName,
        color = MaterialTheme.colorScheme.onSecondary,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium
    )
}

@Preview
@Composable
fun BrushImagePreview() {
    Surface {

        BrushExamplesTheme {
            BrushPatternImage()
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrushGradientImage(modifier: Modifier = Modifier) {

    val objectToFill = LocalContext.current.resources.getDrawable(R.drawable.ic_work_24)
    val intervals = 12f

    val tilted1GrayBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listOf(Color.DarkGray, Color.Black),
                    from = Offset.Zero,
                    to = Offset(size.width / intervals, size.height / intervals),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }
    val tilted2GrayBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listOf(
                        Color.DarkGray.copy(alpha = 0.6f),
                        Color.Black.copy(alpha = 0.4f)
                    ),
                    from = Offset(size.width / intervals, 0f),
                    to = Offset(0f, size.height / intervals),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }

    val objectToFillImage = (objectToFill as VectorDrawable).toBitmap().asImageBitmap()

    Column {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            WriteTitleName(titleName = "Draw Image with all-over tiled brush, varying BlendMode")

            for (blendMode in listOfAllBlendModes()) {
                Column(
                    modifier = Modifier
                        .width(intrinsicSize = IntrinsicSize.Min)
                        .padding(4.dp)
                        .border(Dp.Hairline, Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    WriteValue("$blendMode")
                    Box(modifier = Modifier
                        .padding(4.dp)
                        .size(50.dp)
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawRect(
                                brush = tilted1GrayBrush,
                                alpha = 1f,
                            )
                            drawRect(
                                brush = tilted2GrayBrush,
                                alpha = .5f,
                            )
                            drawImage(
                                image = objectToFillImage,
                                blendMode = blendMode,
                                dstSize = IntSize(
                                    this@drawWithContent.size.width.roundToInt(),
                                    this@drawWithContent.size.height.roundToInt()
                                ),
                            )

                        }) {
                        // Box content intentionally left blank
                    }
                }
            }
        }
    }
}

@Composable
private fun listOfAllBlendModes() = listOf(
    BlendMode.Clear,
    BlendMode.Color,
    BlendMode.ColorBurn,
    BlendMode.ColorDodge,
    BlendMode.Darken,
    BlendMode.Difference,
    BlendMode.Dst,
    BlendMode.DstAtop,
    BlendMode.DstIn,
    BlendMode.DstOut,
    BlendMode.DstOver,
    BlendMode.Exclusion,
    BlendMode.Hardlight,
    BlendMode.Hue,
    BlendMode.Lighten,
    BlendMode.Luminosity,
    BlendMode.Modulate,
    BlendMode.Multiply,
    BlendMode.Overlay,
    BlendMode.Plus,
    BlendMode.Saturation,
    BlendMode.Screen,
    BlendMode.Softlight,
    BlendMode.Src,
    BlendMode.SrcAtop,
    BlendMode.SrcIn,
    BlendMode.SrcOut,
    BlendMode.SrcOver,
    BlendMode.Xor,
)

@Preview
@Composable
fun BrushGradientPreview() {
    BrushExamplesTheme {
        BrushGradientImage()
    }
}

