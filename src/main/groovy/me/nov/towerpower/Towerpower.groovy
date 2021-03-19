package me.nov.towerpower

import com.github.weisj.darklaf.settings.ThemeSettings
import me.nov.towerpower.laf.DarkLookAndFeel
import me.nov.towerpower.listener.ExitListener
import me.nov.towerpower.ui.PaintPanel

import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.util.function.IntConsumer

class Towerpower extends JFrame {
  private static RATIO = 1d / 1d
  static PaintPanel paintPanel
  static JLabel infoLabel

  Towerpower() {
    this.initBounds()
    this.setTitle("Towerpower")
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    this.addWindowListener(new ExitListener(this))
    this.initializeFrame()
    this.initializeMenu()
  }

  static void main(String[] args) {
    DarkLookAndFeel.setLookAndFeel()
    new Towerpower().setVisible(true)
  }

  private void initializeMenu() {
    JMenuBar bar = new JMenuBar()
    JMenu file = new JMenu("File")
    JMenuItem save = new JMenuItem("Export as PNG")
    save.addActionListener {
      JFileChooser jfc = new JFileChooser()
      jfc.setAcceptAllFileFilterUsed(false)
      jfc.setDialogTitle("Export as PNG")
      jfc.setFileFilter(new FileNameExtensionFilter("PNG file (*.png)", "png"))
      int result = jfc.showSaveDialog(this)
      if (result == JFileChooser.APPROVE_OPTION) {
        File output = jfc.getSelectedFile()
        if (!output.getAbsolutePath().endsWith(".png"))
          output = new File(output.getAbsolutePath() + ".png")
        def img = paintPanel.currentImg
        Graphics2D g2d = img.createGraphics()
        g2d.setPaint(Color.black)
        g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12))
        FontMetrics fm = g2d.getFontMetrics()
        String watermark = "Graphed by towerpower"
        g2d.drawString(watermark, 6, 4 + fm.getHeight())
        g2d.dispose()
        ImageIO.write(img, "png", output)
      }
    }
    save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK))
    file.add(save)
    bar.add(file)

    JMenu settings = new JMenu("Settings")
    JMenu maxRes = new JMenu("Resolution doubling amount")
    maxRes.add(generateSpinner(paintPanel._resDoublingAmount, 2, 10, { paintPanel._resDoublingAmount = it }))
    settings.add(maxRes)

    JMenu minColDif = new JMenu("Min color difference for refresh")
    minColDif.add(generateSpinner(paintPanel._minPixelRefreshDif, 2, 256, { paintPanel._minPixelRefreshDif = it }))
    settings.add(minColDif)

    JMenu modFlip = new JMenu("Color sensitivity")
    modFlip.add(generateSpinner((int) paintPanel._colorSensitivity, 1, 1000, { paintPanel._colorSensitivity = (double) it }))
    settings.add(modFlip)


    JMenu accuracy = new JMenu("Tower accuracy")
    accuracy.add(generateSpinner(paintPanel._towerAccuracy, 1, 14, { paintPanel._towerAccuracy = it }))
    settings.add(accuracy)


    JMenu green = new JMenu("Green color calculation")
    ButtonGroup gGroup = new ButtonGroup()
    ActionListener acl = { ActionEvent e ->
      paintPanel._greenCalcType = gGroup.findIndexOf {it == e.getSource() }
      paintPanel.recalc()
    }
    def zero = new JRadioButtonMenuItem("No green")
    zero.addActionListener(acl)
    def minus = new JRadioButtonMenuItem("Real - imaginary")
    minus.addActionListener(acl)
    def dist = new JRadioButtonMenuItem("Distance to starting point", true)
    dist.addActionListener(acl)
    gGroup.add(zero)
    gGroup.add(minus)
    gGroup.add(dist)

    green.add(zero)
    green.add(minus)
    green.add(dist)

    settings.add(green)

    JMenu colMode = new JMenu("Color mode")
    ButtonGroup cGroup = new ButtonGroup()
    ActionListener colAl = { ActionEvent e ->
      paintPanel._sinus = cGroup.findIndexOf {it == e.getSource() } == 1
      paintPanel.recalc()
    }
    def modulo = new JRadioButtonMenuItem("Modulo", true)
    modulo.addActionListener(colAl)
    def sinus = new JRadioButtonMenuItem("Sinus")
    sinus.addActionListener(colAl)
    cGroup.add(modulo)
    cGroup.add(sinus)

    colMode.add(modulo)
    colMode.add(sinus)
    settings.add(colMode)
    JMenu towerMode = new JMenu("Tower equation mode")
    ButtonGroup tGroup = new ButtonGroup()
    ActionListener towerAl = { ActionEvent e ->
      paintPanel._towerMode = tGroup.findIndexOf {it == e.getSource() }
      paintPanel.recalc()
    }
    def normal = new JRadioButtonMenuItem("Normal", true)
    normal.addActionListener(towerAl)
    tGroup.add(normal)
    towerMode.add(normal)
    def keep = new JRadioButtonMenuItem("Keep base")
    keep.addActionListener(towerAl)
    tGroup.add(keep)
    towerMode.add(keep)
    def exp = new JRadioButtonMenuItem("Exp")
    exp.addActionListener(towerAl)
    tGroup.add(exp)
    towerMode.add(exp)
    def keepAndExp = new JRadioButtonMenuItem("Keep base + exp")
    keepAndExp.addActionListener(towerAl)
    tGroup.add(keepAndExp)
    towerMode.add(keepAndExp)
    def sin = new JRadioButtonMenuItem("Sin")
    sin.addActionListener(towerAl)
    tGroup.add(sin)
    towerMode.add(sin)
    def keepAndSin = new JRadioButtonMenuItem("Keep base + sin")
    keepAndSin.addActionListener(towerAl)
    tGroup.add(keepAndSin)
    towerMode.add(keepAndSin)

    settings.add(towerMode)

    JCheckBoxMenuItem grid = new JCheckBoxMenuItem("Show grid")
    grid.addActionListener {
      paintPanel._grid = grid.isSelected()
      paintPanel.repaint()
    }
    settings.add(grid)
    bar.add(settings)

    JMenu help = new JMenu("Help")
    JMenuItem laf = new JMenuItem("Look and feel settings")
    laf.setIcon(ThemeSettings.getIcon())
    laf.addActionListener { ThemeSettings.showSettingsDialog(this) }
    JMenuItem about = new JMenuItem("About")
    about.addActionListener {
      JOptionPane.showMessageDialog(this,
              "<html>Towerpower displays a set of complex numbers, graphed with their value for<br>" +
                      "x to the power of x to the power of x ... until infinity, also called tetration escape.<br>" +
                      "Black means the equation explodes for the complex number.<br><br>" +
                      "<b>Made by GraxCode 2021",
              "About", JOptionPane.INFORMATION_MESSAGE)
    }
    help.add(about)
    help.add(laf)
    bar.add(help)
    this.setJMenuBar(bar)
  }

  private static JSpinner generateSpinner(int defa, int min, int max, IntConsumer changeListener) {
    JSpinner sp = new JSpinner(new SpinnerNumberModel(defa, min, max, 1))
    sp.addChangeListener {
      paintPanel.recalc()
      changeListener.accept(sp.getValue() as int)
    }
    return sp
  }


  private void initializeFrame() {
    JPanel content = new JPanel(new BorderLayout())

    content.add(paintPanel = new PaintPanel(), BorderLayout.CENTER)
    content.add(infoLabel = new JLabel("", SwingConstants.RIGHT), BorderLayout.PAGE_END)
    setContentPane(content)
  }

  private void initBounds() {
    Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()
    int w = screenSize.width as int
    int h = screenSize.height as int
    int height = h / 2 as int
    int width = (height * RATIO) as int
    setBounds(w / 2 - width / 2 as int, h / 2 - height / 2 as int, width, height)
    setMinimumSize(new Dimension(width / 1.25 as int, height / 1.25 as int))
  }
}
