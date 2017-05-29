package trypp.support.opt

import trypp.support.memory.Poolable

/**
 * Optional handling for the float primitive type. Like Float? but you can allocate once and avoid
 * accruing the auto-boxing allocation cost.
 */
class OptFloat : Poolable {
    companion object {

        /**
         * Creates an optional wrapper around a float value.
         */
        fun of(value: Float): OptFloat {
            return OptFloat(value)
        }

        /**
         * Creates an optional which is initialized to no value.
         */
        fun withNoValue(): OptFloat {
            return OptFloat()
        }
    }

    private var value: Float = 0f
    private var hasValue: Boolean = false

    /**
     * Create an optional without a value.
     *
     *
     * Use [withNoValue] instead.
     */
    internal constructor() {
        hasValue = false
    }

    /**
     * Create an optional with an initial value.
     *
     *
     * Use [of] instead.
     */
    internal constructor(value: Float) {
        set(value)
    }

    /**
     * Clears the value of this optional.
     */
    fun clear() {
        hasValue = false
        value = 0f // Set to be safe - all cleared OptFloats will have the same value
    }

    /**
     * Returns the current value of this optional, or throws an exception otherwise. You may
     * consider checking [hasValue] first before calling this method.
     *
     * @throws IllegalStateException if this optional doesn't currently have a value.
     */
    fun getValue(): Float {
        if (!hasValue) {
            throw IllegalStateException("Call to getValue() on a valueless optional.")
        }
        return value
    }

    /**
     * Returns the current value of this optional or the specified default value if this optional
     * has no value.
     */
    fun getValueOr(defaultValue: Float): Float {
        return if (hasValue) value else defaultValue
    }

    /**
     * Sets this optional to a new value.
     */
    fun set(value: Float) {
        this.value = value
        this.hasValue = true
    }

    /**
     * Set the value of this optional to the value held by another optional (or no value if the
     * target optional is also valueless).
     */
    fun setFrom(rhs: OptFloat) {
        this.hasValue = rhs.hasValue
        this.value = rhs.value
    }

    /**
     * Returns true if this optional currently has a value set.
     */
    fun hasValue(): Boolean {
        return hasValue
    }

    override fun hashCode(): Int {
        return if (hasValue) value.hashCode() else 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other is OptFloat) {
            if (hasValue && other.hasValue) {
                return value == other.value
            }
            else {
                return !hasValue && !other.hasValue
            }
        }

        return false
    }

    override fun toString(): String {
        return if (hasValue) "OptFloat{$value}" else "OptFloat{}"
    }

    /**
     * Overridden from the [Poolable] interface, but otherwise use [clear] instead for readability.
     */
    override fun reset() {
        clear()
    }
}
