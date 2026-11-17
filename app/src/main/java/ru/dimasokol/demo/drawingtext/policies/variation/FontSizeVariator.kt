package ru.dimasokol.demo.drawingtext.policies.variation

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import ru.dimasokol.demo.drawingtext.policies.utils.UnitsSource
import kotlin.math.ceil
import kotlin.math.floor

/**
 * A [Variator] that changes the font size by whole `sp` steps. When the initial layout fits,
 * it grows the font (up to +30% of the original) and stops as soon as a variant overflows.
 * When the initial layout overflows, it shrinks the font (down to 70% of the original) until
 * the text fits again or the lower bound is reached, letting the estimator pick the best.
 */
class FontSizeVariator : Variator() {

    private var direction = 0
    private var baseFontSize = 0f
    private var minFontSize = 0f
    private var maxFontSize = 0f
    private var lastFontSize = 0f
    private var sizeStep = 0f

    override fun findNext(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource
    ): TextLayoutResult? {
        if (direction == 0) {
            val initial = textMeasurer.measure(
                text = text,
                style = style,
                overflow = TextOverflow.Clip,
                softWrap = true,
                maxLines = Int.MAX_VALUE,
                constraints = constraints,
            )

            direction = if (initial.hasVisualOverflow) -1 else 1
            baseFontSize = if (style.fontSize.isSpecified) style.fontSize.value else 0f
            lastFontSize = baseFontSize
            minFontSize = floor(baseFontSize * MIN_FONT_RATIO)
            maxFontSize = ceil(baseFontSize * MAX_FONT_RATIO)
            sizeStep = 1f * (direction.toFloat())
            return initial
        }

        if (baseFontSize <= 0f) return null

        lastFontSize += sizeStep
        if (lastFontSize !in minFontSize..maxFontSize) {
            return null
        }

        val measured = textMeasurer.measure(
            text = text,
            style = scaledStyle(style, lastFontSize),
            overflow = TextOverflow.Clip,
            softWrap = true,
            maxLines = Int.MAX_VALUE,
            constraints = constraints,
        )

        if (measured.hasVisualOverflow && direction > 0) {
            return null
        }

        return measured
    }

    override fun reset() {
        super.reset()
        direction = 0
    }

    private fun scaledStyle(style: TextStyle, fontSize: Float): TextStyle {
        val multiplier = fontSize / baseFontSize
        return style.copy(
            fontSize = fontSize.sp,
            lineHeight = if (style.lineHeight.isSpecified) {
                (style.lineHeight.value * multiplier).sp
            } else {
                TextUnit.Unspecified
            },
        )
    }


    private companion object {
        const val MIN_FONT_RATIO = 0.7f
        const val MAX_FONT_RATIO = 1.3f
    }
}