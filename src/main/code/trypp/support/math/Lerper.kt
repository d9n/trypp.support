package trypp.support.math

import trypp.support.memory.Poolable
import trypp.support.time.Duration

/**
 * Class that handles interpolating between two values over some duration.
 */
class Lerper() : Poolable {

    constructor(lerp: Lerp, duration: Duration): this() {
        set(lerp, duration)
    }

    private var function = Lerp.LINEAR
    private val duration = Duration.zero()
    private val elapsed = Duration.zero()
    private var flipDirection = false
    val finished: Boolean
        get() = elapsed == duration

    override fun reset() {
        duration.setZero()
        elapsed.setZero()
        function = Lerp.LINEAR
        flipDirection = false
    }

    fun set(function: Lerp, duration: Duration) {
        this.function = function
        this.duration.setFrom(duration)
        elapsed.setZero()
    }

    fun update(elapsed: Duration) {
        this.elapsed.add(elapsed)
        if (this.elapsed.getSeconds() > duration.getSeconds()) {
            this.elapsed.setFrom(duration)
        }
    }

    fun lerp(from: Float, to: Float): Float {
        var percent = 1f
        if (!duration.isZero) {
            percent = elapsed.getSeconds() / duration.getSeconds()
        }

        val value = clamp(function.apply(percent), 0f, 1f)

        var valA = from
        var valB = to
        if (flipDirection) {
            valA = to
            valB = from
        }

        return valA + (value * (valB - valA))
    }
}