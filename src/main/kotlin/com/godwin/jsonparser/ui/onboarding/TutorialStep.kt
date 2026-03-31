package com.godwin.jsonparser.ui.onboarding

import javax.swing.JComponent

data class TutorialStep(
    val id: String,
    val title: String,
    val description: String,
    val gifResource: String,                          // path in resources e.g. "/tutorials/add_tab.gif"
    val targetComponent: JComponent? = null,          // component to highlight/point at
    val arrowSide: ArrowSide = ArrowSide.BOTTOM       // which side of the target the arrow comes from
)

enum class ArrowSide { TOP, BOTTOM, LEFT, RIGHT }
