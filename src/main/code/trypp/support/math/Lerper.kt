package trypp.support.math

import trypp.support.memory.Poolable
import trypp.support.time.Duration

/**
 * Class that handles interpolating between two values with a configurable interpolation funciton.
 */
class Lerper() : Poolable {

    constructor(function: Lerper.Function, duration: Duration, mode: Mode = Mode.RUN_ONCE): this() {
        set(function, duration, mode)
    }

    enum class Mode {
        /**
         * Lerp from start to target, and then remain on the final value.
         */
        RUN_ONCE,

        /**
         * Lerp from start to target, then jump back to start again, and repeat
         */
        LOOP,

        /**
         * Lerp from start to target, then target to start, and repeat
         */
        BOUNCE
    }

    interface Function {
        /**
         * Convert an input value (between 0.0 and 1.0) to an output value (also between 0.0 and
         * 1.0).
         */
        fun apply(value: Float): Float
    }

    companion object {
        val LINEAR = object : Lerper.Function {
            override fun apply(value: Float): Float { return value }
        }

        val REVERSE = object : Lerper.Function {
            override fun apply(value: Float): Float { return 1f - value }
        }

        val EASE_OUT = object : Lerper.Function {
            override fun apply(value: Float): Float {
                return Math.sin((Angle.HALF_PI * value).toDouble()).toFloat() }
        }

        val EASE_IN = object : Lerper.Function {
            override fun apply(value: Float): Float {
                return 1f - Math.cos((Angle.HALF_PI * value).toDouble()).toFloat() }
        }
    }

    private var mode = Mode.RUN_ONCE
    private var function: Lerper.Function = LINEAR
    private val duration = Duration.zero()
    private val elapsed = Duration.zero()
    private var flipDirection = false

    override fun reset() {
        mode = Mode.RUN_ONCE
        duration.setZero()
        elapsed.setZero()
        function = LINEAR
        flipDirection = false
    }

    fun set(function: Lerper.Function, duration: Duration, mode: Mode = Mode.RUN_ONCE) {
        this.function = function
        this.duration.setFrom(duration)
        elapsed.setZero()
        this.mode = mode
    }

    fun update(elapsed: Duration) {
        this.elapsed.add(elapsed)
        if (this.elapsed.getSeconds() > duration.getSeconds()) {
            when (mode) {
                Mode.RUN_ONCE -> {
                    this.elapsed.setFrom(duration)
                }
                Mode.LOOP -> {
                    while (this.elapsed.getSeconds() > duration.getSeconds()) {
                        this.elapsed.subtract(duration)
                    }
                }
                Mode.BOUNCE -> {
                    while (this.elapsed.getSeconds() > duration.getSeconds()) {
                        flipDirection = !flipDirection
                        this.elapsed.subtract(duration)
                    }
                }
            }
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