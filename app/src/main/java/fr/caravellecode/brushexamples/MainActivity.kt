package fr.caravellecode.brushexamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.caravellecode.brushexamples.captureImage.ExampleCaptureImage
import fr.caravellecode.brushexamples.rasterimage.BrushGradientRasterImage
import fr.caravellecode.brushexamples.rasterimage.BrushPatternRasterImage
import fr.caravellecode.brushexamples.rasterimage.BrushPatternRasterImageWithHighlight
import fr.caravellecode.brushexamples.ui.theme.BrushExamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrushExamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var pickExample by remember {
                        mutableIntStateOf(0)
                    }
                   Column {
                       Row(modifier = Modifier.fillMaxWidth()) {

                           Button(modifier = Modifier.weight(weight = (1f/4)),
                               onClick = { pickExample = 1 }) {
                               Text(text = "Blend 1")
                           }
                           Button(modifier = Modifier.weight(weight = (1f/4)),onClick = { pickExample = 2 }) {
                               Text(text = "Blend 2")
                           }
                           Button(modifier = Modifier.weight(weight = (1f/4)),onClick = { pickExample = 3 }) {
                               Text(text = "Blend 3")
                           }
                           Button(modifier = Modifier.weight(weight = (1f/4)),onClick = { pickExample = 4 }) {
                               Text(text = "Capture image")
                           }
                       }
                       Divider(modifier = Modifier.fillMaxWidth())
                       when (pickExample) {
                           1 -> BrushPatternRasterImage()
                           2 -> BrushGradientRasterImage()
                           3 -> BrushPatternRasterImageWithHighlight()
                           4 -> ExampleCaptureImage()
                           else -> ShowText()
                       }
                   }
                }
            }
        }
    }
}

@Composable
fun ShowText() {
    Text(text = "Click on a button to see example")
}
