package ru.dimasokol.demo.drawingtext.policies

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.estimation.Estimator
import ru.dimasokol.demo.drawingtext.policies.utils.UnitsSource
import ru.dimasokol.demo.drawingtext.policies.variation.Variator

/**
 * A [TextDrawingPolicy] that combines a [Variator] and an [Estimator]: it scans every layout
 * variation the variator produces and returns the one scored highest by the estimator.
 */
class TopScoreTextDrawingPolicy(
    private val variator: Variator,
    private val estimator: Estimator
) : TextDrawingPolicy {

    /**
     * Resets the [variator], then walks through all its variations, scoring each with the
     * [estimator] and returning the highest-scoring layout.
     */
    override fun measure(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource
    ): TextLayoutResult {
        variator.reset()

        var best: TextLayoutResult = variator.nextVariation(textMeasurer, text, style, constraints, units)
            ?: throw IllegalStateException("At least initial variation should exist")
        var bestScore = estimator.estimate(best, constraints)
        var next: TextLayoutResult = best

        while (true) {
            next = variator.nextVariation(textMeasurer, text, style, constraints, units) ?: break
            val score = estimator.estimate(next, constraints)
            if (score > bestScore) {
                bestScore = score
                best = next
            }
        }

        return best
    }

}