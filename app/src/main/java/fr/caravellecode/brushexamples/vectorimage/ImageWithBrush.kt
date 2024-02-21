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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
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
            ImageShader(
                polkaDotImageBitmap, TileMode.Repeated, TileMode.Repeated
            )
        )
    }
    val polkaDotColor = Color.Black

    Column {
        FlowRow(
            modifier = Modifier.background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(4.dp),
                text = "Draw Image over an all-over dot pattern \nand use Red tint as a function of BlendMode:",
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            for (blendmode in listOf(
                BlendMode.Color,
                BlendMode.Clear,
                BlendMode.ColorBurn,
                BlendMode.ColorDodge,
                BlendMode.Darken,
                BlendMode.Dst,
                BlendMode.Difference,
                BlendMode.DstAtop,
                BlendMode.DstIn,
                BlendMode.DstOut,
                BlendMode.DstOver,
                BlendMode.Exclusion,
                BlendMode.Hardlight,
                BlendMode.Hue,
                BlendMode.Lighten,
                BlendMode.Luminosity,
                BlendMode.Multiply,
                BlendMode.Modulate,
                BlendMode.Overlay,
                BlendMode.Plus,
                BlendMode.Saturation,
                BlendMode.SrcIn,
                BlendMode.Screen,
                BlendMode.Softlight,
                BlendMode.Src,
                BlendMode.SrcAtop,
                BlendMode.SrcOut,
                BlendMode.SrcOver,
                BlendMode.Xor,
            )) {
                Column(
                    modifier = Modifier
                        .width(intrinsicSize = IntrinsicSize.Min)
                        .padding(4.dp)
                        .border(Dp.Hairline, Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray)
                            .padding(4.dp),
                        text = "$blendmode",
                        color = Color.White
                    )
                    Box(modifier = Modifier
                        .padding(4.dp)
                        .size(50.dp)
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawRect(
                                brush = polkaDotBrush,
                                alpha = 1f,
                                colorFilter = ColorFilter.tint(polkaDotColor)
                            )
                            drawImage(
                                image = objectToDrawOnAsImageBitmap,
                                blendMode = blendmode,
                                dstSize = IntSize(
                                    this@drawWithContent.size.width.roundToInt(),
                                    this@drawWithContent.size.height.roundToInt()
                                ),
                                colorFilter = ColorFilter.tint(Color.Red),
                            )
                        }) {
                        // intentionally empty

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

@Preview
@Composable
fun BrushImagePreview() {
    BrushExamplesTheme {
        BrushPatternImage()
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrushGradientImage(modifier: Modifier = Modifier) {

    val threeColorStops = arrayOf(
        0.0f to Color.Yellow, 0.2f to Color.Red, 1f to Color.Blue
    )

    val colorStopsWithReturnFirstColor = arrayOf(
        0.0f to Color.Gray, 0.33f to Color.Red, 0.66f to Color.Yellow, 1f to Color.Gray
    )

    val objectToFill = LocalContext.current.resources.getDrawable(R.drawable.ic_work_24)

    val allOverPattern = LocalContext.current.resources.getDrawable(R.drawable.baseline_circle_4)

    val objectToFillImage = (objectToFill as VectorDrawable).toBitmap().asImageBitmap()
    val patternImage = (allOverPattern as VectorDrawable).toBitmap().asImageBitmap()


    Column {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(4.dp),
                text = "Draw Image with all-over gradient",
                color = Color.White
            )

                for (brushValue in listOf(
                    Brush.linearGradient(colorStops = threeColorStops),
                )) {

                    Column(
                        modifier = Modifier
                            .width(intrinsicSize = IntrinsicSize.Min)
                            .padding(4.dp)
                            .border(Dp.Hairline, Color.Black),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.DarkGray)
                                .padding(4.dp), text = "$brushValue", color = Color.White
                        )

                        Box(modifier = Modifier
                            .padding(4.dp)
                            .size(50.dp)
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                drawRect(
                                    brush = brushValue,
                                    alpha = 1f,
                                )
                                drawImage(
                                    image = objectToFillImage,
                                    blendMode = BlendMode.DstIn,
                                    dstSize = IntSize(
                                        this@drawWithContent.size.width.roundToInt(),
                                        this@drawWithContent.size.height.roundToInt()
                                    ),
                                )

                            }) {


                        }
                    }
            }
        }
    }
}

@Preview
@Composable
fun BrushGradientPreview() {
    BrushExamplesTheme {
        BrushGradientImage()
    }
}

