package ru.dimasokol.demo.drawingtext.policies.variation

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import ru.dimasokol.demo.drawingtext.policies.utils.UnitsSource

/**
 * A strategy that iterates over layout variations of the same text, one layout per
 * [nextVariation] call, until it has nothing more to offer (returns `null`). The first call
 * always yields a layout — the [initialVariation] when [findNext] produces nothing on the
 * first pass.
 *
 * Implementations are stateful: reset them with [reset] before reusing for a new scan.
 */
abstract class Variator {

    private var passCount = 0
    private val isInitialPass: Boolean
        get() = passCount == 0

    /**
     * Returns the next layout variation, or `null` when no further variations are produced.
     * The very first call always returns at least one layout (the unchanged initial one).
     * Throws if an implementation keeps producing variations without terminating, guarding
     * against a dead loop.
     */
    fun nextVariation(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource
    ): TextLayoutResult? {
        // Pass count guard
        if (passCount > MAX_PASS_COUNT)
            throw IllegalStateException("Dead loop in ${this::class.simpleName}, max pass reached")

        var result = findNext(textMeasurer, text, style, constraints, units)

        if (result == null && isInitialPass) {
            result = initialVariation(textMeasurer, text, style, constraints)
        }

        passCount++
        return result
    }

    /** Resets the iteration state so this [Variator] can be reused for a fresh scan. */
    open fun reset() {
        passCount = 0
    }

    /**
     * Produces the next variation, or `null` when the scan is exhausted. Called by
     * [nextVariation]; concrete implementations decide when to stop (e.g. on overflow or once
     * a bound is reached).
     */
    protected abstract fun findNext(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints,
        units: UnitsSource
    ): TextLayoutResult?

    /**
     * The baseline layout: the text measured with the given [style] against [constraints],
     * unchanged. Used as the first variation when [findNext] yields nothing on the first pass.
     */
    protected fun initialVariation(
        textMeasurer: TextMeasurer,
        text: String,
        style: TextStyle,
        constraints: Constraints
    ): TextLayoutResult = textMeasurer.measure(
        text = text,
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
        constraints = constraints,
    )

    private companion object {
        const val MAX_PASS_COUNT = 10000
    }
}