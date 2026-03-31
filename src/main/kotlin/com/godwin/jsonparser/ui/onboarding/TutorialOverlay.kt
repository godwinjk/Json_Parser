package com.godwin.jsonparser.ui.onboarding

import java.awt.*
import java.awt.geom.GeneralPath
import java.awt.geom.RoundRectangle2D
import javax.swing.JComponent
import javax.swing.SwingUtilities

/**
 * Transparent glass pane that dims the screen and highlights the target component.
 * Draws a spotlight cutout around the target and an arrow pointing to it.
 */
class TutorialOverlay : JComponent() {

    var targetComponent: JComponent? = null
    var arrowSide: ArrowSide = ArrowSide.BOTTOM
    var dialogBounds: Rectangle? = null   // set by TutorialDialog so arrow knows where to start

    init {
        isOpaque = false
        cursor = Cursor.getDefaultCursor()
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val target = targetComponent
        val targetBounds = target?.let { getTargetBoundsInOverlay(it) }

        // Draw dim overlay with spotlight cutout
        val overlay = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)
        g2.composite = overlay
        g2.color = Color(0, 0, 0)

        if (targetBounds != null) {
            val padding = 8
            val spotlight = RoundRectangle2D.Float(
                (targetBounds.x - padding).toFloat(),
                (targetBounds.y - padding).toFloat(),
                (targetBounds.width + padding * 2).toFloat(),
                (targetBounds.height + padding * 2).toFloat(),
                12f, 12f
            )
            // Fill everything except the spotlight
            val area = java.awt.geom.Area(Rectangle(0, 0, width, height))
            area.subtract(java.awt.geom.Area(spotlight))
            g2.fill(area)

            // Draw spotlight border
            g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
            g2.color = Color(255, 200, 0)
            g2.stroke = BasicStroke(2.5f)
            g2.draw(spotlight)

            // Draw arrow from dialog to target
            drawArrow(g2, targetBounds)
        } else {
            g2.fillRect(0, 0, width, height)
        }
    }

    private fun getTargetBoundsInOverlay(target: JComponent): Rectangle {
        val loc = SwingUtilities.convertPoint(target, 0, 0, this)
        return Rectangle(loc.x, loc.y, target.width, target.height)
    }

    private fun drawArrow(g2: Graphics2D, targetBounds: Rectangle) {
        val dialog = dialogBounds ?: return
        val padding = 8

        // Arrow start: edge of dialog closest to target
        val startX: Int
        val startY: Int
        // Arrow end: edge of spotlight
        val endX: Int
        val endY: Int

        when (arrowSide) {
            ArrowSide.BOTTOM -> {
                startX = dialog.x + dialog.width / 2
                startY = dialog.y + dialog.height
                endX = targetBounds.x + targetBounds.width / 2
                endY = targetBounds.y - padding
            }
            ArrowSide.TOP -> {
                startX = dialog.x + dialog.width / 2
                startY = dialog.y
                endX = targetBounds.x + targetBounds.width / 2
                endY = targetBounds.y + targetBounds.height + padding
            }
            ArrowSide.LEFT -> {
                startX = dialog.x
                startY = dialog.y + dialog.height / 2
                endX = targetBounds.x + targetBounds.width + padding
                endY = targetBounds.y + targetBounds.height / 2
            }
            ArrowSide.RIGHT -> {
                startX = dialog.x + dialog.width
                startY = dialog.y + dialog.height / 2
                endX = targetBounds.x - padding
                endY = targetBounds.y + targetBounds.height / 2
            }
        }

        // Bezier curve arrow
        val cx1 = startX
        val cy1 = (startY + endY) / 2
        val cx2 = endX
        val cy2 = (startY + endY) / 2

        val path = GeneralPath()
        path.moveTo(startX.toFloat(), startY.toFloat())
        path.curveTo(cx1.toFloat(), cy1.toFloat(), cx2.toFloat(), cy2.toFloat(), endX.toFloat(), endY.toFloat())

        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        g2.color = Color(255, 200, 0)
        g2.stroke = BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.draw(path)

        // Arrowhead
        drawArrowHead(g2, endX, endY, arrowSide)
    }

    private fun drawArrowHead(g2: Graphics2D, x: Int, y: Int, side: ArrowSide) {
        val size = 10
        val head = GeneralPath()
        when (side) {
            ArrowSide.BOTTOM -> {
                head.moveTo(x.toFloat(), y.toFloat())
                head.lineTo((x - size).toFloat(), (y - size).toFloat())
                head.lineTo((x + size).toFloat(), (y - size).toFloat())
            }
            ArrowSide.TOP -> {
                head.moveTo(x.toFloat(), y.toFloat())
                head.lineTo((x - size).toFloat(), (y + size).toFloat())
                head.lineTo((x + size).toFloat(), (y + size).toFloat())
            }
            ArrowSide.LEFT -> {
                head.moveTo(x.toFloat(), y.toFloat())
                head.lineTo((x + size).toFloat(), (y - size).toFloat())
                head.lineTo((x + size).toFloat(), (y + size).toFloat())
            }
            ArrowSide.RIGHT -> {
                head.moveTo(x.toFloat(), y.toFloat())
                head.lineTo((x - size).toFloat(), (y - size).toFloat())
                head.lineTo((x - size).toFloat(), (y + size).toFloat())
            }
        }
        head.closePath()
        g2.fill(head)
    }
}
