package trypp.support.memory

import com.google.common.truth.Truth.assertThat
import org.testng.Assert

import org.testng.annotations.Test

class PoolableTest {
    class DummyPoolable : Poolable {
        var resetCount = 0
        override fun reset() {
            resetCount++
        }
    }

    class NoEmptyConstructorPoolable(val id: Int) : Poolable {
        override fun reset() {

        }
    }

    @Test fun poolOfPoolableWorksAsExpected() {
        val dummyPool = Pool.of(DummyPoolable::class, capacity = 2)

        val item1 = dummyPool.grabNew()
        val item2 = dummyPool.grabNew()

        assertThat(dummyPool.remainingCount).isEqualTo(0)

        dummyPool.freeAll()

        assertThat(dummyPool.remainingCount).isEqualTo(2)

        assertThat(item1.resetCount).isEqualTo(1)
        assertThat(item2.resetCount).isEqualTo(1)
    }

    @Test fun poolOfPoolableClassWithoutEmptyConstructorThrowsException() {
        try {
            Pool.of(NoEmptyConstructorPoolable::class)
            Assert.fail("Poolable class must have an empty constructor")
        }
        catch (e: IllegalArgumentException) {
        }
    }

}
