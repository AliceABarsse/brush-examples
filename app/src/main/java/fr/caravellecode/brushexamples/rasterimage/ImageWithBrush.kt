package fr.caravellecode.brushexamples.rasterimage

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode.Companion.Clear
import androidx.compose.ui.graphics.BlendMode.Companion.Difference
import androidx.compose.ui.graphics.BlendMode.Companion.Dst
import androidx.compose.ui.graphics.BlendMode.Companion.DstAtop
import androidx.compose.ui.graphics.BlendMode.Companion.DstIn
import androidx.compose.ui.graphics.BlendMode.Companion.DstOut
import androidx.compose.ui.graphics.BlendMode.Companion.DstOver
import androidx.compose.ui.graphics.BlendMode.Companion.Exclusion
import androidx.compose.ui.graphics.BlendMode.Companion.Hardlight
import androidx.compose.ui.graphics.BlendMode.Companion.Hue
import androidx.compose.ui.graphics.BlendMode.Companion.Luminosity
import androidx.compose.ui.graphics.BlendMode.Companion.Modulate
import androidx.compose.ui.graphics.BlendMode.Companion.Saturation
import androidx.compose.ui.graphics.BlendMode.Companion.Softlight
import androidx.compose.ui.graphics.BlendMode.Companion.Src
import androidx.compose.ui.graphics.BlendMode.Companion.SrcAtop
import androidx.compose.ui.graphics.BlendMode.Companion.SrcIn
import androidx.compose.ui.graphics.BlendMode.Companion.SrcOut
import androidx.compose.ui.graphics.BlendMode.Companion.SrcOver
import androidx.compose.ui.graphics.BlendMode.Companion.Xor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import fr.caravellecode.brushexamples.R
import fr.caravellecode.brushexamples.ui.theme.BrushExamplesTheme
import fr.caravellecode.brushexamples.vectorimage.WriteTitleName
import fr.caravellecode.brushexamples.vectorimage.WriteValue
import fr.caravellecode.brushexamples.vectorimage.listOfAllBlendModes
import kotlin.math.roundToInt


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrushPatternRasterImage(modifier: Modifier = Modifier) {

    val polkaDotAsDrawable =
        ContextCompat.getDrawable(LocalContext.current, R.drawable.baseline_circle_4)
    val objectToDrawOnAsImageBitmap = ImageBitmap.imageResource(id = R.mipmap.ic_chair_foreground)
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

    Column(modifier = Modifier.verticalScroll(rememberScrollState(), true)) {
        WriteTitleName(titleName = "Draw Raster Image with an all-over dot pattern \nand Red tint, varying BlendMode")
        FlowRow(
            modifier = Modifier.background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (blendModeImage in listOfAllBlendModes()
                .filter {
                    listOf(
                        Clear,
                        Dst,
                        DstIn,
                         DstOut,
                         DstOver,
                         Modulate,
                         Softlight,
                        Src,
                        SrcAtop,
                         SrcIn,
                         SrcOver,
                         Xor
                    ).contains(it).not()
                }) {

                for (blendModeColor in listOfAllBlendModes().filter {
                    val not = listOf(
                        Clear,
                        Difference,
                        Dst, DstAtop,
                        DstIn,
                        DstOut,
                        DstOver,
                        Exclusion,
                        Hardlight,
                        Hue,
                        Luminosity,
                        Saturation,
                        Src,SrcAtop,
                        SrcIn,
                        SrcOver,
                        SrcOut,
                        Xor
                    ).contains(it).not()
                    not
                }) {
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
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(start = 4.dp),
                            text = "img: $blendModeImage",
                            color = MaterialTheme.colorScheme.onSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(start = 4.dp),
                            text = "col: $blendModeColor",
                            color = MaterialTheme.colorScheme.onSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Box(modifier = Modifier
                            .size(90.dp)
                            .graphicsLayer {
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                            .drawWithContent {
                                drawRect(
                                    brush = polkaDotBrush,
                                    alpha = 1f,
                                    colorFilter = ColorFilter.tint(polkaDotColor),
                                )
                                drawImage(
                                    image = objectToDrawOnAsImageBitmap,
                                    blendMode = blendModeImage,
                                    dstSize = IntSize(
                                        this@drawWithContent.size.width.roundToInt(),
                                        this@drawWithContent.size.height.roundToInt()
                                    ),
                                    colorFilter = ColorFilter.tint(
                                        Color.Red, blendModeColor
                                    ),
                                )


                            }) // Box content intentionally left blank
                    }

                }
                Divider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun BrushRasterImagePreview() {
    Surface {
        BrushExamplesTheme {
            BrushPatternRasterImage()
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrushGradientRasterImage(modifier: Modifier = Modifier) {

    val objectToFillImage = ImageBitmap.imageResource(id = R.mipmap.ic_chair_foreground)

    Column {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            WriteTitleName(titleName = "Draw Raster Image with all-over tiled brush, varying BlendMode")

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
                        .drawWithCache {

                            val intervals = 12f
                            val color1 = Color(0xFFE6E2CE)
                            val color2 = Color(0xFF70F1F2)

                            val tilted1Brush = object : ShaderBrush() {
                                override fun createShader(size: Size): Shader {
                                    return LinearGradientShader(
                                        colors = listOf(color1, color2),
                                        from = Offset.Zero,
                                        to = Offset(0f, size.height / intervals),
                                        tileMode = TileMode.Mirror
                                    )
                                }
                            }

                            val tilted2Brush = object : ShaderBrush() {
                                override fun createShader(size: Size): Shader {
                                    return LinearGradientShader(
                                        colors = listOf(
                                            color1, color2
                                        ),
                                        from = Offset(size.width / intervals, 0f),
                                        to = Offset(0f, 0f),
                                        tileMode = TileMode.Mirror
                                    )
                                }
                            }

                            onDrawWithContent {
                                drawRect(
                                    brush = tilted1Brush,
                                    alpha = 1f,
                                )
                                drawRect(
                                    brush = tilted2Brush,
                                    alpha = .5f,
                                )
                                drawImage(
                                    image = objectToFillImage,
                                    blendMode = blendMode,
                                    dstSize = IntSize(
                                        this@onDrawWithContent.size.width.roundToInt(),
                                        this@onDrawWithContent.size.height.roundToInt()
                                    ),
                                )

                            }
                        }) // Box content intentionally left blank
                }
            }
        }
    }
}


@Preview
@Composable
fun BrushGradientRasterImagePreview() {
    BrushExamplesTheme {
        BrushGradientRasterImage()
    }
}

