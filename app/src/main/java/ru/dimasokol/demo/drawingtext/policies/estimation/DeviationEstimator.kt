package ru.dimasokol.demo.drawingtext.policies.estimation

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.utils.interiorLineFills
import ru.dimasokol.demo.drawingtext.policies.utils.meanStdDev

/**
 * Base estimator that scores a layout by the standard deviation of its line fills, as
 * `mean − stddev`: higher means lines that are both dense and uniform in length.
 *
 * @param fullText when `true`, every line is scored; when `false`, the last line of each
 *   paragraph is excluded, so its typically-short final line counts as a length outlier.
 */
abstract class DeviationEstimator(
    private val fullText: Boolean
) : Estimator {

    override fun estimate(
        layoutResult: TextLayoutResult,
        area: Constraints
    ): Float {
        val width = layoutResult.size.width
        val fills = layoutResult.interiorLineFills(
            layoutResult.layoutInput.text.text,
            width,
            fullText.not()
        )
        if (fills != null) {
            val (mean, stdDev) = fills.meanStdDev()
            val score = mean - stdDev
            return score
        }

        return 0f
    }
}