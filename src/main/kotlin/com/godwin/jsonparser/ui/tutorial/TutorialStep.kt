package com.godwin.jsonparser.ui.tutorial

import javax.swing.JComponent

data class TutorialStep(
    val id: String,
    val title: String,
    val description: String,
    val gifResource: String,
    val targetComponent: JComponent? = null,
    val arrowSide: ArrowSide = ArrowSide.BOTTOM
)

enum class ArrowSide { TOP, BOTTOM, LEFT, RIGHT }
