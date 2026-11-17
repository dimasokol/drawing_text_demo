package ru.dimasokol.demo.drawingtext

import androidx.annotation.StringRes
import ru.dimasokol.demo.drawingtext.policies.estimation.AreaCoverageEstimator
import ru.dimasokol.demo.drawingtext.policies.estimation.Estimator
import ru.dimasokol.demo.drawingtext.policies.estimation.FirstLinesDeviationEstimator
import ru.dimasokol.demo.drawingtext.policies.estimation.FullTextDeviationEstimator
import ru.dimasokol.demo.drawingtext.policies.estimation.LocalCoverageEstimator
import ru.dimasokol.demo.drawingtext.policies.variation.FontSizeVariator
import ru.dimasokol.demo.drawingtext.policies.variation.NoOpVariator
import ru.dimasokol.demo.drawingtext.policies.variation.ShrinkWidthVariator
import ru.dimasokol.demo.drawingtext.policies.variation.Variator

/**
 * Returns the resource id of the localized name shown for this [Variator] in the UI, mapping
 * each known implementation to its `variator_*_ui_name` string. Falls back to the error string
 * for an unrecognized type.
 */
@StringRes
fun Variator.uiName(): Int = when (this) {
    is NoOpVariator -> R.string.variator_noop_ui_name
    is ShrinkWidthVariator -> R.string.variator_downsize_ui_name
    is FontSizeVariator -> R.string.variator_font_ui_name
    else -> R.string.variator_error_ui
}

/**
 * Returns the resource id of the localized help text for this [Variator], used in the Info
 * dialog. Unknown implementations fall back to the error string.
 */
@StringRes
fun Variator.uiHelp(): Int = when (this) {
    is NoOpVariator -> R.string.variator_noop_ui_help
    is ShrinkWidthVariator -> R.string.variator_downsize_ui_help
    is FontSizeVariator -> R.string.variator_font_ui_help
    else -> R.string.variator_error_ui
}

/**
 * Returns the resource id of the localized name shown for this [Estimator] in the UI, mapping
 * each known implementation to its `estimator_*_ui_name` string. Unknown types fall back to the
 * error string.
 */
@StringRes
fun Estimator.uiName(): Int = when(this) {
    is AreaCoverageEstimator -> R.string.estimator_area_coverage_ui_name
    is LocalCoverageEstimator -> R.string.estimator_local_coverage_ui_name
    is FullTextDeviationEstimator -> R.string.estimator_full_deviation_ui_name
    is FirstLinesDeviationEstimator -> R.string.estimator_paragraph_deviation_ui_name
    else -> R.string.estimator_error_ui
}

/**
 * Returns the resource id of the localized help text for this [Estimator], used in the Info
 * dialog. Unknown implementations fall back to the error string.
 */
@StringRes
fun Estimator.uiHelp(): Int = when(this) {
    is AreaCoverageEstimator -> R.string.estimator_area_coverage_ui_help
    is LocalCoverageEstimator -> R.string.estimator_local_coverage_ui_help
    is FullTextDeviationEstimator -> R.string.estimator_full_deviation_ui_help
    is FirstLinesDeviationEstimator -> R.string.estimator_paragraph_deviation_ui_help
    else -> R.string.estimator_error_ui
}