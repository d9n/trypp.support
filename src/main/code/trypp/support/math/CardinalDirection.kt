package trypp.support.math


import trypp.support.memory.Pool
import java.util.Random

/**
 * Like [CompassDirection] but with only 4 directions.
 */
enum class CardinalDirection {
    E,
    N,
    W,
    S;

    val angle: Angle
        get() = ANGLES[ordinal]

    fun faces(angle: Angle): Boolean {
        return getForAngle(angle) == this
    }

    companion object {
        private val CACHED = values()
        // Regions that correspond with each 45° section of the circle
        private val REGIONS = Array(CACHED.size * 2, { E })
        private val ANGLES = Array(CACHED.size, { Angle.ofDegrees(it * 90f) })

        private val random = Random()

        init {
            REGIONS[0] = E;
            REGIONS[1] = N;
            REGIONS[2] = N;
            REGIONS[3] = W;
            REGIONS[4] = W;
            REGIONS[5] = S;
            REGIONS[6] = S;
            REGIONS[7] = E;
        }

        fun getRandom(): CardinalDirection {
            return CACHED[random.nextInt(CACHED.size)]
        }

        fun getForAngle(angle: Angle): CardinalDirection {
            return REGIONS[(angle.getDegrees() / 45f).toInt()]
        }
    }
}
