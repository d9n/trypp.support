package trypp.support.math

fun log2(value: Int): Int {

    if (value < 0) {
        throw IllegalArgumentException("Log2 must take a value >= 1. Got: $value")
    }

    var valueCopy = value
    var log2 = 0
    while (valueCopy > 1) {
        valueCopy = valueCopy shr 1
        log2++
    }

    if (!isPowerOfTwo(value)) {
        log2++ // Round up to the power of two ceiling. For example, 4 -> 4, 5 -> 8, 8 -> 8, 9 -> 16, etc.
    }
    return log2
}

fun isPowerOfTwo(value: Int): Boolean {
    // See http://stackoverflow.com/a/19383296/1299302
    return value and value - 1 == 0
}

fun clamp(value: Int, min: Int, max: Int): Int {
    if (min > max) {
        throw IllegalArgumentException("Called clamp with min < max (min: $min, max: $max")
    }
    return Math.max(min, Math.min(max, value))
}

fun clamp(value: Float, min: Float, max: Float): Float {
    if (min > max) {
        throw IllegalArgumentException("Called clamp with min < max (min: $min, max: $max")
    }
    return Math.max(min, Math.min(max, value))
}
