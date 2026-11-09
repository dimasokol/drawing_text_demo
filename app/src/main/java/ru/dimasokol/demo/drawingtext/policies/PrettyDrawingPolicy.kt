package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints

/**
 * A drawing policy that aims for the most aesthetically pleasing and human-friendly
 * text layout. Implementations are expected to fine-tune wrapping, alignment, spacing
 * and similar details so the rendered text looks as good as possible.
 *
 * NOTE: This is a temporary placeholder — [measure] currently matches [DefaultDrawingPolicy]
 * exactly; the pretty layout behaviour has not been fleshed out yet.
 */
class PrettyDrawingPolicy : TextDrawingPolicy {

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

