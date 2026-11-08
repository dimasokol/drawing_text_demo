package ru.dimasokol.demo.drawingtext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import ru.dimasokol.demo.drawingtext.ui.theme.DrawingTextTheme

private val DrawingAreaWidth = 240.dp
private val DrawingAreaHeight = 240.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var input by rememberSaveable { mutableStateOf("") }
    var drawnText by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.main_screen_title)) })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(stringResource(R.string.text_to_draw_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )
            Button(
                onClick = { drawnText = input },
                enabled = input.isNotEmpty(),
            ) {
                Text(stringResource(R.string.draw_button))
            }
            DrawingArea(text = drawnText)
        }
    }
}

/**
 * Draws [text] directly into the canvas — no [Text] composable involved.
 *
 * The text is measured against the canvas width, so it wraps the same way a TextView would;
 * anything that does not fit vertically is clipped by the rounded box.
 */
@Composable
private fun DrawingArea(text: String, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onSurface,
    )
    val background = MaterialTheme.colorScheme.surfaceVariant
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .width(DrawingAreaWidth)
            .height(DrawingAreaHeight)
            .clip(shape)
            .background(background)
            .padding(12.dp)
            .drawWithContent {
                drawContent()
                if (text.isEmpty()) return@drawWithContent

                val layout = textMeasurer.measure(
                    text = AnnotatedString(text),
                    style = textStyle,
                    overflow = TextOverflow.Clip,
                    softWrap = true,
                    maxLines = Int.MAX_VALUE,
                    constraints = Constraints(
                        maxWidth = size.width.toInt(),
                        maxHeight = size.height.toInt(),
                    ),
                )
                drawText(layout, topLeft = Offset.Zero)
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    DrawingTextTheme {
        MainScreen()
    }
}
