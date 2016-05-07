package dhcoder.support.math

import trypp.support.memory.Poolable

/**
 * Simple class that represents an integer (x,y) coordinate.
 */
data class IntCoord(var x: Int, var y: Int) : Poolable {

    constructor() : this(0, 0) {
    }

    fun set(x: Int, y: Int): IntCoord {
        this.x = x
        this.y = y
        return this
    }

    fun setFrom(rhs: IntCoord): IntCoord {
        this.x = rhs.x
        this.y = rhs.y
        return this
    }

    override fun reset() {
        set(0, 0)
    }

    override fun toString(): String {
        return "(${x}x${y})"
    }
}
