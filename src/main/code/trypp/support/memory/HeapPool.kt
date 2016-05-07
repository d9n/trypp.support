package dhcoder.support.memory

import trypp.support.collections.ArrayMap
import trypp.support.memory.Pool
import trypp.support.memory.Poolable
import kotlin.reflect.KClass

/**
 * A pool which is better than the base [Pool] at handling allocations and deallocations in any
 * order, especially when the pool in question is relatively large (more than dozens of elements).
 *
 * It works by mapping elements in the pool to their allocation index - meaning we can remove the
 * element directly instead of searching through the pool to find it.
 */
class HeapPool<T : Any> private constructor(private val innerPool: Pool<T>) {
    private val itemIndices: ArrayMap<T, Int>

    constructor(allocate: () -> T, reset: (T) -> Unit, capacity: Int = DEFAULT_CAPACITY) :
        this(Pool(allocate, reset, capacity)) {
    }

    init {
        itemIndices = ArrayMap<T, Int>(innerPool.capacity)
    }

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
            val movedItem = items[index] // An old item was moved to fill in the place of the removed item
            itemIndices.replace(movedItem, index)
        }
    }

    fun freeAll() {
        innerPool.freeAll()
        itemIndices.clear()
    }

    companion object {

        val DEFAULT_CAPACITY = 200 // HeapPools should be relatively large

        fun <P : Poolable> of(poolableClass: KClass<P>): HeapPool<P> {
            return HeapPool(Pool.of(poolableClass, DEFAULT_CAPACITY))
        }

        fun <P : Poolable> of(poolableClass: KClass<P>, capacity: Int): HeapPool<P> {
            return HeapPool(Pool.of(poolableClass, capacity))
        }
    }
}
