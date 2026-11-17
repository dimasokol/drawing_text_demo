package ru.dimasokol.demo.drawingtext.policies.estimation

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.utils.boundingBoxCoverage

/**
 * Scores a layout by the share of the result's own bounding box covered by its line
 * rectangles; layouts that overflow the area score 0. Higher coverage is better.
 */
class LocalCoverageEstimator : Estimator {

    override fun estimate(
        layoutResult: TextLayoutResult,
        area: Constraints
    ): Float {
        if (layoutResult.hasVisualOverflow) return 0f
        return layoutResult.boundingBoxCoverage()
    }

}