package me.nov.towerpower.ui


import javax.swing.*
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

class KeepAspectRatioPanel extends JPanel {

  KeepAspectRatioPanel(JPanel innerPanel) {
    setLayout(new GridBagLayout())
    addComponentListener(new ComponentAdapter() {
      @Override
      void componentResized(ComponentEvent e) {
        int w = getWidth()
        int h = getHeight()
        int size = Math.min(w, h)
        innerPanel.setPreferredSize(new Dimension(size, size))
        revalidate()
      }
    })
    add(innerPanel)
  }
}
