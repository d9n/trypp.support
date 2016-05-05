package trypp.screensaver

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import trypp.screensaver.screensavers.RandomFillScreenSaver
import trypp.screensaver.screensavers.RandomPixelScreenSaver
import kotlin.properties.Delegates
import javafx.scene.canvas.Canvas as FxCanvas

/**
 * JavaFX Application which lets the user choose from a list of screensavers to watch.
 */
class ScreenSaverApplication : Application() {
    private var active: ScreenSaver
    private var canvas: CanvasImpl by Delegates.notNull()

    init {
        G.extensions.create(ScreenSaver::class)
        G.extensions.add(ScreenSaver::class, RandomPixelScreenSaver::class)
        G.extensions.add(ScreenSaver::class, RandomFillScreenSaver::class)
        active = G.extensions[ScreenSaver::class].first()
    }

    class ResizableCanvas : FxCanvas() {
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

    private fun createMenu(): MenuBar {
        val menuBar = MenuBar()
        val menuFile = Menu("File")
        val menuRestart = MenuItem("Restart")
        menuRestart.onAction = EventHandler { active.start(canvas) }
        menuFile.items.add(menuRestart)
        val menuItemExit = MenuItem("Exit")
        menuItemExit.onAction = EventHandler { System.exit(0) }
        menuFile.items.add(menuItemExit)

        val menuSavers = Menu("Savers")
        ScreenSaver.getAll().forEach { saver ->
            val menuSaver = MenuItem(saver.name)
            menuSaver.onAction = EventHandler {
                active = saver
                saver.start(canvas)
            }
            menuSavers.items.add(menuSaver)
        }
        menuBar.menus.addAll(menuFile, menuSavers)
        return menuBar
    }


    override fun start(stage: Stage) {
        stage.title = "\"Screen saver\" Demo"

        val root = Group();
        val s = Scene(root, 640.0, 480.0, Color.BLACK);

        val menuBar = createMenu()

        val fxCanvas = ResizableCanvas();
        fxCanvas.widthProperty().bind(s.widthProperty())
        fxCanvas.heightProperty().bind(s.heightProperty())

        canvas = CanvasImpl(fxCanvas)
        var now = System.currentTimeMillis()
        var before = now
        var elapsed: Long
        val updater = Timeline(KeyFrame(Duration.ONE, EventHandler {
            now = System.currentTimeMillis()
            elapsed = now - before
            before = now
            active.update(elapsed)
        }))
        val fps = Duration(1000.0 / 60.0)
        val renderer = Timeline(KeyFrame(fps, EventHandler {
            active.renderTo(canvas)
        }))
        updater.cycleCount = Animation.INDEFINITE
        renderer.cycleCount = Animation.INDEFINITE
        active.start(canvas)
        updater.play()
        renderer.play()

        val vbox = VBox()
        vbox.children.add(menuBar)
        vbox.children.add(fxCanvas)
        root.children.add(vbox);
        stage.scene = s
        stage.show()
    }
}