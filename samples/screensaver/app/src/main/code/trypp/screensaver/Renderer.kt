package trypp.screensaver

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

interface Renderer {
    companion object {
        fun get(): Renderer {
            return G.hook.points[Renderer::class]
        }
    }

    fun renderTo(c: Canvas)
}

class DefaultRenderer : Renderer {
    override fun renderTo(c: Canvas) {
        val gc = c.graphicsContext2D
        gc.stroke = Color.RED;
        gc.strokeLine(0.0, 0.0, c.width, c.height);
        gc.strokeLine(0.0, c.height, c.width, 0.0);
    }
}