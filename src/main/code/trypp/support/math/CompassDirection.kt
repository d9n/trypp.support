package trypp.support.math

import java.util.Random

/**
 * Enumeration for compass directions and various utility methods.
 */
enum class CompassDirection {
    E,
    NE,
    N,
    NW,
    W,
    SW,
    S,
    SE;

    val angle: Angle
        get() = ANGLES[ordinal]

    fun faces(angle: Angle): Boolean {
        return getForAngle(angle) == this
    }

    companion object {

        private val CACHED = values()
        // Regions that correspond with each 22.5° section of the circle
        private val REGIONS = Array(CACHED.size * 2, { E })
        private val ANGLES = Array(CACHED.size, { Angle.ofDegrees(it * 45f) })

        private val random = Random()

        init {
            REGIONS[0] = E
            REGIONS[1] = NE
            REGIONS[2] = NE
            REGIONS[3] = N
            REGIONS[4] = N
            REGIONS[5] = NW
            REGIONS[6] = NW
            REGIONS[7] = W
            REGIONS[8] = W
            REGIONS[9] = SW
            REGIONS[10] = SW
            REGIONS[11] = S
            REGIONS[12] = S
            REGIONS[13] = SE
            REGIONS[14] = SE
            REGIONS[15] = E
        }

        fun getRandom(): CompassDirection {
            return CACHED[random.nextInt(CACHED.size)]
        }

        fun getForAngle(angle: Angle): CompassDirection {
            return REGIONS[(angle.getDegrees() / 22.5f).toInt()]
        }
    }
}
