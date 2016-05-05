package trypp.screensaver

interface ScreenSaver {
    companion object {
        fun getAll(): Iterable<ScreenSaver> {
            return G.extensions[ScreenSaver::class];
        }
    }

    val name: String
    fun start(canvas: Canvas)
    fun update(deltaTimeMs: Long)
    fun renderTo(canvas: Canvas)
}

