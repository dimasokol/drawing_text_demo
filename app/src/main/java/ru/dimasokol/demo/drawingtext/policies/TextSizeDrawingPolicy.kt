package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import ru.dimasokol.demo.drawingtext.policies.utils.UnitsSource
import ru.dimasokol.demo.drawingtext.policies.utils.coverage

/**
 * A drawing policy that sizes the font (in whole `sp` steps) to fill the drawing area as
 * much as possible without overflowing it; the fill is measured as the share of the area
 * covered by the rectangles containing the text lines.
 *
 * If the text already fits at the first measurement (like [DefaultDrawingPolicy]), the font
 * is enlarged — but never by more than 50% of the original size. If the text does not fit,
 * the font is reduced by the same principle, never going below 20% of the original size.
 */
class TextSizeDrawingPolicy : TextDrawingPolicy {

    override fun measure(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource,
    ): TextLayoutResult {
        val areaWidth = constraints.maxWidth
        val areaHeight = constraints.maxHeight

        // First measurement with the font as-is — same as DefaultDrawingPolicy.
        val baseline = measureAt(textMeasurer, text, style, constraints)

        val baseFontSize = if (style.fontSize.isSpecified) style.fontSize.value else 0f
        if (baseFontSize <= 0f) return baseline

        val fits = !baseline.hasVisualOverflow
        // When it fits we enlarge; when it overflows we shrink. An overflowing baseline is
        // never a valid best, so its coverage is only used in the fitting case.
        val step = if (fits) FONT_STEP_SP else -FONT_STEP_SP
        val limit = if (fits) baseFontSize * MAX_FONT_RATIO else baseFontSize * MIN_FONT_RATIO

        return scanFontSizes(
            textMeasurer = textMeasurer,
            text = text,
            style = style,
            baseFontSize = baseFontSize,
            step = step,
            limit = limit,
            constraints = constraints,
            areaWidth = areaWidth,
            areaHeight = areaHeight,
            bestCoverage = if (fits) baseline.coverage(areaWidth, areaHeight) else Float.NEGATIVE_INFINITY,
            bestResult = if (fits) baseline else null,
            fallback = baseline,
        )
    }

    private fun scanFontSizes(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        baseFontSize: Float,
        step: Float,
        limit: Float,
        constraints: Constraints,
        areaWidth: Int,
        areaHeight: Int,
        bestCoverage: Float,
        bestResult: TextLayoutResult?,
        fallback: TextLayoutResult,
    ): TextLayoutResult {
        var bestCoverage = bestCoverage
        var bestResult = bestResult

        var fontSize = baseFontSize + step
        val limitReached: (Float) -> Boolean =
            if (step > 0f) { value -> value <= limit + EPSILON }
            else { value -> value >= limit - EPSILON }

        while (limitReached(fontSize)) {
            val result = measureAt(textMeasurer, text, scaledStyle(style, baseFontSize, fontSize), constraints)
            if (!result.hasVisualOverflow) {
                val coverage = result.coverage(areaWidth, areaHeight)
                // Replace only on a strictly better coverage, so equally filled candidates
                // keep the smaller font size.
                if (coverage > bestCoverage) {
                    bestCoverage = coverage
                    bestResult = result
                }
            }
            fontSize += step
        }

        return bestResult ?: fallback
    }

    private fun scaledStyle(style: TextStyle, baseFontSize: Float, fontSize: Float): TextStyle {
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

    private fun measureAt(
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

    private companion object {
        /** Step used for scanning the font size, in sp. */
        const val FONT_STEP_SP = 1f

        /** The font may grow by at most 50% of the original size. */
        const val MAX_FONT_RATIO = 1.5f

        /** The font may shrink by at most 20% of the original size. */
        const val MIN_FONT_RATIO = 0.8f

        /** Tolerance for the floating-point font-size bounds. */
        const val EPSILON = 1e-4f
    }
}