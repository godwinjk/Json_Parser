package com.godwin.jsonparser.ui.tutorial

import java.awt.Component
import java.awt.Container
import javax.swing.JComponent
import javax.swing.Timer

object TutorialManager {

    private var tutorialWindow: TutorialWindow? = null
    private var steps: List<TutorialStep> = emptyList()
    private var currentIndex = 0


    fun start(steps: List<TutorialStep>) {
        if (steps.isEmpty()) return
        this.steps = steps
        this.currentIndex = 0

        Timer(500) {
            val win = TutorialWindow(
                onPrev = { navigate(-1) },
                onNext = { navigate(1) },
                onDone = { stop() },
                onSkip = { stop() }
            )
            tutorialWindow = win
            showStep(0)
            win.show()
        }.apply { isRepeats = false; start() }
    }

    private fun navigate(delta: Int) {
        val next = currentIndex + delta
        if (next in steps.indices) {
            currentIndex = next; showStep(currentIndex)
        }
    }

    private fun showStep(index: Int) {
        val step = steps[index]
        tutorialWindow?.update(step, index, steps.size)
    }

    fun stop() {
        tutorialWindow?.hide()
        tutorialWindow = null
    }

    fun findComponentByHint(root: Component, hint: String): JComponent? {
        if (root is JComponent) {
            val tip = root.toolTipText ?: ""
            val name = root.accessibleContext?.accessibleName ?: ""
            if (tip.contains(hint, ignoreCase = true) || name.contains(hint, ignoreCase = true)) return root
        }
        if (root is Container) {
            for (child in root.components) {
                val found = findComponentByHint(child, hint)
                if (found != null) return found
            }
        }
        return null
    }
}
