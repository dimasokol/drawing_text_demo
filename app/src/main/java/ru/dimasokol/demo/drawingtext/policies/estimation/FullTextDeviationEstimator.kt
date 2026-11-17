package ru.dimasokol.demo.drawingtext.policies.estimation

/**
 * A [DeviationEstimator] that scores every line, including the last lines of paragraphs.
 */
class FullTextDeviationEstimator : DeviationEstimator(true)