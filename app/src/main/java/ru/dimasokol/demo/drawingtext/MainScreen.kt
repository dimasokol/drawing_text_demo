package ru.dimasokol.demo.drawingtext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import ru.dimasokol.demo.drawingtext.policies.TopScoreTextDrawingPolicy
import ru.dimasokol.demo.drawingtext.policies.estimation.AreaCoverageEstimator
import ru.dimasokol.demo.drawingtext.policies.estimation.Estimator
import ru.dimasokol.demo.drawingtext.policies.estimation.FirstLinesDeviationEstimator
import ru.dimasokol.demo.drawingtext.policies.estimation.FullTextDeviationEstimator
import ru.dimasokol.demo.drawingtext.policies.estimation.LocalCoverageEstimator
import ru.dimasokol.demo.drawingtext.policies.utils.DensityUnitsSource
import ru.dimasokol.demo.drawingtext.policies.variation.FontSizeVariator
import ru.dimasokol.demo.drawingtext.policies.variation.NoOpVariator
import ru.dimasokol.demo.drawingtext.policies.variation.ShrinkWidthVariator
import ru.dimasokol.demo.drawingtext.policies.variation.Variator
import ru.dimasokol.demo.drawingtext.ui.theme.DrawingTextTheme

private val DrawingAreaWidth = 280.dp
private val DrawingAreaHeight = 240.dp

/**
 * The demo screen: type text (or pick a preset), choose a [Variator] and an [Estimator] and an
 * optional enlargement, then press Draw to lay the text out into the drawing area and see its
 * computed bounds. Press Info for a help dialog about the selected variator and estimator.
 *
 * @param modifier applied to the screen root.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val defaultText = stringResource(R.string.default_text)
    var input by rememberSaveable { mutableStateOf(defaultText) }
    var drawnText by rememberSaveable { mutableStateOf("") }
    var enlargedText by rememberSaveable { mutableStateOf(false) }
    var drawnEnlarged by rememberSaveable { mutableStateOf(false) }
    val variators = remember {
        listOf(
            NoOpVariator(),
            ShrinkWidthVariator(),
            FontSizeVariator()
        )
    }
    val estimators = remember {
        listOf(
            AreaCoverageEstimator(),
            LocalCoverageEstimator(),
            FullTextDeviationEstimator(),
            FirstLinesDeviationEstimator()
        )
    }
    var selectedVariatorIndex by rememberSaveable { mutableStateOf(0) }
    var selectedEstimatorIndex by rememberSaveable { mutableStateOf(0) }
    var drawnVariatorIndex by rememberSaveable { mutableStateOf(0) }
    var drawnEstimatorIndex by rememberSaveable { mutableStateOf(0) }
    var variatorMenuExpanded by remember { mutableStateOf(false) }
    var estimatorMenuExpanded by remember { mutableStateOf(false) }
    var showPolicyInfo by remember { mutableStateOf(false) }
    var showPresetsSheet by remember { mutableStateOf(false) }
    val presetTexts = stringArrayResource(R.array.preset_texts)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.main_screen_title)) })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(stringResource(R.string.text_to_draw_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )
            OutlinedButton(
                onClick = { showPresetsSheet = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.presets_button))
            }
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                SegmentedButton(
                    selected = !enlargedText,
                    onClick = { enlargedText = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                ) {
                    Text(stringResource(R.string.text_size_normal))
                }
                SegmentedButton(
                    selected = enlargedText,
                    onClick = { enlargedText = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                ) {
                    Text(stringResource(R.string.text_size_large))
                }
            }

            ExposedDropdownMenuBox(
                expanded = variatorMenuExpanded,
                onExpandedChange = { variatorMenuExpanded = !variatorMenuExpanded },
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextField(
                    value = stringResource(variators[selectedVariatorIndex].uiName()),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.variator_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = variatorMenuExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = variatorMenuExpanded,
                    onDismissRequest = { variatorMenuExpanded = false },
                ) {
                    variators.forEachIndexed { index, variator ->
                        DropdownMenuItem(
                            text = { Text(stringResource(variator.uiName())) },
                            onClick = {
                                selectedVariatorIndex = index
                                variatorMenuExpanded = false
                            },
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = estimatorMenuExpanded,
                onExpandedChange = { estimatorMenuExpanded = !estimatorMenuExpanded },
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextField(
                    value = stringResource(estimators[selectedEstimatorIndex].uiName()),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.estimator_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = estimatorMenuExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = estimatorMenuExpanded,
                    onDismissRequest = { estimatorMenuExpanded = false },
                ) {
                    estimators.forEachIndexed { index, estimator ->
                        DropdownMenuItem(
                            text = { Text(stringResource(estimator.uiName())) },
                            onClick = {
                                selectedEstimatorIndex = index
                                estimatorMenuExpanded = false
                            },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        drawnText = input
                        drawnEnlarged = enlargedText
                        drawnVariatorIndex = selectedVariatorIndex
                        drawnEstimatorIndex = selectedEstimatorIndex
                    },
                    enabled = input.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.draw_button))
                }
                OutlinedButton(
                    onClick = { showPolicyInfo = true },
                ) {
                    Text(stringResource(R.string.info_button))
                }
            }
            if (showPolicyInfo) {
                val selectedVariator = variators[selectedVariatorIndex]
                val selectedEstimator = estimators[selectedEstimatorIndex]
                AlertDialog(
                    onDismissRequest = { showPolicyInfo = false },
                    title = { Text(stringResource(R.string.info_dialog_title)) },
                    text = {
                        Text(text =
                            stringResource(
                                R.string.help_format,
                                selectedVariator::class.simpleName!!,
                                stringResource(selectedVariator.uiHelp()),
                                selectedEstimator::class.simpleName!!,
                                stringResource(selectedEstimator.uiHelp())
                                )
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showPolicyInfo = false }) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                )
            }
            if (showPresetsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPresetsSheet = false },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        presetTexts.forEach { preset ->
                            TextButton(
                                onClick = {
                                    input = preset
                                    showPresetsSheet = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = preset,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
            DrawingArea(
                text = drawnText,
                variator = variators[drawnVariatorIndex],
                estimator = estimators[drawnEstimatorIndex],
                enlargedText = drawnEnlarged
            )
        }
    }
}

/**
 * Draws [text] directly into the canvas, without a [Text] composable. The text is laid out
 * through [TopScoreTextDrawingPolicy] driven by the selected [variator] and scored by
 * [estimator]; the bounding box of the chosen layout is highlighted before the text is drawn.
 *
 * @param text the text to draw.
 * @param variator produces the layout variations (e.g. shrinking the width, changing font size).
 * @param estimator scores each produced layout, deciding which one is considered the result.
 * @param enlargedText whether to draw the text enlarged (1.2x font).
 * @param modifier applied to the drawing box.
 */
@Composable
private fun DrawingArea(
    text: String,
    variator: Variator,
    estimator: Estimator,
    enlargedText: Boolean,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val baseBodyLarge = MaterialTheme.typography.bodyLarge
    val textStyle = baseBodyLarge.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = if (enlargedText) baseBodyLarge.fontSize * 1.2f else baseBodyLarge.fontSize,
    )
    val background = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.inversePrimary
    val shape = RoundedCornerShape(2.dp)

    val combinedPolicy = TopScoreTextDrawingPolicy(variator, estimator)

    Box(
        modifier = modifier
            .width(DrawingAreaWidth)
            .height(DrawingAreaHeight)
            .clip(shape)
            .background(background)
            .padding(0.dp)
            .drawWithContent {
                drawContent()
                if (text.isEmpty()) return@drawWithContent

                val layout = combinedPolicy.measure(
                    textMeasurer = textMeasurer,
                    text = text,
                    style = textStyle,
                    constraints = Constraints(
                        maxWidth = size.width.toInt(),
                        maxHeight = size.height.toInt(),
                    ),
                    units = DensityUnitsSource(this),
                )
                drawRect(highlight, size = layout.size.toSize())
                drawText(layout, topLeft = Offset.Zero)
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    DrawingTextTheme {
        MainScreen()
    }
}
