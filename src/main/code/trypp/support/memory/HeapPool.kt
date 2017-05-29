package trypp.support.memory

import trypp.support.collections.ArrayMap
import kotlin.reflect.KClass

/**
 * A pool which is better than the base [Pool] at handling allocations and deallocations in any
 * order, especially when the pool in question is relatively large (more than dozens of elements).
 *
 * It works by mapping elements in the pool to their allocation index - meaning we can remove the
 * element directly instead of searching through the pool to find it.
 */
class HeapPool<T : Any> private constructor(private val innerPool: Pool<T>) {
    private val itemIndices: ArrayMap<T, Int> = ArrayMap(innerPool.capacity)

    constructor(allocate: () -> T, reset: (T) -> Unit, capacity: Int = DEFAULT_CAPACITY) :
    this(Pool(allocate, reset, capacity))

    fun makeResizable(maxCapacity: Int): HeapPool<T> {
        innerPool.makeResizable(maxCapacity)
        return this
    }

    val capacity: Int
        get() = innerPool.capacity

    val maxCapacity: Int
        get() = innerPool.maxCapacity

    val itemsInUse: List<T>
        get() = innerPool.itemsInUse

    val remainingCount: Int
        get() = innerPool.remainingCount

    val resizable: Boolean
        get() = innerPool.resizable

    fun grabNew(): T {
        val item = innerPool.grabNew()
        itemIndices.put(item, innerPool.itemsInUse.size - 1)
        return item
    }

    fun free(item: T) {
        val index = itemIndices[item]
        innerPool.freeAt(index)

        itemIndices.remove(item)
        val items = itemsInUse
        if (items.size > index) {
            // An old item was moved to fill in the place of the removed item
            val movedItem = items[index]
            itemIndices.replace(movedItem, index)
        }
    }

    fun freeAll() {
        innerPool.freeAll()
        itemIndices.clear()
    }

    companion object {
        val DEFAULT_CAPACITY = 200 // HeapPools should be relatively large

        fun <P : Poolable> of(poolableClass: KClass<P>,
                              capacity: Int = DEFAULT_CAPACITY): HeapPool<P> {
            return HeapPool(Pool.of(poolableClass, capacity))
        }
    }
}
