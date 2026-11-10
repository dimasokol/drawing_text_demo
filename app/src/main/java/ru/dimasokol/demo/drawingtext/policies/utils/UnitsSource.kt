package ru.dimasokol.demo.drawingtext.policies.utils

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Provides Android's system units — dp and sp — converted to pixels, so drawing policies can
 * scale their logic by the current screen density and font scale instead of assuming
 * physical pixels.
 */
interface UnitsSource {

    /** The number of pixels in [dpValue] density-independent pixels. */
    fun dpToPx(dpValue: Float): Float

    /** The number of pixels in [spValue] scale-independent pixels. */
    fun spToPx(spValue: Float): Float
}

/** [UnitsSource] backed by a Compose [Density]. */
class DensityUnitsSource(private val density: Density) : UnitsSource {

    override fun dpToPx(dpValue: Float): Float = with(density) { dpValue.dp.toPx() }

    override fun spToPx(spValue: Float): Float = with(density) { spValue.sp.toPx() }
}