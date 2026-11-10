package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.utils.UnitsSource
import ru.dimasokol.demo.drawingtext.policies.utils.boundingBoxCoverage
import kotlin.math.roundToInt

/**
 * A drawing policy that varies the drawing width (in whole `sp` steps, down to 60% of the
 * drawing-area width) and picks the layout whose line rectangles cover the largest share of
 * the rectangle bounding the whole measured result — the coverage is relative to the result's
 * own bounding box, not to the drawing area.
 *
 * If the text is short and fits entirely on one line, the result is identical to
 * [DefaultDrawingPolicy]. Layouts that overflow the drawing area are rejected.
 */
class CoverageDrawingPolicy : TextDrawingPolicy {

    override fun measure(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource,
    ): TextLayoutResult {
        val areaWidth = constraints.maxWidth
        if (areaWidth <= 0) return measureAt(textMeasurer, text, style, areaWidth, constraints.maxHeight)
        val minWidth = (areaWidth * MIN_WIDTH_RATIO).toInt()

        // Baseline at the full drawing width — the same layout DefaultDrawingPolicy would give.
        val fullWidthResult = measureAt(textMeasurer, text, style, areaWidth, constraints.maxHeight)
        if (fullWidthResult.lineCount <= 1) return fullWidthResult

        var bestCoverage = fullWidthResult.boundingBoxCoverage()
        var bestResult = fullWidthResult

        // Scan towards narrower widths, shrinking by a whole sp per step; only a strictly
        // better coverage replaces the current best, so ties keep the widest layout.
        val stepPx = units.spToPx(1f).takeIf { it > 0f } ?: 1f
        var w = areaWidth - stepPx
        while (w >= minWidth) {
            val wInt = w.roundToInt()
            val result = measureAt(textMeasurer, text, style, wInt, constraints.maxHeight)
            if (!result.hasVisualOverflow) {
                val coverage = result.boundingBoxCoverage()
                if (coverage > bestCoverage) {
                    bestCoverage = coverage
                    bestResult = result
                }
            }
            w -= stepPx
        }

        return bestResult
    }

    private fun measureAt(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        width: Int,
        maxHeight: Int,
    ): TextLayoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
        constraints = Constraints(
            maxWidth = width,
            maxHeight = maxHeight,
        ),
    )

    private companion object {
        /** The narrowest candidate width is 60% of the drawing-area width. */
        const val MIN_WIDTH_RATIO = 0.6f
    }
}