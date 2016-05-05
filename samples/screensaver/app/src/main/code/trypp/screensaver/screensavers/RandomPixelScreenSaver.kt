package trypp.screensaver.screensavers

import javafx.scene.paint.Color
import trypp.screensaver.Canvas
import trypp.screensaver.Canvas.Pos
import trypp.screensaver.ScreenSaver
import java.util.*

class RandomPixelScreenSaver : ScreenSaver {
    private val NUM_PIXELS_PER_MS = 1000
    private val r = Random()
    private var count: Long = 0

    override val name: String
        get() = "Static"

    override fun start(canvas: Canvas) {
        canvas.clear()
    }

    override fun update(deltaTimeMs: Long) {
        count += (deltaTimeMs * NUM_PIXELS_PER_MS)
    }

    override fun renderTo(canvas: Canvas) {
        val size = canvas.size
        while (count > 0) {
            canvas.drawPixel(Pos(r.nextInt(size.w), r.nextInt(size.h)),
                Color.color(nextColor(), nextColor(), nextColor()))
            --count
        }
    }

    private fun nextColor() : Double {
        return r.nextInt(256) / 255.0
    }
}