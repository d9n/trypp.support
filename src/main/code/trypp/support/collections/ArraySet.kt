package trypp.support.collections

import trypp.support.collections.ArrayMap.InsertMethod

/**
 * Like [ArrayMap] but where you only care whether a key is present or not and values don't matter.
 *
 * Construct a set with an expected size and load factor. The load factor dictates how full a
 * hashtable should get before it resizes. A load factor of 0.5 means the table should resize
 * when it is 50% full.
 *
 * @throws IllegalArgumentException if the input load factor is not between 0 and 1.
 */
class ArraySet<E : Any>(expectedSize: Int = ArrayMap.DEFAULT_EXPECTED_SIZE, loadFactor: Float = ArrayMap.DEFAULT_LOAD_FACTOR) {

    private var internalMap: ArrayMap<E, Any> = ArrayMap(expectedSize, loadFactor)

    val size: Int
        get() = internalMap.size

    /**
     * Note: This method allocates an array and should only be used in non-critical areas.
     */
    fun getKeys(): List<E> {
        return internalMap.getKeys()
    }

    val isEmpty: Boolean
        get() = internalMap.isEmpty

    operator fun contains(element: E): Boolean {
        return internalMap.containsKey(element)
    }

    /**
     * Add the element, which must NOT exist in the set. Use [putIf] if you don't need this
     * requirement.
     *
     * @throws IllegalArgumentException if the element already exists in the set.
     */
    fun put(element: E) {
        internalMap.put(element, DUMMY_OBJECT)
    }

    /**
     * Add the element, if it's not already in the set.

     * @return `true` if the element was only just now added into the set.
     */
    fun putIf(element: E): Boolean {
        return internalMap.putOrReplace(element, DUMMY_OBJECT) === InsertMethod.PUT
    }

    /**
     * Remove the element, which MUST exist in the set. Use [removeIf] if you don't need this
     * requirement. This distinction can be useful to assert cases when you want to guarantee the
     * element is in the set, and it also better mimics the related [ArrayMap] class.
     */
    fun remove(element: E) {
        internalMap.remove(element)
    }

    /**
     * Remove the element, which may or may not exist in the set. Use [remove] if you want to assert
     * existence of the element in the set.

     * @return `true` if the element was in the set.
     */
    fun removeIf(element: E): Boolean {
        return internalMap.removeIf(element) != null
    }

    fun clear() {
        internalMap.clear()
    }

    companion object {
        private val DUMMY_OBJECT = Any()
    }
}
