package com.godwin.jsonparser.ui.tutorial

import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * Large centered tutorial window — always stays in screen center.
 * Shows GIF, title, description and navigation.
 */
class TutorialWindow(
    private val onPrev: () -> Unit,
    private val onNext: () -> Unit,
    private val onDone: () -> Unit,
    private val onSkip: () -> Unit
) {
    private val frame = JFrame("JSON Parser Tutorial").apply {
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        isResizable = true
        isAlwaysOnTop = true
        // Wire the X button to call onSkip
        addWindowListener(object : java.awt.event.WindowAdapter() {
            override fun windowClosing(e: java.awt.event.WindowEvent) { onSkip() }
        })
    }

    private val titleLabel = JLabel().apply {
        font = font.deriveFont(Font.BOLD, 16f)
        border = EmptyBorder(0, 0, 4, 0)
    }
    private val descLabel = JLabel().apply { font = font.deriveFont(13f) }
    private val gifLabel = JLabel().apply {
        horizontalAlignment = SwingConstants.CENTER
        preferredSize = Dimension(700, 394)
        minimumSize = Dimension(700, 394)
        border = BorderFactory.createLineBorder(Color(60, 60, 60), 1)
    }
    private val stepLabel = JLabel().apply {
        font = font.deriveFont(11f); foreground = Color.GRAY
    }
    private val prevBtn = JButton("← Prev")
    private val nextBtn = JButton("Next →")
    private val doneBtn = JButton("Done ✓").apply {
        foreground = Color(0, 140, 0); font = font.deriveFont(Font.BOLD)
    }
    private val skipBtn = JButton("Skip tour").apply {
        font = font.deriveFont(10f); foreground = Color.GRAY
        isBorderPainted = false; isContentAreaFilled = false
    }

    init {
        val content = JPanel(BorderLayout(0, 12)).apply {
            border = EmptyBorder(16, 20, 16, 20)

            // Header
            add(JPanel(BorderLayout(0, 6)).apply {
                isOpaque = false
                add(titleLabel, BorderLayout.NORTH)
                add(descLabel, BorderLayout.CENTER)
            }, BorderLayout.NORTH)

            // GIF
            add(gifLabel, BorderLayout.CENTER)

            // Footer
            add(JPanel(BorderLayout()).apply {
                isOpaque = false
                val nav = JPanel(FlowLayout(FlowLayout.RIGHT, 6, 0)).apply {
                    isOpaque = false; add(prevBtn); add(nextBtn); add(doneBtn)
                }
                add(stepLabel, BorderLayout.WEST)
                add(nav, BorderLayout.CENTER)
                add(skipBtn, BorderLayout.EAST)
            }, BorderLayout.SOUTH)
        }

        frame.contentPane = content
        frame.pack()

        prevBtn.addActionListener { onPrev() }
        nextBtn.addActionListener { onNext() }
        doneBtn.addActionListener { onDone() }
        skipBtn.addActionListener { onSkip() }
    }

    fun update(step: TutorialStep, index: Int, total: Int) {
        titleLabel.text = step.title
        descLabel.text = "<html><body style='width:680px'>${step.description}</body></html>"
        stepLabel.text = "${index + 1} / $total"

        val url = TutorialWindow::class.java.getResource(step.gifResource)
        if (url != null) {
            val raw = ImageIcon(url)
            val icon = if (raw.iconWidth > 700 || raw.iconHeight > 394) {
                ImageIcon(raw.image.getScaledInstance(700, 394, Image.SCALE_DEFAULT))
            } else raw
            gifLabel.icon = icon
            gifLabel.text = null
        } else {
            gifLabel.icon = null
            gifLabel.text = "<html><center><i>${step.gifResource}</i></center></html>"
        }

        prevBtn.isEnabled = index > 0
        nextBtn.isVisible = index < total - 1
        doneBtn.isVisible = index == total - 1
        frame.pack()
        centerOnScreen()
    }

    fun show() {
        centerOnScreen()
        frame.isVisible = true
    }

    fun hide() {
        frame.isVisible = false
        frame.dispose()
    }

    private fun centerOnScreen() {
        val screen = Toolkit.getDefaultToolkit().screenSize
        frame.setLocation(
            (screen.width - frame.width) / 2,
            (screen.height - frame.height) / 2
        )
    }
}
