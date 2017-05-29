package trypp.support.memory;

/**
 * Interface which marks a class as designed intentionally to work with a [Pool].
 *
 * Subclasses are expected to provide a default constructor (either explicitly or by specifying no
 * constructors at all). The constructor can be private, if there are API concerns -
 * [Pool] can still work with that.
 */
interface Poolable {
    fun reset()
}
