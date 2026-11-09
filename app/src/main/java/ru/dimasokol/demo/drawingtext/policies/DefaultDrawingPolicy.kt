package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints

/**
 * The default layout policy: wraps text to the canvas width and clips anything that
 * does not fit vertically, with no attempts to improve text appearance. This is the
 * baseline behaviour that other policies can build on.
 */
class DefaultDrawingPolicy : TextDrawingPolicy {

    override fun measure(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
    ): TextLayoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
        constraints = constraints,
    )
}

