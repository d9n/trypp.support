package trypp.support.memory

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test

class PoolTest {

    @Test fun grabNewAvoidsAllocation() {
        var allocationCount = 0
        val pool = Pool({ allocationCount++; PoolItem() }, { it.reset() }, capacity = 1)

        assertThat(allocationCount).isEqualTo(1)

        var item = pool.grabNew()
        assertThat(allocationCount).isEqualTo(1)
        assertThat(item.resetCount).isEqualTo(0)

        pool.free(item)
        assertThat(item.resetCount).isEqualTo(1)

        item = pool.grabNew()
        assertThat(allocationCount).isEqualTo(1)
        assertThat(item.resetCount).isEqualTo(1)
    }

    @Test fun poolOfPoolableMethodWorksAsExpected() {
        val pool = Pool.of(PoolItem::class)

        var item = pool.grabNew()
        assertThat(item.resetCount).isEqualTo(0)
        pool.free(item)
        assertThat(item.resetCount).isEqualTo(1)

        item = pool.grabNew()
        assertThat(item.resetCount).isEqualTo(1)
    }

    @Test fun grabNewAndFreeWorksAsExpected() {
        val pool = Pool({ PoolItem() }, { it.reset() }, capacity = 5)
        assertThat(pool.itemsInUse.size).isEqualTo(0)
        assertThat(pool.remainingCount).isEqualTo(5)

        var item1 = pool.grabNew()
        assertThat(pool.itemsInUse.size).isEqualTo(1)
        assertThat(pool.remainingCount).isEqualTo(4)

        var item2 = pool.grabNew()
        var item3 = pool.grabNew()
        var item4 = pool.grabNew()
        var item5 = pool.grabNew()
        assertThat(pool.itemsInUse.size).isEqualTo(5)
        assertThat(pool.remainingCount).isEqualTo(0)

        pool.free(item1)
        assertThat(pool.itemsInUse.size).isEqualTo(4)
        assertThat(pool.remainingCount).isEqualTo(1)

        pool.free(item2)
        pool.free(item3)
        pool.free(item4)
        pool.free(item5)
        assertThat(pool.itemsInUse.size).isEqualTo(0)
        assertThat(pool.remainingCount).isEqualTo(5)
    }

    @Test fun makeResizableAllowsPoolToGrow() {
        var allocationCount = 0
        val pool = Pool({ allocationCount++; PoolItem() }, { it.reset() }, capacity = 2)

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

    @Test fun markAndFreeWorksAsExpected() {
        val pool = Pool({ PoolItem() }, { it.reset() })
        val mark1 = pool.mark()
        pool.grabNew()
        val mark2 = pool.mark()
        pool.grabNew()
        pool.grabNew()

        assertThat(pool.itemsInUse.size).isEqualTo(3)

        pool.freeToMark(mark2)
        assertThat(pool.itemsInUse.size).isEqualTo(1)
        pool.freeToMark(mark1)
        assertThat(pool.itemsInUse.size).isEqualTo(0)
    }

    @Test fun freeCountWorksAsExpected() {
        val pool = Pool({ PoolItem() }, { it.reset() })
        pool.grabNew()
        pool.grabNew()
        pool.grabNew()

        assertThat(pool.itemsInUse.size).isEqualTo(3)

        pool.freeCount(2)
        assertThat(pool.itemsInUse.size).isEqualTo(1)
    }

    @Test fun freeAllWorksAsExpected() {
        val pool = Pool({ PoolItem() }, { it.reset() })
        pool.grabNew()
        pool.grabNew()
        pool.grabNew()

        assertThat(pool.itemsInUse.size).isEqualTo(3)

        pool.freeAll()
        assertThat(pool.itemsInUse.size).isEqualTo(0)
    }

    @Test fun makeResizableWithBadCapacityThrowsException() {
        val pool = Pool({ PoolItem() }, { it.reset() }, capacity = 2)
        try {
            pool.makeResizable(maxCapacity = 1)
            Assert.fail("Can't resize pool below its current capacity")
        }
        catch (e: IllegalArgumentException) {
        }

        assertThat(pool.resizable).isFalse()
    }


    @Test fun freeUnownedObjectThrowsException() {
        val pool = Pool({ PoolItem() }, { it.reset() })
        try {
            pool.free(PoolItem())
            Assert.fail("Pool can't free what it doesn't own")
        }
        catch (e: IllegalArgumentException) {
        }
    }

    @Test fun freeObjectTwiceThrowsException() {
        val pool = Pool({ PoolItem() }, { it.reset() })
        val item = pool.grabNew()
        pool.free(item)
        try {
            pool.free(item)
            Assert.fail("Pool can't free same item twice")
        }
        catch (e: IllegalArgumentException) {
        }
    }

    @Test fun goingOverCapacityThrowsException() {
        val pool = Pool({ PoolItem() }, { it.reset() }, capacity = 3)
        pool.grabNew()
        pool.grabNew()
        pool.grabNew()
        try {
            pool.grabNew()
            Assert.fail("Can't grab new if a pool is out of capacity")
        }
        catch (e: IllegalStateException) {
        }
    }

    @Test fun invalidCapacityThrowsException() {
        try {
            Pool({ PoolItem() }, { it.reset() }, capacity = 0)
            Assert.fail("Pool can't be instantiated with no capacity")
        }
        catch (e: IllegalArgumentException) {
        }

        try {
            Pool({ PoolItem() }, { it.reset() }, capacity = -5)
            Assert.fail("Pool can't be instantiated with negative capacity")
        }
        catch (e: IllegalArgumentException) {
        }
    }
}
