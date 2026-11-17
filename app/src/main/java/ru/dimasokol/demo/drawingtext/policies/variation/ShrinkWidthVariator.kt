package ru.dimasokol.demo.drawingtext.policies.variation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.utils.UnitsSource
import kotlin.math.roundToInt

/**
 * A [Variator] that narrows the drawing width by whole `sp` steps, down to 60% of the original
 * width, and stops as soon as a layout no longer overflows the drawing area.
 */
class ShrinkWidthVariator : Variator() {

    private var lastWidth = Float.NaN
    private var oneSp = 0f
    private var minimumWidth = 0f

    override fun findNext(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource
    ): TextLayoutResult? {
        if (lastWidth.isNaN()) {
            lastWidth = constraints.maxWidth.toFloat()
            oneSp = units.spToPx(1f).takeIf { it > 0f } ?: 1f
            minimumWidth = lastWidth * MINIMUM_WIDTH_RATIO
            return initialVariation(textMeasurer, text, style, constraints)
        }

        lastWidth -= oneSp

        if (lastWidth < minimumWidth) {
            return null
        }

        val measured = textMeasurer.measure(
            text = AnnotatedString(text),
            style = style,
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = Int.MAX_VALUE,
            constraints = constraints.copy(maxWidth = lastWidth.roundToInt())
        )

        return measured.takeIf { !it.hasVisualOverflow }
    }

    override fun reset() {
        super.reset()
        lastWidth = Float.NaN
    }

    private companion object {
        const val MINIMUM_WIDTH_RATIO = 0.6f
    }
}