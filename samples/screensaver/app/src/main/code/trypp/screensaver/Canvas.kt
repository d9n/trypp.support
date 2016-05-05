package trypp.screensaver

import javafx.scene.canvas.Canvas as FxCanvas
import javafx.scene.paint.Color
import trypp.screensaver.Canvas.Pos
import trypp.screensaver.Canvas.Size

interface Canvas {
    data class Pos(val x: Int, val y: Int)
    data class Size(val w: Int, val h: Int)
    val size: Size

    fun clear(color: Color = Color.BLACK)
    fun drawPixel(pt: Pos, color: Color)
    fun drawLine(pt1: Pos, pt2: Pos, color: Color)
    fun drawRect(pt1: Pos, pt2: Pos, color: Color)
    fun fillRect(pt1: Pos, pt2: Pos, color: Color)
    fun outlineRect(pt1: Pos, pt2: Pos, colorFill: Color, colorBorder: Color) {
        fillRect(pt1, pt2, colorFill)
        drawRect(pt1, pt2, colorBorder)
    }
}

internal class CanvasImpl(val fxCanvas: FxCanvas) : Canvas {
    override val size: Size
        get() = Size(fxCanvas.width.toInt(), fxCanvas.height.toInt())

    override fun clear(color: Color) {
        val gc = fxCanvas.graphicsContext2D
        gc.fill = color
        gc.fillRect(0.0, 0.0, fxCanvas.width, fxCanvas.height)
    }

    override fun drawPixel(pt: Pos, color: Color) {
        val gc = fxCanvas.graphicsContext2D
        gc.pixelWriter.setColor(pt.x, pt.y, color)
    }

    override fun drawLine(pt1: Pos, pt2: Pos, color: Color) {
        val gc = fxCanvas.graphicsContext2D
        gc.stroke = color
        gc.strokeLine(pt1.x.toDouble(), pt1.y.toDouble(), pt2.x.toDouble(), pt2.y.toDouble())
    }

    override fun drawRect(pt1: Pos, pt2: Pos, color: Color) {
        val gc = fxCanvas.graphicsContext2D
        gc.stroke = color
        gc.strokeRect(pt1.x.toDouble(), pt1.y.toDouble(), pt2.x.toDouble(), pt2.y.toDouble())
    }

    override fun fillRect(pt1: Pos, pt2: Pos, color: Color) {
        val gc = fxCanvas.graphicsContext2D
        gc.fill = color
        gc.fillRect(pt1.x.toDouble(), pt1.y.toDouble(), pt2.x.toDouble(), pt2.y.toDouble())
    }
}