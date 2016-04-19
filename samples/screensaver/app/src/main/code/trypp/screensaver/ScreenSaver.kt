package trypp.screensaver

import javafx.application.Application
import javafx.beans.InvalidationListener
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.stage.Stage

class ScreenSaver : Application() {
    init {
        // TODO: Move to hook.json
        G.hook.points.create(Renderer::class, DefaultRenderer::class)
    }

    class ResizableCanvas : Canvas {
        constructor() {
            widthProperty().addListener(InvalidationListener { refresh() })
            heightProperty().addListener(InvalidationListener { refresh() })
        }

        private fun refresh() {
            graphicsContext2D.clearRect(0.0, 0.0, width, height);
            Renderer.get().renderTo(this)
        }

        override fun isResizable(): Boolean {
            return true
        }

        override fun prefWidth(height: Double): Double {
            return width
        }

        override fun prefHeight(width: Double): Double {
            return height
        }
    }

    override fun start(stage: Stage) {
        stage.title = "\"Screen saver\" Demo"

        val root = Group();
        val s = Scene(root, 100.0, 100.0, Color.BLACK);

        val canvas = ResizableCanvas();
        canvas.widthProperty().bind(s.widthProperty())
        canvas.heightProperty().bind(s.heightProperty())

        val gc = canvas.graphicsContext2D;

        root.children.add(canvas);
        stage.scene = s
        stage.show()
    }
}