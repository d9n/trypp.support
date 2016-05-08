package trypp.support.memory

/**
 * Simple test item to use in pool tests
 */
class PoolItem : Poolable {
    var resetCount = 0
        private set

    override fun reset() {
        resetCount++
    }
}
