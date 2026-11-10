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

/**
 * A drawing policy that enlarges the font (by whole `sp` steps) to fill as much of the
 * drawing area as possible without overflowing it. The fill is measured as the share of the
 * area covered by the rectangles containing the text lines.
 *
 * If the text is short and fits entirely on one line, the result is identical to
 * [DefaultDrawingPolicy]. The font size never grows by more than 50% of the original size.
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

        // Baseline measurement with the font as-is — same as DefaultDrawingPolicy.
        val baseline = measureAt(textMeasurer, text, style, constraints)
        if (baseline.lineCount <= 1) return baseline

        val baseFontSize = if (style.fontSize.isSpecified) style.fontSize.value else 0f
        if (baseFontSize <= 0f) return baseline

        var bestCoverage = baseline.coverage(areaWidth, areaHeight)
        var bestResult = baseline

        // Scan the font size upwards in whole sp; candidates that overflow the drawing area
        // are rejected, and only a strictly better coverage replaces the current best, so
        // equally filled candidates keep the smaller font.
        val maxFontSize = baseFontSize * MAX_FONT_RATIO
        var fontSize = baseFontSize + FONT_STEP_SP
        while (fontSize <= maxFontSize) {
            val sizeMultiplier = fontSize / baseFontSize
            val candidateStyle = style.copy(
                fontSize = fontSize.sp,
                lineHeight = if (style.lineHeight.isSpecified) {
                    (style.lineHeight.value * sizeMultiplier).sp
                } else {
                    TextUnit.Unspecified
                },
            )
            val result = measureAt(textMeasurer, text, candidateStyle, constraints)
            if (!result.hasVisualOverflow) {
                val coverage = result.coverage(areaWidth, areaHeight)
                if (coverage > bestCoverage) {
                    bestCoverage = coverage
                    bestResult = result
                }
            }
            fontSize += FONT_STEP_SP
        }

        return bestResult
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
        /** The font may grow by at most 50% of the original size. */
        const val MAX_FONT_RATIO = 1.5f

        /** Step used to scan the increasing font size, in sp. */
        const val FONT_STEP_SP = 1f
    }
}