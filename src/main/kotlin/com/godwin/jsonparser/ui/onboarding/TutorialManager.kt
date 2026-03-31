package com.godwin.jsonparser.ui.onboarding

import javax.swing.JComponent
import javax.swing.JLayeredPane
import javax.swing.JRootPane
import javax.swing.SwingUtilities

/**
 * Orchestrates the tutorial overlay + dialog lifecycle.
 *
 * Usage:
 *   TutorialManager.start(steps, rootComponent)
 */
object TutorialManager {

    private var overlay: TutorialOverlay? = null
    private var dialog: TutorialDialog? = null
    private var steps: List<TutorialStep> = emptyList()
    private var currentIndex = 0
    private var rootPane: JRootPane? = null

    fun start(steps: List<TutorialStep>, root: JComponent) {
        if (steps.isEmpty()) return
        this.steps = steps
        this.currentIndex = 0

        val rp = SwingUtilities.getRootPane(root) ?: return
        this.rootPane = rp

        val layered = rp.layeredPane

        val ov = TutorialOverlay()
        ov.setBounds(0, 0, layered.width, layered.height)
        this.overlay = ov

        val dlg = TutorialDialog(
            onPrev = { navigate(-1) },
            onNext = { navigate(1) },
            onDone = { stop() },
            onSkip = { stop() }
        )
        this.dialog = dlg

        layered.add(ov, JLayeredPane.MODAL_LAYER)
        layered.add(dlg, JLayeredPane.POPUP_LAYER)

        showStep(currentIndex)
    }

    private fun navigate(delta: Int) {
        val next = currentIndex + delta
        if (next in steps.indices) {
            currentIndex = next
            showStep(currentIndex)
        }
    }

    private fun showStep(index: Int) {
        val step = steps[index]
        val ov = overlay ?: return
        val dlg = dialog ?: return
        val layered = rootPane?.layeredPane ?: return

        // Resize overlay to fill layered pane
        ov.setBounds(0, 0, layered.width, layered.height)

        ov.targetComponent = step.targetComponent
        ov.arrowSide = step.arrowSide

        dlg.update(step, index, steps.size)
        dlg.positionNear(step.targetComponent, ov, step.arrowSide)

        ov.repaint()
        dlg.repaint()
    }

    fun stop() {
        val layered = rootPane?.layeredPane ?: return
        overlay?.let { layered.remove(it) }
        dialog?.let { layered.remove(it) }
        layered.repaint()
        overlay = null
        dialog = null
        rootPane = null
    }

    fun isRunning() = overlay != null
}
