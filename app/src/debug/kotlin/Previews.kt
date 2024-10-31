package fr.caravellecode.brushexamples

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fr.caravellecode.brushexamples.captureImage.ExampleCaptureImage
import fr.caravellecode.brushexamples.captureImage.InputContentVectorComposable
import fr.caravellecode.brushexamples.ui.theme.BrushExamplesTheme


@Preview
@Composable
private fun ImagePreview() {
    BrushExamplesTheme {
        InputContentVectorComposable()
    }
}


@Preview
@Composable
private fun ExampleCaptureImagePreview() {
    BrushExamplesTheme {
        ExampleCaptureImage(content = { InputContentVectorComposable() })
    }
}
