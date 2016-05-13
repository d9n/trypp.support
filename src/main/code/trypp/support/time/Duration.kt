package trypp.support.time

import trypp.support.memory.Poolable

/**
 * An class which represents a time duration.
 */
class Duration
/**
 * Don't construct directly. Use [ofSeconds], [ofMinutes], [ofMilliseconds], or [of]
 * instead.
 */
internal constructor() : Poolable {

    private var milliseconds = 0f

    fun getMilliseconds(): Float {
        return milliseconds
    }

    fun setMilliseconds(milliseconds: Float): Duration {
        this.milliseconds = if (milliseconds > 0f) milliseconds else 0f
        return this
    }

    fun getSeconds(): Float {
        return milliseconds / 1000f
    }

    fun setSeconds(seconds: Float): Duration {
        setMilliseconds(seconds * 1000f)
        return this
    }

    fun getMinutes(): Float {
        return getSeconds() / 60f
    }

    fun setMinutes(minutes: Float): Duration {
        setSeconds(minutes * 60f)
        return this
    }

    fun setFrom(duration: Duration): Duration {
        milliseconds = duration.milliseconds
        return this
    }

    fun addMilliseconds(milliseconds: Float): Duration {
        setMilliseconds(getMilliseconds() + milliseconds)
        return this
    }

    fun addSeconds(secs: Float): Duration {
        setSeconds(getSeconds() + secs)
        return this
    }

    fun addMinutes(minutes: Float): Duration {
        setMinutes(getMinutes() + minutes)
        return this
    }

    fun add(duration: Duration): Duration {
        setMilliseconds(getMilliseconds() + duration.getMilliseconds())
        return this
    }

    fun subtractMilliseconds(milliseconds: Float): Duration {
        setMilliseconds(getMilliseconds() - milliseconds)
        return this
    }

    fun subtractSeconds(secs: Float): Duration {
        setSeconds(getSeconds() - secs)
        return this
    }

    fun subtractMinutes(minutes: Float): Duration {
        setMinutes(getMinutes() - minutes)
        return this
    }

    fun subtract(duration: Duration): Duration {
        setMilliseconds(getMilliseconds() - duration.getMilliseconds())
        return this
    }

    fun setZero(): Duration {
        setMilliseconds(0f)
        return this
    }

    val isZero: Boolean
        get() = milliseconds == 0f

    /**
     * Overridden from [Poolable]. Prefer using [setZero] instead for readability.
     */
    override fun reset() {
        setZero()
    }

    override fun toString(): String {
        return "${getSeconds()}s"
    }

    companion object {
        fun zero(): Duration {
            return Duration()
        }

        fun ofSeconds(secs: Float): Duration {
            val duration = Duration()
            duration.setSeconds(secs)
            return duration
        }

        fun ofMinutes(minutes: Float): Duration {
            val duration = Duration()
            duration.setMinutes(minutes)
            return duration
        }

        fun ofMilliseconds(milliseconds: Float): Duration {
            val duration = Duration()
            duration.setMilliseconds(milliseconds)
            return duration
        }

        fun of(duration: Duration): Duration {
            val clonsedDuration = Duration()
            clonsedDuration.setFrom(duration)
            return clonsedDuration
        }
    }
}
