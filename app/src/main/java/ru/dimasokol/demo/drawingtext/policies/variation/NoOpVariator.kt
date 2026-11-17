package ru.dimasokol.demo.drawingtext.policies.variation

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.utils.UnitsSource

/**
 * A [Variator] that never produces variations — it only yields the unchanged, initial layout.
 * Combined with an `estimator`, it behaves like the plain default drawing.
 */
class NoOpVariator : Variator() {

    override fun findNext(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource
    ): TextLayoutResult? = null

}