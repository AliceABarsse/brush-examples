package fr.caravellecode.brushexamples.rasterimage

import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.BlendMode
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
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
    val objectToDrawOnAsImageBitmap = ImageBitmap.imageResource(id = R.drawable.ic_chair_foreground)
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

    val polkaDotColor = Red
    val tintColor = Black

    Column(modifier = Modifier.verticalScroll(rememberScrollState(), true)) {
        WriteTitleName(titleName = "Draw Raster Image in Black with an all-over Red dot pattern, varying BlendMode")

        FlowRow(
            modifier = Modifier.background(LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (blendModeImage in listOfAllBlendModes().filterNot {
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
                ).contains(it)
            }) {

                for (blendModeColor in listOfAllBlendModes().filterNot {
                    listOf(
                        Clear,
                        Difference,
                        Dst,
                        DstAtop,
                        DstIn,
                        DstOut,
                        DstOver,
                        Exclusion,
                        Hardlight,
                        Hue,
                        Luminosity,
                        Saturation,
                        Src, SrcAtop,
                        SrcIn,
                        SrcOver,
                        Xor,
                    ).contains(it)
                }) {

                    Column(
                        modifier = Modifier
                            .width(intrinsicSize = IntrinsicSize.Min)
                            .padding(4.dp)
                            .border(Dp.Hairline, Black),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ShowValueText("img: $blendModeImage")
                        ShowValueText("col: $blendModeColor")

                        Box(modifier = Modifier
                            .size(90.dp)
                            .graphicsLayer {
                                compositingStrategy = CompositingStrategy.Offscreen
                            }
                            .drawWithContent {
                                drawRect(
                                    brush = polkaDotBrush,
                                    alpha = 1f,
                                    colorFilter = ColorFilter.tint(polkaDotColor)
                                )
                                drawImage(
                                    image = objectToDrawOnAsImageBitmap,
                                    blendMode = blendModeImage,
                                    dstSize = IntSize(
                                        this@drawWithContent.size.width.roundToInt(),
                                        this@drawWithContent.size.height.roundToInt()
                                    ),
                                    colorFilter = ColorFilter.tint(
                                        tintColor, blendModeColor
                                    ),
                                )
                            }) // Box content intentionally left blank
                    }

                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}

@Composable
private fun allBlendModes() = listOf(
    Clear,
    Difference,
    Dst,
    DstAtop,
    DstIn,
    DstOut,
    DstOver,
    Exclusion,
    Hardlight,
    Hue,
    Luminosity,
    Saturation,
    Src,
    SrcAtop,
    SrcIn,
    SrcOut,
    SrcOver,
    Xor,
)

@Preview
@Composable
fun BrushRasterImageHighlightPreview() {
    Surface {
        BrushExamplesTheme {
            BrushPatternRasterImageWithHighlight()
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

    val objectToFillImage = ImageBitmap.imageResource(id = R.drawable.ic_chair_foreground)

    Column {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                .background(LightGray),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            WriteTitleName(titleName = "Draw Raster Image with all-over tiled brush, varying BlendMode")

            for (blendMode in listOfAllBlendModes()) {
                Column(
                    modifier = Modifier
                        .width(intrinsicSize = IntrinsicSize.Min)
                        .padding(4.dp)
                        .border(Dp.Hairline, Black),
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

@Composable
fun BasicBlack(
    objectToDrawOnAsImageBitmap: ImageBitmap,
    tintColor: Color,
    polkaDotBrush: Brush,
    polkaDotColor: Color,
    blendModeMotifRect: BlendMode,
) {
    Column(
        modifier = Modifier
            .width(intrinsicSize = IntrinsicSize.Min)
            .padding(4.dp)
            .border(Dp.Hairline, Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShowValueText("dots: $blendModeMotifRect")
        ShowValueText("col: Black")
        ShowValueText("lght: none")
        ShowValueText("LTint: none")
        Box(modifier = Modifier
            .size(90.dp)
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {

                // no blendmode on first image
                drawImage(
                    image = objectToDrawOnAsImageBitmap,
                    dstSize = IntSize(
                        this@drawWithContent.size.width.roundToInt(),
                        this@drawWithContent.size.height.roundToInt()
                    ),
                    colorFilter = ColorFilter.tint(
                        tintColor, Modulate
                    ),
                )

                drawRect(
                    blendMode = SrcAtop, // only show parts that cover precedent image
                    brush = polkaDotBrush,
                    alpha = 1f,
                    colorFilter = ColorFilter.tint(
                        polkaDotColor, blendModeMotifRect
                    ),
                )


            }) // Box content intentionally left blank

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrushPatternRasterImageWithHighlight(modifier: Modifier = Modifier) {

    val polkaDotAsDrawable =
        ContextCompat.getDrawable(LocalContext.current, R.drawable.baseline_circle_4)
    val objectToDrawOnAsImageBitmap = ImageBitmap.imageResource(id = R.drawable.ic_chair_foreground)
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

    val polkaDotColor = Red
    val tintColor = White //Black //Blue
    val selected = mutableListOf<String>()

    Column(modifier = Modifier.verticalScroll(rememberScrollState(), true)) {
        WriteTitleName(titleName = "Draw Raster Image in White with an all-over Red dot pattern, varying BlendMode, and Highlight layer")
        FlowRow(
            modifier = Modifier.background(Green),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {

            BasicBlack(
                objectToDrawOnAsImageBitmap = objectToDrawOnAsImageBitmap,
                tintColor = tintColor,
                polkaDotBrush = polkaDotBrush,
                polkaDotColor = polkaDotColor,
                blendModeMotifRect = SrcAtop
            )

            for (blendModeHighlight in allBlendModes().filterNot {
                listOf(
                    Clear,
                    Dst,
                    DstAtop,
                    DstIn,
                    DstOut,
                    Hue,
                    Src,
                    SrcIn,
                    SrcOut,
                    Xor,
                ).contains(it)
            }) {

                for (blendModeMotifRect in allBlendModes().filterNot {
                    listOf(
                        Clear,
                        Difference,
                        Dst,
                        DstAtop,
                        DstIn,
                        DstOut,
                        DstOver,
                        Exclusion,
                        Hardlight,
                        Hue,
                        Luminosity,
                        Saturation,
                        Src,
                        SrcOut,
                        SrcOver,
                        Xor,
                    ).contains(it)
                }) {

                    for (blendModeHighlightTint in allBlendModes().filterNot {
                        listOf(
                            Clear,
                            Dst,
                            SrcOut,
                            Xor,
                        ).contains(it)
                    }) {

                        for (blendModeColor in listOf(Modulate)) {

                            Column(
                                modifier = Modifier
                                    .width(intrinsicSize = IntrinsicSize.Min)
                                    .padding(4.dp)
                                    .border(Dp.Hairline, Black)
                                    .clickable { selected.add("dots") },
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                ShowValueText("dots: $blendModeMotifRect")
                                ShowValueText("col: $blendModeColor")
                                ShowValueText("lght: $blendModeHighlight")
                                ShowValueText("LTint: $blendModeHighlightTint")
                                Box(modifier = Modifier
                                    .size(90.dp)
                                    .graphicsLayer {
                                        compositingStrategy = CompositingStrategy.Offscreen
                                    }
                                    .drawWithContent {

                                        // no blendmode on first image
                                        drawImage(
                                            image = objectToDrawOnAsImageBitmap,
                                            dstSize = IntSize(
                                                this@drawWithContent.size.width.roundToInt(),
                                                this@drawWithContent.size.height.roundToInt()
                                            ),
                                            colorFilter = ColorFilter.tint(
                                                tintColor, blendModeColor
                                            ),
                                        )

                                        // Add white version for lightening of drawing
                                        drawImage(
                                            image = objectToDrawOnAsImageBitmap,
                                            blendMode = blendModeHighlight,
                                            dstSize = IntSize(
                                                this@drawWithContent.size.width.roundToInt(),
                                                this@drawWithContent.size.height.roundToInt()
                                            ),
                                            colorFilter = ColorFilter.tint(
                                                // Test with Black, White, Yellow (complementary for Blue tint of object), and various alpha
                                                // complementary color in subtractive mode; for Blue -> Yellow
                                                Black.copy(alpha = 0.4f), blendModeHighlightTint
                                            ),
                                        )

                                        drawRect(
                                            blendMode = SrcAtop, // only show parts that cover precedent image
                                            brush = polkaDotBrush,
                                            alpha = 1f,
                                            colorFilter = ColorFilter.tint(
                                                polkaDotColor, blendModeMotifRect
                                            ),
                                        )


                                    }) // Box content intentionally left blank
                            }

                        }
                    } //  loop

                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            } // loop
        }
    }
}

@Composable
private fun ShowValueText(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(start = 4.dp),
        text = text,
        color = MaterialTheme.colorScheme.onSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall
    )
}