package me.nov.towerpower.ui

import me.nov.towerpower.Towerpower
import org.apache.commons.math3.complex.Complex

import javax.swing.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage

class PaintPanel extends JPanel {


  Complex start = new Complex(-20, -20)
  Complex end = new Complex(20, 20)

  int resolution
  BufferedImage currentImg

  Thread calcHandler
  Thread[] resolutionRunners = []

  Point enterPoint, exitPoint

  Stack<Complex[]> history = new Stack<>()

  int _minPixelRefreshDif = 15
  double _colorSensitivity = 50
  int _resDoublingAmount = 6
  int _towerAccuracy = 8
  int _greenCalcType = 2
  boolean _sinus = false
  boolean _grid = false
  int _towerMode = 0
  boolean _keepAspectRatio = true

  PaintPanel() {
    this.addMouseListener(new MouseAdapter() {
      @Override
      void mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent))
          return
        enterPoint = ((JPanel) mouseEvent.getSource()).getMousePosition()
      }

      @Override
      void mouseReleased(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent) || exitPoint == null || enterPoint.distance(exitPoint) == 0)
          return
        history.push([start, end] as Complex[])

        Point min = new Point(Math.min(enterPoint.x, exitPoint.x) as int, Math.min(enterPoint.y, exitPoint.y) as int)
        Point max = new Point(Math.max(enterPoint.x, exitPoint.x) as int, Math.max(enterPoint.y, exitPoint.y) as int)
        def oldStart = start
        double horP = min.x / (double) width
        double verP = min.y / (double) height
        start = new Complex(oldStart.real + (end.real - oldStart.real) * horP, oldStart.imaginary + (end.imaginary - oldStart.imaginary) * verP)
        horP = max.x / (double) width
        verP = max.y / (double) height

        end = new Complex(oldStart.real + (end.real - oldStart.real) * horP, oldStart.imaginary + (end.imaginary - oldStart.imaginary) * verP)
        if(_keepAspectRatio) {
          // ensure no floating point errors take overhand
          end = new Complex(end.imaginary - start.imaginary + start.real, end.imaginary)
        }
        def dist = start.subtract(end)
        if(Math.abs(dist.real) < 1E-14 || Math.abs(dist.imaginary) < 1E-14) {
          def old = history.pop()
          start = old[0]
          end = old[1]
          JOptionPane.showMessageDialog(PaintPanel.this, "Zoom exceeds 64-bit precision. Undo-ing.")
        }
        exitPoint = enterPoint = null
        recalc()
      }

      @Override
      void mouseClicked(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
          if (history.isEmpty())
            return
          def old = history.pop()
          start = old[0]
          end = old[1]
        }
        recalc()
      }
    })
    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      void mouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent))
          return
        def mp = ((JPanel) mouseEvent.getSource()).getMousePosition()
        if (mp != null) {
          exitPoint = mp
          if(_keepAspectRatio) {
            def ar = (end.real - start.real) / (end.imaginary - start.imaginary)
            exitPoint.y = (int) ((exitPoint.x - enterPoint.x) / ar + enterPoint.y)
          }
        }
        repaint()
      }

      @Override
      void mouseMoved(MouseEvent mouseEvent) {
        def mp = ((JPanel) mouseEvent.getSource()).getMousePosition()
        if (mp == null) {
          Towerpower.infoLabel.setText(" ")
          return
        }
        double horP = mp.x / (double) width
        double verP = mp.y / (double) height
        def real = start.real + (end.real - start.real) * horP
        def imag = start.imaginary + (end.imaginary - start.imaginary) * verP
        Towerpower.infoLabel.setText("(" + real + "; " + imag + "i) ")
      }
    })
    Thread.startDaemon {
      while (true) {
        if (calcHandler != null && calcHandler.isAlive())
          repaint()
        Thread.sleep(50)
      }
    }
    recalc()
  }

  @Override
  void paint(Graphics g) {
    super.paint(g)
    Graphics2D g2 = g as Graphics2D
    //g2.setColor(Color.white)
    //g2.fillRect(0, 0, width, height)
    if (currentImg != null)
      g2.drawImage(currentImg, 0, 0, width, height, null)
    g2.setColor(Color.black)
    if (enterPoint != null && exitPoint != null) {
      Point min = new Point(Math.min(enterPoint.x, exitPoint.x) as int, Math.min(enterPoint.y, exitPoint.y) as int)
      Point max = new Point(Math.max(enterPoint.x, exitPoint.x) as int, Math.max(enterPoint.y, exitPoint.y) as int)
      g2.drawRect(min.x as int, min.y as int, max.x - min.x as int, max.y - min.y as int)
    }
    if (_grid) {

      def lY = (0 - start.imaginary / (end.imaginary - start.imaginary)) * height as int
      g2.drawLine(0, lY, width, lY)

      def lX = (0 - start.real / (end.real - start.real)) * width as int
      g2.drawLine(lX, 0, lX, height)

      g2.setColor(Color.magenta)
      lX = ((Math.pow(Math.E, -Math.E) - start.real) / (end.real - start.real)) * width as int
      g2.drawLine(lX, 0, lX, height)
      g2.setColor(Color.cyan)
      lX = ((Math.pow(Math.E, 1d / Math.E) - start.real) / (end.real - start.real)) * width as int
      g2.drawLine(lX, 0, lX, height)
    }
  }


  def void recalc() {
    // stop old calculation and recalc
    resolutionRunners.findAll { it.isAlive() }.each { it.stop() }
    if (calcHandler != null && calcHandler.isAlive())
      calcHandler.stop()

    calcHandler = Thread.start {
      calcZoom()
    }
  }

  def calcZoom() {
    resolutionRunners = []
    resolution = 16
    currentImg = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB)
    // initial pass
    for (x in 0..resolution - 1)
      for (y in 0..resolution - 1)
        currentImg.setRGB(x, y, findPointColor(x, y))

    for (iter in 0.._resDoublingAmount - 1) {
      resolution *= 2
      def oldImg = currentImg
      currentImg = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB)

      Graphics g = currentImg.createGraphics()
      g.drawImage(oldImg, 0, 0, resolution, resolution, null)
      g.dispose()


      def full = 0..(resolution - 1)
      def q1 = full.findAll { it < full.getTo() / 2 }
      def q2 = full.findAll { it > full.getTo() / 2 }
      resolutionRunners += Thread.start {
        for (x in q1)
          for (y in q1)
            convolutionalRedraw(x, y)
      }
      resolutionRunners += Thread.start {
        for (x in q2)
          for (y in q1)
            convolutionalRedraw(x, y)
      }
      resolutionRunners += Thread.start {
        for (x in q1)
          for (y in q2)
            convolutionalRedraw(x, y)
      }
      resolutionRunners += Thread.start {
        for (x in q2)
          for (y in q2)
            convolutionalRedraw(x, y)
      }

      resolutionRunners.each { it.join() }
    }
    calcHandler = null
  }

  private void convolutionalRedraw(int x, int y) {
    def col = currentImg.getRGB(x, y)
    boolean should = false

    boolean canGoLeft = x > 1
    boolean canGoDown = y > 1
    boolean canGoRight = x < resolution - 2
    boolean canGoUp = y < resolution - 2



    if (canGoLeft && colorDiff(currentImg.getRGB(x - 2, y), col) > _minPixelRefreshDif)
      should = true
    else if (canGoDown && colorDiff(currentImg.getRGB(x, y - 2), col) > _minPixelRefreshDif)
      should = true
    else if (canGoRight && colorDiff(currentImg.getRGB(x + 2, y), col) > _minPixelRefreshDif)
      should = true
    else if (canGoUp && colorDiff(currentImg.getRGB(x, y + 2), col) > _minPixelRefreshDif)
      should = true
    else if (canGoLeft && canGoDown && colorDiff(currentImg.getRGB(x - 2, y - 2), col) > _minPixelRefreshDif)
      should = true
    else if (canGoLeft && canGoUp && colorDiff(currentImg.getRGB(x - 2, y + 2), col) > _minPixelRefreshDif)
      should = true
    else if (canGoRight && canGoDown && colorDiff(currentImg.getRGB(x + 2, y - 2), col) > _minPixelRefreshDif)
      should = true
    else if (canGoRight && canGoUp && colorDiff(currentImg.getRGB(x + 2, y + 2), col) > _minPixelRefreshDif)
      should = true
    else
      return

    currentImg.setRGB(x, y, findPointColor(x, y))
  }

  private static float colorDiff(int a, int b) {
    int ar = a & 0xFF;
    int ag = (a >> 8) & 0xFF;
    int ab = (a >> 16) & 0xFF;
    int br = b & 0xFF;
    int bg = (b >> 8) & 0xFF;
    int bb = (b >> 16) & 0xFF;

    int dr = ar - br;
    int dg = ag - bg;
    int db = ab - bb;

    return dr * dr + dg * dg + db * db
  }

  private int findPointColor(int x, int y) {
    def orig = new Complex(start.real + (end.real - start.real) * (x / (float) resolution), start.imaginary + (end.imaginary - start.imaginary) * (y / (float) resolution))
    Complex point = orig
    def overflow = false
    def maxErr = 1 / (10**_towerAccuracy)

    for (i in 0..50) {
      Complex top
      switch (_towerMode) {
        case 1:
          top = point.pow(point)
          break
        case 2:
          top = orig.pow(point.exp())
          break
        case 3:
          top = point.pow(point.exp())
          break
        case 4:
          top = orig.pow(point.sin())
          break
        case 5:
          top = point.pow(point.sin())
          break
        default:
          top = orig.pow(point)
          break
      }

      Complex diff = top.subtract(point)

      if (Math.abs(diff.imaginary) + Math.abs(diff.real) < maxErr) {
        point = top
        break
      }
      if (Math.abs(top.real as double) >= 100 || Math.abs(top.imaginary as double) >= 100) {
        overflow = true
        break
      }
      point = top
    }
    return overflow ? 0 : getHue(orig, point)
  }

  int getHue(Complex orig, Complex point) {
    def real = point.real
    def imag = point.imaginary
    def green = 0
    switch (_greenCalcType) {
      case 0:
        break
      case 1:
        green = real - imag
        break
      case 2:
        def dif = orig.subtract(point)
        green = Math.sqrt(dif.real * dif.real + dif.imaginary * dif.imaginary)
        break
    }

    return (0xFF << 24) | (pendulum(real, _colorSensitivity) * 255 as int << 16) | (pendulum(green, _colorSensitivity) * 255 as int << 8) | (pendulum(imag, _colorSensitivity) * 255 as int)
  }


  def pendulum(double val, double pend) {
    if (_sinus)
      return (Math.sin(val * pend * Math.PI) + 1f) / 2f

    def m = 1 / pend
    return (((val % m) + m) % m) * pend // fuck JVM for not implementing modulo correctly
  }
}
