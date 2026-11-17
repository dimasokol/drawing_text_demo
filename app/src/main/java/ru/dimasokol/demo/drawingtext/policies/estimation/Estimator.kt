package ru.dimasokol.demo.drawingtext.policies.estimation

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Constraints

/**
 * Scores a text layout so that the best variant produced by a [Variator][ru.dimasokol.demo.drawingtext.policies.variation.Variator]
 * can be selected. Higher values are considered better.
 */
interface Estimator {

    /**
     * Estimates the quality of [layoutResult] laid out within the [area] constraints, returning
     * a score where higher is better.
     */
    fun estimate(layoutResult: TextLayoutResult, area: Constraints): Float

}