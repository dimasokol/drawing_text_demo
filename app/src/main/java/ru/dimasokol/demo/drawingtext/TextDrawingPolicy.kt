package ru.dimasokol.demo.drawingtext

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints

/**
 * A layout strategy that decides how [text] is measured before it is drawn into the
 * drawing area. [MainScreen] delegates every `textMeasurer.measure` call to the
 * currently selected policy, and draws the produced layout unchanged.
 */
interface TextDrawingPolicy {

    /**
     * Measures [text] with [textMeasurer] within a canvas of [width]x[height] and
     * returns the resulting layout.
     */
    fun measure(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        width: Int,
        height: Int,
    ): TextLayoutResult
}

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
        width: Int,
        height: Int,
    ): TextLayoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
        constraints = Constraints(
            maxWidth = width,
            maxHeight = height,
        ),
    )
}

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
        width: Int,
        height: Int,
    ): TextLayoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
        constraints = Constraints(
            maxWidth = width,
            maxHeight = height,
        ),
    )
}