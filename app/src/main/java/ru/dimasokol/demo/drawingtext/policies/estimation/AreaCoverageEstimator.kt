package ru.dimasokol.demo.drawingtext.policies.estimation

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.utils.coverage

/**
 * Scores a layout by the share of the drawing area covered by its line rectangles; layouts
 * that overflow the area score 0. Higher coverage is better.
 */
class AreaCoverageEstimator : Estimator {

    override fun estimate(
        layoutResult: TextLayoutResult,
        area: Constraints
    ): Float {
        if (layoutResult.hasVisualOverflow) return 0f
        return layoutResult.coverage(area.maxWidth, area.maxHeight)
    }

}