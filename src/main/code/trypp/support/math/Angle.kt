package trypp.support.math

import trypp.support.math.Angle.Companion.ofDegrees
import trypp.support.math.Angle.Companion.ofRadians
import trypp.support.memory.Poolable
import trypp.support.opt.OptFloat


/**
 * Simple class that represents a 0 -> 360° angle. You can set and get this angle's value in either degreesOpt or
 * radiansOpt.
 */
class Angle
/**
 * Use [ofDegrees] or [ofRadians] instead.
 */
internal constructor() : Poolable {

    // One or both of these values are guaranteed to be set at any time. When one value is set, the other invalidated,
    // but when a request is made to get an unset value, it will lazily be calculated at that time.
    private val degreesOpt = OptFloat.of(0f)
    private val radiansOpt = OptFloat.of(0f)

    fun getDegrees(): Float {
        if (!degreesOpt.hasValue()) {
            setDegrees(radiansOpt.getValue() * RAD_TO_DEG)
        }
        return degreesOpt.getValue()
    }

    fun setDegrees(degrees: Float): Angle {
        var boundedDegrees = degrees % FULL_REVOLUTION_DEG
        while (boundedDegrees < 0f) {
            boundedDegrees += FULL_REVOLUTION_DEG
        }

        degreesOpt.set(boundedDegrees)
        radiansOpt.clear()

        return this
    }

    fun getRadians(): Float {
        if (!radiansOpt.hasValue()) {
            setRadians(degreesOpt.getValue() * DEG_TO_RAD)
        }
        return radiansOpt.getValue()
    }

    fun setRadians(radians: Float): Angle {
        var boundedRadians = radians % FULL_REVOLUTION_RAD
        while (boundedRadians < 0f) {
            boundedRadians += FULL_REVOLUTION_RAD
        }

        radiansOpt.set(boundedRadians)
        degreesOpt.clear()

        return this
    }

    fun setFrom(rhs: Angle): Angle {
        degreesOpt.setFrom(rhs.degreesOpt)
        radiansOpt.setFrom(rhs.radiansOpt)

        return this
    }

    fun addDegrees(degrees: Float): Angle {
        setDegrees(getDegrees() + degrees)
        return this
    }

    fun addRadians(radians: Float): Angle {
        setRadians(getRadians() + radians)
        return this
    }

    fun add(rhs: Angle): Angle {
        addDegrees(rhs.getDegrees())
        return this
    }

    fun subDegrees(degrees: Float): Angle {
        setDegrees(getDegrees() - degrees)
        return this
    }

    fun subRadians(radians: Float): Angle {
        setRadians(getRadians() - radians)
        return this
    }

    fun sub(rhs: Angle): Angle {
        subDegrees(rhs.getDegrees())
        return this
    }

    override fun toString(): String {
        return if (degreesOpt.hasValue()) "${getDegrees()}°"
            else "%.2f π".format(getRadians() / Angle.PI)
    }

    override fun reset() {
        degreesOpt.set(0f)
        radiansOpt.set(0f)
    }

    companion object {

        /**
         * A float version of java.lang.Math.PI
         */
        val PI = Math.PI.toFloat()

        /**
         * Convenience constant for π/2
         */
        val HALF_PI = PI / 2f

        /**
         * Convenience constant for π/4
         */
        val QUARTER_PI = PI / 4f

        /**
         * Convenience constant for 2π
         */
        val TWO_PI = PI * 2f

        /**
         * Multiplying this to a value in degrees converts it to radians.
         */
        val RAD_TO_DEG = 180f / PI

        /**
         * Multiplying this to a value in radians converts it to degrees.
         */
        val DEG_TO_RAD = PI / 180f

        private val FULL_REVOLUTION_RAD = 2 * PI
        private val FULL_REVOLUTION_DEG = 360f

        fun ofDegrees(degrees: Float): Angle {
            val angle = Angle()
            angle.setDegrees(degrees)
            return angle
        }

        fun ofRadians(radians: Float): Angle {
            val angle = Angle()
            angle.setRadians(radians)
            return angle
        }

        fun of(otherAngle: Angle): Angle {
            val angle = Angle()
            angle.setFrom(otherAngle)
            return angle
        }
    }
}
