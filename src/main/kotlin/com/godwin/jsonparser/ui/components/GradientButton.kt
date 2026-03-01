package com.godwin.jsonparser.ui.components

import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.JButton
import javax.swing.border.EmptyBorder

class GradientButton(text: String) : JButton(text) {
    init {
        isContentAreaFilled = false
        isBorderPainted = false
        isFocusPainted = false
        // Add padding so text doesn't hit the border
        border = EmptyBorder(4, 4, 4, 4)
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        preferredSize = Dimension(100, 20)
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val w = width.toFloat()
        val h = height.toFloat()
        val arc = 12f // Rounded corners

        // 1. Draw Subtle Background (Dark Gray/Black)
        g2.color = Color(25, 25, 26)
        g2.fill(RoundRectangle2D.Float(2f, 2f, w - 4f, h - 4f, arc, arc))

        // 2. Create the Static Gradient Border
        // Using a diagonal gradient for a more "dynamic" look
        val gradient = LinearGradientPaint(
            0f, 0f, w, h,
            floatArrayOf(0.0f, 1.0f),
            arrayOf(Color(0x4285F4), Color(0x9B72CB)) // Blue to Purple
        )

        g2.paint = gradient
        g2.stroke = BasicStroke(2.0f)
        g2.draw(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, arc, arc))

        // 3. Draw Text
        g2.color = Color.WHITE
        val metrics = g2.fontMetrics
        val textX = (width - metrics.stringWidth(text)) / 2
        val textY = (height - metrics.height) / 2 + metrics.ascent
        g2.drawString(text, textX, textY)
    }
}