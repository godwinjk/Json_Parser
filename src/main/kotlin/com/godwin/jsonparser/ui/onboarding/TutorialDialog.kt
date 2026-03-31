package com.godwin.jsonparser.ui.onboarding

import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * Floating tutorial card showing GIF, title, description and navigation buttons.
 * Positioned relative to the target component.
 */
class TutorialDialog(
    private val onPrev: () -> Unit,
    private val onNext: () -> Unit,
    private val onDone: () -> Unit,
    private val onSkip: () -> Unit
) : JPanel(BorderLayout(0, 8)) {

    private val titleLabel = JLabel().apply {
        font = font.deriveFont(Font.BOLD, 14f)
    }
    private val descLabel = JLabel().apply {
        font = font.deriveFont(12f)
    }
    private val gifLabel = JLabel().apply {
        horizontalAlignment = SwingConstants.CENTER
        preferredSize = Dimension(500, 500)
    }
    private val stepLabel = JLabel().apply {
        font = font.deriveFont(11f)
        foreground = Color.GRAY
    }
    private val prevBtn = JButton("← Prev")
    private val nextBtn = JButton("Next →")
    private val doneBtn = JButton("Done ✓").apply {
        foreground = Color(0, 120, 0)
        font = font.deriveFont(Font.BOLD)
    }
    private val skipBtn = JButton("Skip tour").apply {
        font = font.deriveFont(10f)
        foreground = Color.GRAY
        isBorderPainted = false
        isContentAreaFilled = false
    }

    init {
        isOpaque = true
        background = UIManager.getColor("Panel.background") ?: Color(50, 50, 50)
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color(255, 200, 0), 2, true),
            EmptyBorder(12, 16, 12, 16)
        )
        preferredSize = Dimension(360, 320)

        add(buildHeader(), BorderLayout.NORTH)
        add(gifLabel, BorderLayout.CENTER)
        add(buildFooter(), BorderLayout.SOUTH)

        prevBtn.addActionListener { onPrev() }
        nextBtn.addActionListener { onNext() }
        doneBtn.addActionListener { onDone() }
        skipBtn.addActionListener { onSkip() }
    }

    private fun buildHeader() = JPanel(BorderLayout(0, 4)).apply {
        isOpaque = false
        add(titleLabel, BorderLayout.NORTH)
        add(descLabel, BorderLayout.CENTER)
    }

    private fun buildFooter() = JPanel(BorderLayout()).apply {
        isOpaque = false
        val navPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 4, 0)).apply {
            isOpaque = false
            add(prevBtn)
            add(nextBtn)
            add(doneBtn)
        }
        add(stepLabel, BorderLayout.WEST)
        add(navPanel, BorderLayout.CENTER)
        add(skipBtn, BorderLayout.EAST)
    }

    fun update(step: TutorialStep, index: Int, total: Int) {
        titleLabel.text = step.title
        descLabel.text = "<html><body style='width:300px'>${step.description}</body></html>"
        stepLabel.text = "${index + 1} / $total"

        // Load GIF
        val url = TutorialDialog::class.java.getResource(step.gifResource)
        if (url != null) {
            val icon = ImageIcon(url)
            gifLabel.icon = icon
            gifLabel.text = null
        } else {
            gifLabel.icon = null
            gifLabel.text = "<html><center><i>GIF: ${step.gifResource}</i></center></html>"
        }

        prevBtn.isEnabled = index > 0
        nextBtn.isVisible = index < total - 1
        doneBtn.isVisible = index == total - 1

        revalidate()
        repaint()
    }

    /** Position this dialog near the target component, avoiding screen edges */
    fun positionNear(target: JComponent?, overlay: TutorialOverlay, side: ArrowSide) {
        val overlaySize = overlay.size
        val dialogSize = preferredSize

        if (target == null) {
            // Center on screen
            setBounds(
                (overlaySize.width - dialogSize.width) / 2,
                (overlaySize.height - dialogSize.height) / 2,
                dialogSize.width, dialogSize.height
            )
        } else {
            val loc = SwingUtilities.convertPoint(target, 0, 0, overlay)
            val margin = 20

            var x: Int
            var y: Int

            when (side) {
                ArrowSide.BOTTOM -> {
                    x = loc.x + target.width / 2 - dialogSize.width / 2
                    y = loc.y - dialogSize.height - margin
                }
                ArrowSide.TOP -> {
                    x = loc.x + target.width / 2 - dialogSize.width / 2
                    y = loc.y + target.height + margin
                }
                ArrowSide.LEFT -> {
                    x = loc.x + target.width + margin
                    y = loc.y + target.height / 2 - dialogSize.height / 2
                }
                ArrowSide.RIGHT -> {
                    x = loc.x - dialogSize.width - margin
                    y = loc.y + target.height / 2 - dialogSize.height / 2
                }
            }

            // Clamp to overlay bounds
            x = x.coerceIn(8, overlaySize.width - dialogSize.width - 8)
            y = y.coerceIn(8, overlaySize.height - dialogSize.height - 8)

            setBounds(x, y, dialogSize.width, dialogSize.height)
        }

        // Tell overlay where dialog is so it can draw the arrow
        overlay.dialogBounds = bounds
    }
}
