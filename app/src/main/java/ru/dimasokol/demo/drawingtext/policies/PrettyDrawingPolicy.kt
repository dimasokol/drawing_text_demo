package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * A drawing policy that aims for an even, pleasant text block: it varies the drawing width
 * (within the drawing area, stepping by whole `sp` units) to make the lengths of the lines
 * as uniform as possible.
 *
 * Only the "interior" lines of each paragraph are taken into account — the last line of a
 * paragraph is allowed to stay short, so a short final line never spoils the uniformity of
 * the rest. If the text is short and fits (e.g. on a single line), the result is identical
 * to [DefaultDrawingPolicy]. The width is never reduced below 60% of the drawing-area width.
 */
class PrettyDrawingPolicy : TextDrawingPolicy {

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
        val fullWidthFills = fullWidthResult.interiorLineFills(text, areaWidth)
        if (fullWidthFills == null) return fullWidthResult

        val (firstMean, firstStdDev) = fullWidthFills.meanStdDev()
        var bestScore = firstMean - firstStdDev
        var bestResult = fullWidthResult

        // Scan towards narrower widths, shrinking by a whole sp per step and scoring the
        // uniformity of the interior lines; because we go from wide to narrow and replace
        // only on a strictly better score, ties keep the widest layout.
        val stepPx = units.spToPx(1f).takeIf { it > 0f } ?: 1f
        var w = areaWidth - stepPx
        while (w >= minWidth) {
            val wInt = w.roundToInt()
            val result = measureAt(textMeasurer, text, style, wInt, constraints.maxHeight)
            val fills = result.interiorLineFills(text, wInt)
            if (fills != null) {
                val (mean, stdDev) = fills.meanStdDev()
                val score = mean - stdDev
                if (score > bestScore) {
                    bestScore = score
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