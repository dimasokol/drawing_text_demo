package ru.dimasokol.demo.drawingtext.policies

/**
 * A [DeviationDrawingPolicy] variant that evaluates every line, including the last lines of
 * paragraphs — it does not ignore paragraph ends when scoring length uniformity.
 */
class FTDeviationDrawingPolicy : DeviationDrawingPolicy() {

    override val ignoreParagraphEnds: Boolean
        get() = false
}