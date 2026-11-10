package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints

/**
 * A layout strategy that decides how text is measured before it is drawn into the
 * drawing area. [MainScreen][ru.dimasokol.demo.drawingtext.MainScreen] delegates every
 * `textMeasurer.measure` call to the currently selected policy, and draws the produced
 * layout unchanged.
 */
interface TextDrawingPolicy {

    /**
     * Measures [text] with [textMeasurer] within the given [constraints] and
     * returns the resulting layout. [units] gives access to Android dp/sp in pixels,
     * so the implementation can scale its logic by the current density.
     */
    fun measure(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource,
    ): TextLayoutResult
}

