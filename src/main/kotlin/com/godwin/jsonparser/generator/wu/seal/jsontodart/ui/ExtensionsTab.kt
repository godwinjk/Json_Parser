package wu.seal.jsontodart.ui

import com.intellij.ui.layout.panel
import com.intellij.util.ui.JBDimension
import extensions.ExtensionsCollector
import java.awt.FlowLayout
import java.awt.LayoutManager
import javax.swing.JPanel

class ExtensionsTab(layout: LayoutManager?, isDoubleBuffered: Boolean) : JPanel(layout, isDoubleBuffered) {

    constructor(isDoubleBuffered: Boolean) : this(FlowLayout(), isDoubleBuffered)

    init {

        add(panel {
            ExtensionsCollector.extensions.forEach {
                row(separated = false) {
                    it.createUI().apply {
                        preferredSize = JBDimension(500, 0)
                    }.invoke()
                }
            }
        }.apply {
            minimumSize = JBDimension(500, 300)
            preferredSize = JBDimension(500, 300)
        })
    }

}
