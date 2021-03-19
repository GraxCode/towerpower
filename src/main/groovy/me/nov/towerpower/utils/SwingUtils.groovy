package me.nov.towerpower.utils

import com.github.weisj.darklaf.icons.IconLoader
import me.nov.towerpower.Towerpower

import javax.swing.*
import javax.swing.border.Border
import java.awt.*

class SwingUtils {

  private static final IconLoader ICON_LOADER = IconLoader.get(Towerpower.class)

  static JComponent pad(JComponent comp, int top, int left, int bottom, int right) {
    JPanel panel = new JPanel(new BorderLayout())
    panel.add(comp, BorderLayout.CENTER)
    if (top > 0) {
      JPanel p = new JPanel()
      p.setPreferredSize(new Dimension(0, top))
      panel.add(p, BorderLayout.NORTH)
    }
    if (bottom > 0) {
      JPanel p = new JPanel()
      p.setPreferredSize(new Dimension(0, bottom))
      panel.add(p, BorderLayout.SOUTH)
    }
    if (left > 0) {
      JPanel p = new JPanel()
      p.setPreferredSize(new Dimension(left, 0))
      panel.add(p, BorderLayout.WEST)
    }
    if (right > 0) {
      JPanel p = new JPanel()
      p.setPreferredSize(new Dimension(right, 0))
      panel.add(p, BorderLayout.EAST)
    }
    return panel
  }

  static JComponent horizontallyDivided(JComponent top, JComponent bottom) {
    JPanel content = new JPanel(new BorderLayout())
    JPanel topHolder = new JPanel(new BorderLayout())
    topHolder.add(top, BorderLayout.CENTER)
    topHolder.add(createHorizontalSeparator(8), BorderLayout.SOUTH)
    content.add(topHolder, BorderLayout.CENTER)
    content.add(bottom, BorderLayout.SOUTH)
    return content
  }

  static JComponent verticallyDivided(JComponent left, JComponent right) {
    JPanel content = new JPanel(new BorderLayout())
    JPanel leftHolder = new JPanel(new BorderLayout())
    leftHolder.add(left, BorderLayout.CENTER)
    leftHolder.add(createVerticalSeparator(8), BorderLayout.EAST)
    content.add(leftHolder, BorderLayout.CENTER)
    content.add(right, BorderLayout.EAST)
    return content
  }

  static JComponent alignBottom(JComponent component) {
    JPanel panel = new JPanel(new BorderLayout())
    panel.add(component, BorderLayout.SOUTH)
    return panel
  }

  static JComponent createHorizontalSeparator() {
    return createHorizontalSeparator(0)
  }

  static JComponent createHorizontalSeparator(int padding) {
    return withEmptyBorder(wrap(new JSeparator(JSeparator.HORIZONTAL)), padding, 0, padding, 0)
  }

  static JComponent createVerticalSeparator() {
    return createVerticalSeparator(0)
  }

  static JComponent createVerticalSeparator(int padding) {
    return withEmptyBorder(wrap(new JSeparator(JSeparator.VERTICAL)), 0, padding, 0, padding)
  }

  static <T extends JComponent> T withEmptyBorder(T comp, int pad) {
    return withEmptyBorder(comp, pad, pad, pad, pad)
  }

  static <T extends JComponent> T withEmptyBorder(T comp, int top, int left, int bottom, int right) {
    return withBorder(comp, BorderFactory.createEmptyBorder(top, left, bottom, right))
  }

  static <T extends JComponent> T withBorder(T comp, Border border) {
    comp.setBorder(border)
    return comp
  }

  static JComponent wrap(final JComponent component) {
    JPanel wrap = new JPanel(new BorderLayout())
    wrap.add(component)
    return wrap
  }

  static Image iconToFrameImage(Icon icon, Window window) {
    return IconLoader.createFrameIcon(icon, window)
  }

  static Icon getIcon(String path) {
    return getIcon(path, false)
  }

  static Icon getIcon(String path, boolean themed) {
    return ICON_LOADER.getIcon(path, themed)
  }

  static Icon getIcon(String path, int width, int height) {
    return ICON_LOADER.getIcon(path, width, height, false)
  }

  static Icon getIcon(String path, int width, int height, boolean themed) {
    return ICON_LOADER.getIcon(path, width, height, themed)
  }
}
