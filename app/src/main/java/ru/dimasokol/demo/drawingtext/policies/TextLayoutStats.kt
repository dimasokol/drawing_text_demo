package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.TextLayoutResult
import kotlin.math.sqrt

/**
 * Returns the fill factors (measured line width divided by [width]) of the interior lines of
 * this layout — all lines except the last line of each paragraph. Returns `null` when there
 * are no interior lines (single-line text or a whole layout of paragraph-final lines).
 */
internal fun TextLayoutResult.interiorLineFills(text: String, width: Int): FloatArray? {
    if (lineCount == 0) return null
    val fills = ArrayList<Float>(lineCount)
    for (i in 0 until lineCount) {
        // The last line ends its paragraph at the end of the text; other lines are final for
        // their paragraph when immediately followed by a newline.
        val isParagraphFinal = i == lineCount - 1 ||
            (getLineEnd(i) < text.length && text[getLineEnd(i)] == '\n')
        if (isParagraphFinal) continue
        fills.add((getLineRight(i) - getLineLeft(i)) / width)
    }
    return if (fills.isEmpty()) null else fills.toFloatArray()
}

/**
 * Mean and standard deviation of this array of line fills.
 */
internal fun FloatArray.meanStdDev(): Pair<Float, Float> {
    var sum = 0f
    for (value in this) sum += value
    val mean = sum / size
    var deviation = 0f
    for (value in this) {
        val delta = value - mean
        deviation += delta * delta
    }
    return mean to sqrt(deviation / size)
}

/**
 * The share of [areaWidth]x[areaHeight] covered by the line rectangles of this layout: the
 * total area of all line boxes (width times height of each line) divided by the drawing-area
 * area. Returns 0 for a degenerate empty area.
 */
internal fun TextLayoutResult.coverage(areaWidth: Int, areaHeight: Int): Float {
    if (areaWidth <= 0 || areaHeight <= 0) return 0f
    var covered = 0f
    for (i in 0 until lineCount) {
        val lineWidth = getLineRight(i) - getLineLeft(i)
        val lineHeight = getLineBottom(i) - getLineTop(i)
        covered += lineWidth * lineHeight
    }
    return covered / (areaWidth * areaHeight)
}