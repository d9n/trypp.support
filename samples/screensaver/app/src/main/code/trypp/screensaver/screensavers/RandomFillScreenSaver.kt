package trypp.screensaver.screensavers

import javafx.scene.paint.Color
import trypp.screensaver.Canvas
import trypp.screensaver.Canvas.Pos
import trypp.screensaver.ScreenSaver
import java.util.*

class RandomFillScreenSaver : ScreenSaver {
    private val r = Random()

    private var durationMs = 0L
    private var elapsedMs = 0L
    private var percent = 0f
    private var from = Color.BLACK
    private var target = Color.BLACK

    override val name: String
        get() = "Fill"

    override fun start(canvas: Canvas) {
        canvas.clear()
        durationMs = 0L
        elapsedMs = 0L
        percent = 0f
        from = Color.BLACK
        target = Color.BLACK
        update(0)
    }

    override fun update(deltaTimeMs: Long) {
        elapsedMs += deltaTimeMs

        if (elapsedMs >= durationMs) {
            from = target
            target = Color.color(nextColor(), nextColor(), nextColor())
            durationMs = r.nextLong() % 1000L + 2000L // between 1/2 to 1 1/2 second
            elapsedMs = 0
            percent = 0f
        }
        else {
            percent = (elapsedMs.toFloat() / durationMs.toFloat())
        }
    }

    override fun renderTo(canvas: Canvas) {
        val curr = Color.color(lerp(from.red, target.red, percent),
            lerp(from.green, target.green, percent), lerp(from.blue, target.blue, percent))
        canvas.clear(curr)
    }

    private fun nextColor(): Double {
        return r.nextInt(256) / 255.0
    }

    private fun lerp(d1: Double, d2: Double, percent: Float): Double {

        return d1 + ((d2 - d1) * percent)
    }
}