package trypp.support.pattern.observer

import java.util.*

/**
 * Base class for observable classes that listeners can listen to. The logic that triggers the
 * listeners must be handled by the deriving class.
 *
 * @param L Listener type for this observable
 */
abstract class Observable<L>() {
    companion object {
        /**
         * In general, most events will only have one listener.
         */
        private val INITIAL_CAPACITY = 1
    }

    protected val listeners = ArrayList<L>(INITIAL_CAPACITY)

    operator fun plusAssign(listener: L) {
        addListener(listener)
    }

    operator fun minusAssign(listener: L) {
        removeListener(listener)
    }

    operator fun minusAssign(handle: Int) {
        removeListener(handle)
    }

    /**
     * Like the += operator but returns a handle to the listener as a return value.
     *
     * This can be a useful way to get a return value of a lambda method which we can remove later.
     *
     * e.g.
     *
     * ```
     * val handle = event.addListener { ... }
     * event.removeListener(handle)
     * ```
     */
    fun addListener(listener: L): Int {
        listeners.add(listener)
        return listener!!.hashCode()
    }

    fun removeListener(listener: L): Boolean {
        return listeners.remove(listener)
    }

    /**
     * Remove a listener by the handle value returned by [addListener]
     */
    fun removeListener(handle: Int): Boolean {
        return listeners.removeIf { it!!.hashCode() == handle }
    }

    fun clearListeners() {
        listeners.clear()
    }
}
