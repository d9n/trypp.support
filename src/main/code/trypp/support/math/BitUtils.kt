package trypp.support.math

object BitUtils {
    fun requireSingleBit(value: Int) {
        assert(hasSingleBit(value), { "Passed in value should only have a single bit set" })
    }

    /**
     * Quick check to see if the passed in value only has a single bit set.
     */
    fun hasSingleBit(value: Int): Boolean {
        // See http://stackoverflow.com/a/12483864
        return value != 0 && value and value - 1 == 0
    }

    fun getBitIndex(value: Int): Int {
        requireSingleBit(value)
        for (i in 0..31) {
            if (value and (1 shl i) != 0) {
                return i
            }
        }
        throw IllegalArgumentException("Unexpected value $value passed into getBitIndex")
    }
}