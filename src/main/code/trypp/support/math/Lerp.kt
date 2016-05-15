package trypp.support.math

/**
 * Convert an input value (between 0.0 and 1.0) to an output value (also between 0.0 and
 * 1.0) but not necessarily at the same rate.
 */
interface Lerp {
    companion object {
        /**
         * Run from 0 - 1 linearly
         */
        val LINEAR = object : Lerp {
            override fun apply(value: Float): Float { return value }
        }

        /**
         * Run from 0 - 1, decelerating as it reaches the end
         */
        val EASE_OUT = object : Lerp {
            override fun apply(value: Float): Float {
                return Math.sin((Angle.HALF_PI * value).toDouble()).toFloat() }
        }

        /**
         * Run from 0 - 1, accelerating as it reaches the end
         */
        val EASE_IN = object : Lerp {
            override fun apply(value: Float): Float {
                return 1f - Math.cos((Angle.HALF_PI * value).toDouble()).toFloat() }
        }

        /**
         * Take any lerp and have it run backwards instead.
         */
        fun reverse(lerp: Lerp): Lerp {
            return object : Lerp {
                override fun apply(value: Float): Float {
                    return lerp.apply(1.0f - value)
                }
            }
        }

        /**
         * Take any lerp and have it run from start to target and back to start again
         */
        fun bounce(lerp: Lerp): Lerp {
            return object : Lerp {
                override fun apply(value: Float): Float {
                    if (value <= 0.5f) {
                        return lerp.apply(value * 2f)
                    }
                    else {
                        return lerp.apply((1.0f - value) * 2f)
                    }
                }
            }
        }
    }

    /**
     * Convert an input value (between 0.0 and 1.0) to an output value (also between 0.0 and
     * 1.0).
     */
    fun apply(value: Float): Float
}


