package me.nov.towerpower.listener

import javax.swing.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class ExitListener extends WindowAdapter {
  private JFrame frame

  ExitListener(JFrame frame) {
    this.frame = frame
  }

  @Override
  void windowClosing(WindowEvent we) {
    if (JOptionPane
            .showConfirmDialog(frame, "Do you really want to exit?", "Confirm", JOptionPane.YES_NO_OPTION) ==
            JOptionPane.YES_OPTION) {
      Runtime.getRuntime().exit(0)
    }
  }
}
