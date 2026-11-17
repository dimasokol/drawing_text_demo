package ru.dimasokol.demo.drawingtext.policies.estimation

/**
 * A [DeviationEstimator] that ignores the (typically short) last line of every paragraph when
 * scoring, so paragraph-final lines do not count as length outliers.
 */
class FirstLinesDeviationEstimator : DeviationEstimator(false) {
}