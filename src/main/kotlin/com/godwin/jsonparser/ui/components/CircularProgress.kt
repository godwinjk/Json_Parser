package com.godwin.jsonparser.ui.components

import java.awt.*
import java.awt.geom.Arc2D
import javax.swing.JComponent
import javax.swing.Timer

class CircularProgress(
    private val strokeWidth: Int = 2,
    private val trackColor: Color = Color(220, 220, 220),
    private val progressColor: Color = Color(30, 144, 255),
    private val cometTail: Boolean = true
) : JComponent() {

    private var angle = 0
    private val timer: Timer

    init {
        preferredSize = Dimension(100, 100)

        // Rotate 10° every 20ms → smooth spin
        timer = Timer(20) {
            angle = (angle - 10) % 360
            repaint()
        }
        timer.start()
    }

    fun stop() = timer.stop()
    fun start() {
        if (!timer.isRunning)
            timer.start()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g.create() as Graphics2D

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val size = minOf(width, height) - strokeWidth * 2
        val x = (width - size) / 2
        val y = (height - size) / 2

        // Background track
        g2.stroke = BasicStroke(strokeWidth.toFloat(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.color = trackColor
        g2.drawOval(x, y, size, size)

        if (cometTail) {
            // Comet-tail effect: multiple arcs with increasing opacity
            val arcLength = 90
            val segments = 10

            for (i in 0 until segments) {
                val alpha = ((i + 1).toFloat() / segments * 255).toInt()
                g2.color = Color(
                    progressColor.red,
                    progressColor.green,
                    progressColor.blue,
                    alpha
                )
                val segAngle = angle + (i * arcLength / segments)
                g2.draw(
                    Arc2D.Float(
                        x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat(),
                        segAngle.toFloat(), (arcLength / segments).toFloat(), Arc2D.OPEN
                    )
                )
            }
        } else {
            // Simple solid spinning arc
            g2.color = progressColor
            g2.draw(
                Arc2D.Float(
                    x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat(),
                    angle.toFloat(), 90f, Arc2D.OPEN
                )
            )
        }

        g2.dispose()
    }
}