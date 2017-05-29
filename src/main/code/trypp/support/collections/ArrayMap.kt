package trypp.support.collections

import trypp.support.collections.ArrayMap.InsertMethod.REPLACE
import trypp.support.math.log2
import trypp.support.memory.Pool
import trypp.support.opt.OptInt
import java.util.*
import kotlin.properties.Delegates

/**
 * A map implementation that uses [ArrayList]s under the hood, allowing it to preallocate all memory
 * ahead of time (unless the collection needs to grow).
 *
 * Create a map with an expected size and load factor. The load factor dictates how full a hashtable
 * should get before it resizes. A load factor of 0.5 means the table should resize when it is 50%
 * full.
 *
 * @throws IllegalArgumentException if the input load factor is not between 0 and 1.
 */
class ArrayMap<K : Any, V : Any>(expectedSize: Int = DEFAULT_EXPECTED_SIZE, private val loadFactor: Float = DEFAULT_LOAD_FACTOR) {

    companion object {
        // See: http://planetmath.org/goodhashtableprimes
        private val PRIME_TABLE_SIZES = intArrayOf(
            3, // 2^0 -- Ex: If requested size is 0-1, actually use 3.
            3, // 2^1 -- Ex: If requested size is 1-2, actually use 3.
            7, // 2^2 -- Ex: If requested size is 3-4, actually use 7.
            13, // 2^3 -- Ex: If requested size is 5-8, actually use 13. etc...
            23, // 2^4
            53, // 2^5
            97, // 2^6
            193, // 2^7
            389, // 2^8
            769, // 2^9
            1543, // 2^10
            3079, // 2^11
            6151, // 2^12
            12289, // 2^13
            24593, // 2^14
            49157, // 2^15
            98317, // 2^16
            196613, // 2^17
            393241, // 2^18
            786433, // 2^19
            1572869, // 2^20
            3145739, // 2^21
            6291469, // 2^22
            12582917, // 2^23
            25165843, // 2^24
            50331653, // 2^25
            100663319, // 2^26
            201326611, // 2^27
            402653189, // 2^28
            805306457, // 2^29
            1610612741)// 2^30
        val DEFAULT_EXPECTED_SIZE = 10
        val DEFAULT_LOAD_FACTOR = 0.75f

        /**
         * If `true` do some extra checks in [put] to make sure we aren't reusing the same key
         * instance twice. For example, since keys are often [Poolable], a user may reset a key and
         * use it again without realizing they left a copy in a map.
         *
         * The checks are somewhat expensive, so it's probably best to leave this check `false` in
         * production code.
         */
        var ASSERT_NO_DUP_KEYS = false

        private fun getNextPrimeSize(requestedSize: Int): Int {
            val log2 = log2(requestedSize)
            if (log2 >= PRIME_TABLE_SIZES.size) {
                throw IllegalStateException(
                    "Table can't grow big enough to accommodate requested size")
            }

            return PRIME_TABLE_SIZES[log2]
        }
    }


    enum class InsertMethod {
        PUT,
        REPLACE
    }

    private enum class IndexMethod {
        GET,
        PUT
    }

    private val indexPool = Pool.of(OptInt::class, 1)
    var size: Int = 0
        private set
    private var resizeAtSize: Int = 0
    /**
     * Returns the potential capacity of this ArrayMap. Note that an ArrayMap will get resized before max capacity is
     * reached, depending on its load factor, so don't expect to fill one completely.
     */
    var capacity: Int = 0
        private set

    /**
     * We use a probing algorithm to find a key's index, jumping over spots that are already taken to find a free
     * bucket. But if a key/value is removed, that leaves a hold that future searches for our key may fall in to. We
     * mark the spot so that, even if a key was removed, we know it used to be there.
     */
    private var keyIsDead: BooleanArray by Delegates.notNull()
    private var keys: ArrayList<K?> by Delegates.notNull()
    private var values: ArrayList<V?> by Delegates.notNull()

    init {
        if (loadFactor <= 0.0f || loadFactor >= 1.0f) {
            throw IllegalArgumentException("Load factor must be between 0 and 1. Got $loadFactor")
        }

        capacity = getNextPrimeSize(expectedSize)

        // Ensure initial capacity to be at least large enough so we don't resize if user puts in
        // `expectedSize` items.
        val enoughInitialCapacity = (expectedSize / loadFactor + 0.5f).toInt() // 0.5f for rounding
        while (capacity < enoughInitialCapacity) {
            capacity = getNextPrimeSize(capacity + 1)
        }

        initializeStructures()
    }

    val isEmpty: Boolean
        get() = size == 0

    /**
     * Note: This method allocates an array and should only be used in non-critical areas.
     */
    fun getKeys(): List<K> {
        val compactKeys = ArrayList<K>(size)
        for (i in 0..capacity - 1) {
            val key = keys[i]
            if (key != null) {
                compactKeys.add(key)
            }
        }
        return compactKeys
    }

    /**
     * Note: This method allocates an array and should only be used in non-critical code paths.
     */
    fun getValues(): List<V> {
        val compactValues = ArrayList<V>(size)
        for (i in 0..capacity - 1) {
            val value = values[i]
            if (value != null) {
                compactValues.add(value)
            }
        }
        return compactValues
    }

    fun containsKey(key: K): Boolean {
        val indexOpt = indexPool.grabNew()
        getIndex(key, IndexMethod.GET, indexOpt)
        val containsKey = indexOpt.hasValue()
        indexPool.free(indexOpt)

        return containsKey
    }

    /**
     * Gets the value associated with the passed in key, or throws an exception if that key is not registered.

     * @throws IllegalArgumentException if no value is associated with the passed in key.
     */
    operator fun get(key: K): V {
        val result = getIf(key) ?:
            throw IllegalArgumentException("No value associated with key $key")
        return result
    }

    /**
     * Lie [get] but can handle if the key is not in the map.
     */
    fun getIf(key: K): V? {
        var result: V? = null
        val indexOpt = indexPool.grabNew()
        getIndex(key, IndexMethod.GET, indexOpt)
        if (indexOpt.hasValue()) {
            result = values[indexOpt.get()]
        }
        indexPool.free(indexOpt)
        return result
    }

    /**
     * Put the key in the map. For efficiency, it is not intended for you to call this method with
     * the same key twice (without first removing it) - if done, the existing value will simply be
     * left in place. Use [replace] if that's the behavior you desire, or use [putOrReplace] if
     * you're not sure if the key exists at all.
     *
     * This may seem like overly strict behavior for a put method, but the goal here is to allow
     * users of the class to enforce expected behavior on how their map is being used - sometimes,
     * it is simply an error to trounce one key's value with another, and doing so without realizing
     * it leads to the sort of bug that is really hard to find later.
     *
     * NOTE: As a key's hashcode can change over time, this method won't catch reused keys with
     * updated hashcodes unless [ASSERT_NO_DUP_KEYS] is set to `true`.
     */
    fun put(key: K, value: V) {
        val indexOpt = indexPool.grabNew()
        getIndex(key, IndexMethod.PUT, indexOpt)
        val index = indexOpt.get()
        indexPool.freeCount(1)

        if (!keyIsDead[index] && keys[index] != null) {
            throw IllegalArgumentException(
                "Attempting to use the same key twice: $key. Are you using the same key accidentally?")
        }

        if (ASSERT_NO_DUP_KEYS) {
            keys.forEach {
                assert(key !== it, { "Key $key reused. Did you accidentally leave an old key in the map?" })
            }
        }

        setInternal(index, key, value)

        size++
        if (size == resizeAtSize) {
            increaseCapacity()
        }
    }

    /**
     * Use if you know for sure the key is already in the map. This is a bit more efficient than
     * using [remove] followed by [put]
     */
    fun replace(key: K, value: V) {

        val indexOpt = indexPool.grabNew()
        getIndex(key, IndexMethod.GET, indexOpt)
        val index = indexOpt.getOr(-1)
        indexPool.freeCount(1)

        if (index == -1) {
            throw IllegalArgumentException(
                "Attempting to replace key not found in the map: $key. Use putOrReplace instead?")
        }

        setInternal(index, key, value)
    }

    /**
     * Use this if you're not sure if the key is already in the map or not. This is less efficient
     * than either using [put] or [replace] but is useful if you simply don't care.
     */
    fun putOrReplace(key: K, value: V): InsertMethod {
        val indexOpt = indexPool.grabNew()
        getIndex(key, IndexMethod.GET, indexOpt)

        if (indexOpt.hasValue()) {
            setInternal(indexOpt.get(), key, value)
            indexPool.freeCount(1)
            return REPLACE
        }
        else {
            indexPool.freeCount(1)
            put(key, value)
            return InsertMethod.PUT
        }
    }

    /**
     * Remove the value associated with the passed in key. The key MUST be in the map because this
     * method needs to return a non-null value. If you are not sure if the key is in the map, you
     * can instead use [removeIf]

     * @throws IllegalArgumentException if the key is not found in the map.
     */
    fun remove(key: K): V {
        val result = removeIf(key) ?:
            throw IllegalArgumentException("No value associated with key $key")

        return result
    }

    /**
     * Like [remove] but can handle the case of the key not being found in the map.
     */
    fun removeIf(key: K): V? {
        var result: V? = null

        val indexOpt = indexPool.grabNew()
        getIndex(key, IndexMethod.GET, indexOpt)
        if (indexOpt.hasValue()) {
            val index = indexOpt.get()
            keyIsDead[index] = true
            result = values[index]
            values[index] = null
            keys[index] = null
            size--

        }
        indexPool.free(indexOpt)
        return result
    }

    fun clear() {
        for (i in 0 until capacity) {
            keyIsDead[i] = false
            keys[i] = null
            values[i] = null
        }

        size = 0
    }

    private fun setInternal(index: Int, key: K, value: V) {
        keys[index] = key
        values[index] = value
        keyIsDead[index] = false
    }

    private fun increaseCapacity() {
        val oldCapacity = capacity
        capacity = getNextPrimeSize(capacity + 1)

        val keysCopy = keys
        val valuesCopy = values

        initializeStructures()

        for (i in 0..oldCapacity - 1) {
            if (keysCopy[i] != null) {
                val key = keysCopy[i]!!
                val value = valuesCopy[i]!!

                put(key, value)
            }
        }
    }

    private fun initializeStructures() {
        keyIsDead = BooleanArray(capacity)
        keys = ArrayList<K?>(capacity)
        values = ArrayList<V?>(capacity)

        size = 0
        for (i in 0 until capacity) {
            keys.add(null)
            values.add(null)
        }

        resizeAtSize = (capacity * loadFactor).toInt()
    }

    // Returns index of the key in this hashtable. If 'forGetMethods' is true, 'outIndex' won't have any value set if
    // the key couldn't be found.
    private fun getIndex(key: K, indexMethod: IndexMethod, outIndex: OptInt) {
        outIndex.clear()

        val positiveHashCode = key.hashCode() and 0x7FFFFFFF
        val initialIndex = positiveHashCode % capacity
        var index = initialIndex
        var loopCount = 1
        while ((keys[index] != null || keyIsDead[index]) && loopCount <= capacity) {
            if (indexMethod == IndexMethod.PUT && keyIsDead[index]) {
                // This used to be a bucket for a key that got removed, so it's free for reuse!
                outIndex.set(index)
                return
            }

            if (key == keys[index]) {
                outIndex.set(index)
                return
            }

            index = (initialIndex + loopCount * loopCount) % capacity // Quadratic probing
            loopCount++
        }

        if (indexMethod == IndexMethod.PUT) {
            outIndex.set(index)
        }
    }

}
