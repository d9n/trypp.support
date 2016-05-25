package trypp.support.memory

import trypp.support.extensions.swapToEndAndRemove
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * A class which manages a pool of pre-allocated objects so you can avoid thrashing Android's
 * garbage collector when you want to make lots of small, temporary allocations.
 *
 * Note: This class is appropriate for small pools or pools where you allocate temporaries which you
 * then deallocate in reverse order (like a stack). Use {@link HeapPool} for larger pools especially
 * when you want to allocate and return elements in random order.
 *
 * Pools are constructed with two callbacks, one which allocates a new instance of a class, and one
 * which clears an instance of a class for re-use later. After that, just call {@link #grabNew()}
 * and {@link #free(Object)}, and this class will take care of the rest!
 *
 * Be careful using pools. After you grab something from a pool, you have to remember to release it
 * - and if anyone is still holding on to that reference after you release it, that's an error -
 * they will soon find the reference reset underneath them.
 */
class Pool<T>(private val allocate: () -> T, private val reset: (T) -> Unit, capacity: Int = DEFAULT_CAPACITY) {

    companion object {
        val DEFAULT_CAPACITY = 10

        /**
         * Although [Pool] can work with any class provided an `allocate` and `reset` callbacks, if
         * used in conjunction with [Poolable] classes, default callbacks are provided.
         */
        fun <P : Poolable> of(poolableClass: KClass<P>, capacity: Int = DEFAULT_CAPACITY): Pool<P> {
            val emptyCons: KFunction<P>
            try {
                emptyCons = poolableClass.constructors.first { it.parameters.size == 0 }
            }
            catch(e: NoSuchElementException) {
                throw IllegalArgumentException(
                    "Can't create pool of $poolableClass: No empty constructor")
            }

            return Pool({ emptyCons.call() }, { it.reset() }, capacity)
        }
    }

    private val freeItems = Stack<T>()
    private val _itemsInUse: ArrayList<T>
    val itemsInUse: List<T>
        get() = _itemsInUse


    var resizable = false
        private set
    var capacity = capacity
        private set
    var maxCapacity = capacity
        private set

    val remainingCount: Int
        get() = freeItems.size

    init {
        if (capacity <= 0) {
            throw IllegalArgumentException("Invalid pool capacity: $capacity")
        }

        freeItems.ensureCapacity(capacity)
        _itemsInUse = ArrayList<T>(capacity)

        for (i in 0 until capacity) {
            freeItems.push(allocate())
        }
    }

    fun makeResizable(maxCapacity: Int): Pool<T> {
        if (maxCapacity < capacity) {
            throw IllegalArgumentException(
                "Can't set pool's max capacity to $maxCapacity as that is smaller than current capacity, $capacity")
        }

        resizable = true
        this.maxCapacity = maxCapacity
        return this
    }

    fun grabNew(): T {

        if (remainingCount == 0) {
            if (!resizable || capacity == maxCapacity) {
                throw IllegalStateException(
                    "Requested too many items from this pool (capacity: $capacity) - are you forgetting to free some?")
            }

            val oldCapacity = capacity
            capacity = Math.min(capacity * 2, maxCapacity)

            freeItems.ensureCapacity(capacity)
            _itemsInUse.ensureCapacity(capacity)

            for (i in oldCapacity until capacity) {
                freeItems.push(allocate())
            }
        }

        val newItem = freeItems.pop()
        _itemsInUse.add(newItem)

        return newItem
    }

    fun mark(): Int {
        return _itemsInUse.size
    }

    fun freeToMark(mark: Int) {
        freeCount(_itemsInUse.size - mark)
    }

    fun freeCount(count: Int) {
        var indexToFree = _itemsInUse.size - 1
        for (i in 0 until count) {
            val item = _itemsInUse[indexToFree]
            returnItemToPool(item)
            _itemsInUse.removeAt(indexToFree)
            indexToFree--
        }
    }

    fun freeAll() {
        freeCount(_itemsInUse.size)
    }

    fun free(item: T) {
        _itemsInUse.swapToEndAndRemove(item)
        returnItemToPool(item)
    }

    /**
     * Convenience method that runs an action and then releases any items grabbed within the action.
     */
    fun runAndFree(action: () -> Unit) {
        val mark = mark()
        action()
        freeToMark(mark)
    }

    /**
     * Release item by index. As a user, you should never really know what a pool item's index is.
     * If you think you really need it, perhaps you want to use [HeapPool] instead?
     */
    internal fun freeAt(index: Int) {
        returnItemToPool(_itemsInUse.swapToEndAndRemove(index))
    }

    private fun returnItemToPool(item: T) {
        reset(item)
        freeItems.push(item)
    }
}
