package trypp.support.memory

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class HeapPoolTest {

    @Test fun grabNewAndFreeWorksAsExpected() {
        val pool = HeapPool({ PoolItem() }, { it.reset() }, capacity = 5)
        assertThat(pool.itemsInUse.size).isEqualTo(0)
        assertThat(pool.remainingCount).isEqualTo(5)

        val item1 = pool.grabNew()
        assertThat(pool.itemsInUse.size).isEqualTo(1)
        assertThat(pool.remainingCount).isEqualTo(4)

        val item2 = pool.grabNew()
        val item3 = pool.grabNew()
        val item4 = pool.grabNew()
        val item5 = pool.grabNew()
        assertThat(pool.itemsInUse.size).isEqualTo(5)
        assertThat(pool.remainingCount).isEqualTo(0)

        pool.free(item3)
        assertThat(pool.itemsInUse.size).isEqualTo(4)
        assertThat(pool.remainingCount).isEqualTo(1)

        pool.free(item5)
        pool.free(item1)
        pool.free(item2)
        pool.free(item4)
        assertThat(pool.itemsInUse.size).isEqualTo(0)
        assertThat(pool.remainingCount).isEqualTo(5)
    }

    @Test fun poolOfPoolableMethodWorksAsExpected() {
        val pool = HeapPool.of(PoolItem::class)

        var item = pool.grabNew()
        assertThat(item.resetCount).isEqualTo(0)
        pool.free(item)
        assertThat(item.resetCount).isEqualTo(1)

        item = pool.grabNew()
        assertThat(item.resetCount).isEqualTo(1)
    }

    @Test fun makeResizableAllowsPoolToGrow() {
        var allocationCount = 0
        val pool = HeapPool({ allocationCount++; PoolItem() }, { it.reset() }, capacity = 2)

        assertThat(pool.resizable).isFalse()
        pool.makeResizable(maxCapacity = 3)
        assertThat(pool.resizable).isTrue()

        assertThat(pool.capacity).isEqualTo(2)
        assertThat(pool.maxCapacity).isEqualTo(3)
        assertThat(allocationCount).isEqualTo(2)

        pool.grabNew()
        pool.grabNew()
        pool.grabNew()

        assertThat(pool.capacity).isEqualTo(3)
        assertThat(pool.maxCapacity).isEqualTo(3)
        assertThat(allocationCount).isEqualTo(3)
    }

    @Test fun freeAllWorksAsExpected() {
        val pool = HeapPool({ PoolItem() }, { it.reset() })
        pool.grabNew()
        pool.grabNew()
        pool.grabNew()

        assertThat(pool.itemsInUse.size).isEqualTo(3)

        pool.freeAll()
        assertThat(pool.itemsInUse.size).isEqualTo(0)
    }
//
//    @Test fun makeResizableWithBadCapacityThrowsException() {
//        val pool = HeapPool({ PoolItem() }, { it.reset() }, capacity = 2)
//        try {
//            pool.makeResizable(maxCapacity = 1)
//            Assert.fail("Can't resize pool below its current capacity")
//        }
//        catch (e: IllegalArgumentException) {
//        }
//
//        assertThat(pool.resizable).isFalse()
//    }
//
//
//    @Test fun freeUnownedObjectThrowsException() {
//        val pool = HeapPool({ PoolItem() }, { it.reset() })
//        try {
//            pool.free(PoolItem())
//            Assert.fail("Pool can't free what it doesn't own")
//        }
//        catch (e: IllegalArgumentException) {
//        }
//    }
//
//    @Test fun freeObjectTwiceThrowsException() {
//        val pool = HeapPool({ PoolItem() }, { it.reset() })
//        val item = pool.grabNew()
//        pool.free(item)
//        try {
//            pool.free(item)
//            Assert.fail("Pool can't free same item twice")
//        }
//        catch (e: IllegalArgumentException) {
//        }
//    }
//
//    @Test fun goingOverCapacityThrowsException() {
//        val pool = HeapPool({ PoolItem() }, { it.reset() }, capacity = 3)
//        pool.grabNew()
//        pool.grabNew()
//        pool.grabNew()
//        try {
//            pool.grabNew()
//            Assert.fail("Can't grab new if a pool is out of capacity")
//        }
//        catch (e: IllegalStateException) {
//        }
//    }
//
//    @Test fun invalidCapacityThrowsException() {
//        try {
//            Pool({ PoolItem() }, { it.reset() }, capacity = 0)
//            Assert.fail("Pool can't be instantiated with no capacity")
//        }
//        catch (e: IllegalArgumentException) {
//        }
//
//        try {
//            Pool({ PoolItem() }, { it.reset() }, capacity = -5)
//            Assert.fail("Pool can't be instantiated with negative capacity")
//        }
//        catch (e: IllegalArgumentException) {
//        }
//    }
}
