package fr.caravellecode.brushexamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import fr.caravellecode.brushexamples.basicbox.BasicBrushInBoxBackground
import fr.caravellecode.brushexamples.basicbox.CustomBrushInBoxBackground
import fr.caravellecode.brushexamples.basicbox.TiledBrushInBoxBackground
import fr.caravellecode.brushexamples.ui.theme.BrushExamplesTheme
import fr.caravellecode.brushexamples.vectorimage.BrushPatternImage

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
                    CustomBrushInBoxBackground()
                }
            }
        }
    }
}
