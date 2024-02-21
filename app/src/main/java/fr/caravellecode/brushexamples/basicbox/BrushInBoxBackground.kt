package fr.caravellecode.brushexamples.basicbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.caravellecode.brushexamples.ui.theme.BrushExamplesTheme

@Composable
fun BasicBrushInBoxBackground(modifier: Modifier = Modifier) {

    val threeColorStops = arrayOf(
        0.0f to Color.Yellow, 0.2f to Color.Red, 1f to Color.Blue
    )

    val colorStopsWithReturnFirstColor = arrayOf(
        0.0f to Color.Gray, 0.33f to Color.Red, 0.66f to Color.Yellow, 1f to Color.Gray
    )

    Column(
        modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "All base brush examples", modifier = modifier
        )

        for (brushValue in listOf(
            Brush.horizontalGradient(colorStops = threeColorStops),
            Brush.linearGradient(colorStops = threeColorStops),
            Brush.radialGradient(colorStops = threeColorStops),
            Brush.sweepGradient(colorStops = colorStopsWithReturnFirstColor),
        )) {
            Box(
                modifier = Modifier
                    .requiredSize(200.dp)
                    .background(brushValue)
            )
        }

    }
}

@Composable
fun TiledBrushInBoxBackground(modifier: Modifier = Modifier) {

    val colorStopsWithReturnFirstColor = arrayOf(
        0.0f to Color.Gray, 0.33f to Color.Red, 0.66f to Color.Yellow, 1f to Color.Gray
    )
    val threeColors = listOf(Color.Yellow, Color.Red, Color.Blue)
    val tileSize = with(LocalDensity.current) {
        50.dp.toPx()
    }

    Column(
        modifier = Modifier
            .background(Color.DarkGray)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "All base brush examples", color = Color.White, modifier = modifier
        )

        for (brushValue in listOf(
            Brush.horizontalGradient(
                colors = threeColors, endX = tileSize, tileMode = TileMode.Repeated
            ),
            Brush.linearGradient(
                colors = threeColors, end = Offset(tileSize, tileSize), tileMode = TileMode.Mirror
            ),
            Brush.radialGradient(
                colors = threeColors, center = Offset(tileSize, tileSize), tileMode = TileMode.Decal
            ),
            Brush.sweepGradient(
                colorStops = colorStopsWithReturnFirstColor, center = Offset(120f, 450f)
            ),
        )) {
            Box(
                modifier = Modifier
                    .requiredSize(200.dp)
                    .border(width = Dp.Hairline, color = Color.White)
                    .background(brushValue)
            )
        }

    }
}


@Composable
fun CustomBrushInBoxBackground(modifier: Modifier = Modifier) {

    val listColors = listOf(Color.White, Color.Blue)

    val intervals = 12f
    val customVerticalBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listColors,
                    from = Offset.Zero,
                    to = Offset(size.width / intervals, 0f),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }
    val customHorizontalBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listOf(Color.White.copy(alpha = 0.5f), Color.Blue.copy(alpha = 0.5f)),
                    from = Offset.Zero,
                    to = Offset(0f, size.height / intervals),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }
    val tilted1BlueBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listColors,
                    from = Offset.Zero,
                    to = Offset(size.width / intervals, size.height / intervals),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }
    val tilted1RedBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listOf(Color.White, Color.Red),
                    from = Offset.Zero,
                    to = Offset(size.width / intervals, size.height / intervals),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }
    val tilted2RedBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listOf(Color.White.copy(alpha = 0.6f), Color.Red.copy(alpha = 0.4f)),
                    from = Offset(size.width / intervals, 0f),
                    to = Offset(0f, size.height / intervals),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }
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
    val tilted3ColorBrush1 = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listOf(Color.Blue, Color.White, Color.Yellow),
                    from = Offset.Zero,
                    to = Offset(size.width / intervals, size.height / intervals),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }
    val tilted3ColorBrush2 = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listOf(
                        Color.Red.copy(alpha = 0.6f),
                        Color.Yellow.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.3f)
                    ),
                    from = Offset(size.width / intervals, 0f),
                    to = Offset(0f, size.height / intervals),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }
    val boxSize = 150.dp
    Column(
        modifier = Modifier
            .background(Color.DarkGray)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Custom plaid brush examples", color = Color.White, modifier = modifier
        )

        // straight plaid
        Box(
            modifier = Modifier
                .requiredSize(boxSize)
                .border(width = Dp.Hairline, color = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(customVerticalBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(customHorizontalBrush)
            )
        }
        // diamond red plaid
        Box(
            modifier = Modifier
                .requiredSize(boxSize)
                .border(width = Dp.Hairline, color = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted1RedBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted2RedBrush)
            )
        }
        // diamond gray plaid
        Box(
            modifier = Modifier
                .requiredSize(boxSize)
                .border(width = Dp.Hairline, color = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted1GrayBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted2GrayBrush)
            )
        }
        // mix colors diamond plaid
        Box(
            modifier = Modifier
                .requiredSize(boxSize)
                .border(width = Dp.Hairline, color = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted1BlueBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted2RedBrush)
            )
        }
        // 3-color plaid
        Box(
            modifier = Modifier
                .requiredSize(boxSize)
                .border(width = Dp.Hairline, color = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted3ColorBrush1)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tilted3ColorBrush2)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun BasicBrushPreview() {
    BrushExamplesTheme {
        BasicBrushInBoxBackground()
    }
}


@Preview(showBackground = true)
@Composable
fun TiledBrushInBoxBackgroundPreview() {
    BrushExamplesTheme {
        TiledBrushInBoxBackground()
    }
}


@Preview(showBackground = true)
@Composable
fun PlaidsBrushPreview() {
    BrushExamplesTheme {
        CustomBrushInBoxBackground()
    }
}
