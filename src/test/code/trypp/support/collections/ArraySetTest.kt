package trypp.support.collections

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test

class ArraySetTest {

    @Test fun createIntegerSet() {
        val numericSet = ArraySet<Int>()

        numericSet.put(1)
        numericSet.put(2)
        numericSet.put(3)
        numericSet.put(4)
        numericSet.put(5)
        numericSet.put(6)
        numericSet.put(7)
        numericSet.put(8)
        numericSet.put(9)

        assertThat(numericSet.size).isEqualTo(9);
        assertThat(numericSet.getKeys()).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9)

        assertThat(numericSet.contains(0)).isFalse()
        assertThat(numericSet.contains(1)).isTrue()
        assertThat(numericSet.contains(2)).isTrue()
        assertThat(numericSet.contains(3)).isTrue()
        assertThat(numericSet.contains(4)).isTrue()
        assertThat(numericSet.contains(5)).isTrue()
        assertThat(numericSet.contains(6)).isTrue()
        assertThat(numericSet.contains(7)).isTrue()
        assertThat(numericSet.contains(8)).isTrue()
        assertThat(numericSet.contains(9)).isTrue()
        assertThat(numericSet.contains(10)).isFalse()
    }

    @Test fun createStringSet() {
        val stringSet = ArraySet<String>()

        stringSet.put("1")
        stringSet.put("2")
        stringSet.put("3")
        stringSet.put("4")
        stringSet.put("5")
        stringSet.put("6")
        stringSet.put("7")
        stringSet.put("8")
        stringSet.put("9")

        assertThat(stringSet.size).isEqualTo(9);

        assertThat(stringSet.contains("0")).isFalse()
        assertThat(stringSet.contains("1")).isTrue()
        assertThat(stringSet.contains("2")).isTrue()
        assertThat(stringSet.contains("3")).isTrue()
        assertThat(stringSet.contains("4")).isTrue()
        assertThat(stringSet.contains("5")).isTrue()
        assertThat(stringSet.contains("6")).isTrue()
        assertThat(stringSet.contains("7")).isTrue()
        assertThat(stringSet.contains("8")).isTrue()
        assertThat(stringSet.contains("9")).isTrue()
        assertThat(stringSet.contains("10")).isFalse()
    }

    @Test fun putAndRemoveWorksAsExpected() {
        val numericSet = ArraySet<Int>()
        numericSet.put(42)
        assertThat(numericSet.contains(42)).isTrue()
        assertThat(numericSet.size).isEqualTo(1)

        numericSet.remove(42)
        assertThat(numericSet.isEmpty)
    }

    @Test fun clearWorksAsExpected() {
        val numericSet = ArraySet<Int>()
        numericSet.put(1)
        numericSet.put(2)
        numericSet.put(3)
        numericSet.put(4)
        numericSet.put(5)
        numericSet.put(6)
        numericSet.put(7)
        numericSet.put(8)
        numericSet.put(9)

        numericSet.clear()
        assertThat(numericSet.isEmpty)
    }

    @Test fun putWithTheSameKeyTwiceThrowsException() {
        val stringSet = ArraySet<String>()
        stringSet.put("hello")
        stringSet.put("world")

        try {
            stringSet.put("hello")
            Assert.fail("Can't put the same key twice in a set")
        }
        catch (e: IllegalArgumentException) {
        }
    }

    @Test fun putIfLetsYouAddTheSameKeyTwice() {
        val stringSet = ArraySet<String>()
        assertThat(stringSet.putIf("hello")).isTrue()
        assertThat(stringSet.putIf("world")).isTrue()
        assertThat(stringSet.putIf("hello")).isFalse()
    }

    @Test fun removeAKeyNotInTheSetThrowsException() {
        val stringSet = ArraySet<String>()
        stringSet.put("hello")
        stringSet.put("world")

        try {
            stringSet.remove("goodbye")
            Assert.fail("Can't remove a key that's not in the set")
        }
        catch (e: IllegalArgumentException) {
        }
    }

    @Test fun removeIfLetsYouRemoveAKeyThatsNotInTheSet() {
        val stringSet = ArraySet<String>()
        stringSet.put("hello")
        stringSet.put("world")

        assertThat(stringSet.removeIf("hello")).isTrue()
        assertThat(stringSet.removeIf("world")).isTrue()
        assertThat(stringSet.removeIf("goodbye")).isFalse()
        assertThat(stringSet.removeIf("hello")).isFalse()
    }

    //    @Test fun createMapWithStringKeys() {
//        val stringNumericMap = ArrayMap<String, Int>()
//
//        stringNumericMap.put("one", 1)
//        stringNumericMap.put("two", 2)
//        stringNumericMap.put("three", 3)
//        stringNumericMap.put("four", 4)
//        stringNumericMap.put("five", 5)
//        stringNumericMap.put("six", 6)
//        stringNumericMap.put("seven", 7)
//        stringNumericMap.put("eight", 8)
//        stringNumericMap.put("nine", 9)
//
//        assertThat(stringNumericMap["one"]).isEqualTo(1)
//        assertThat(stringNumericMap["two"]).isEqualTo(2)
//        assertThat(stringNumericMap["three"]).isEqualTo(3)
//        assertThat(stringNumericMap["four"]).isEqualTo(4)
//        assertThat(stringNumericMap["five"]).isEqualTo(5)
//        assertThat(stringNumericMap["six"]).isEqualTo(6)
//        assertThat(stringNumericMap["seven"]).isEqualTo(7)
//        assertThat(stringNumericMap["eight"]).isEqualTo(8)
//        assertThat(stringNumericMap["nine"]).isEqualTo(9)
//
//        assertThat(stringNumericMap.remove("one")).isEqualTo(1)
//        assertThat(stringNumericMap.remove("two")).isEqualTo(2)
//        assertThat(stringNumericMap.remove("three")).isEqualTo(3)
//        assertThat(stringNumericMap.remove("four")).isEqualTo(4)
//        assertThat(stringNumericMap.remove("five")).isEqualTo(5)
//        assertThat(stringNumericMap.remove("six")).isEqualTo(6)
//        assertThat(stringNumericMap.remove("seven")).isEqualTo(7)
//        assertThat(stringNumericMap.remove("eight")).isEqualTo(8)
//        assertThat(stringNumericMap.remove("nine")).isEqualTo(9)
//
//        assertThat(stringNumericMap.size).isEqualTo(0)
//    }
//
//    @Test fun clearWorks() {
//        val stringNumericMap = ArrayMap<String, Int>()
//
//        stringNumericMap.put("one", 1)
//        stringNumericMap.put("two", 2)
//        stringNumericMap.put("three", 3)
//        stringNumericMap.put("four", 4)
//        stringNumericMap.put("five", 5)
//        stringNumericMap.put("six", 6)
//        stringNumericMap.put("seven", 7)
//        stringNumericMap.put("eight", 8)
//        stringNumericMap.put("nine", 9)
//        assertThat(stringNumericMap.size).isEqualTo(9)
//
//        stringNumericMap.clear()
//        assertThat(stringNumericMap.size).isEqualTo(0)
//
//        stringNumericMap.put("one", 1)
//        stringNumericMap.put("two", 2)
//        stringNumericMap.put("three", 3)
//        stringNumericMap.put("four", 4)
//        stringNumericMap.put("five", 5)
//        stringNumericMap.put("six", 6)
//        stringNumericMap.put("seven", 7)
//        stringNumericMap.put("eight", 8)
//        stringNumericMap.put("nine", 9)
//        assertThat(stringNumericMap.size).isEqualTo(9)
//    }
//
//    @Test fun getIfWorks() {
//        val numericStringMap = ArrayMap<Int, String>()
//
//        numericStringMap.put(1, "one")
//        numericStringMap.put(2, "two")
//        numericStringMap.put(3, "three")
//        numericStringMap.put(4, "four")
//        numericStringMap.put(5, "five")
//        numericStringMap.put(6, "six")
//        numericStringMap.put(7, "seven")
//        numericStringMap.put(8, "eight")
//        numericStringMap.put(9, "nine")
//
//        var value = numericStringMap.getIf(1)
//        assertThat(value).isEqualTo("one")
//
//        value = numericStringMap.getIf(8)
//        assertThat(value).isEqualTo("eight")
//
//        value = numericStringMap.getIf(99)
//        assertThat(value).isNull()
//    }
//
//    @Test fun arrayMapHandlesHashCollisions() {
//        val hashCollisionMap = ArrayMap<HashCollisionItem, Int>()
//
//        for (i in 0..9) {
//            hashCollisionMap.put(HashCollisionItem(i), i)
//            assertThat(hashCollisionMap.size).isEqualTo(i + 1)
//        }
//
//        for (i in 0..9) {
//            assertThat(hashCollisionMap[HashCollisionItem(i)]).isEqualTo(i)
//        }
//
//        for (i in 9 downTo 0) {
//            assertThat(hashCollisionMap.remove(HashCollisionItem(i))).isEqualTo(i)
//            assertThat(hashCollisionMap.size).isEqualTo(i)
//        }
//    }
//
//    @Test fun arrayMapHandlesNegativeHashCodes() {
//        val negativeHashMap = ArrayMap<NegativeHashItem, Int>()
//
//        for (i in 0..9) {
//            negativeHashMap.put(NegativeHashItem(i), i)
//            assertThat(negativeHashMap.size).isEqualTo(i + 1)
//        }
//
//        for (i in 0..9) {
//            assertThat(negativeHashMap[NegativeHashItem(i)]).isEqualTo(i)
//        }
//
//        for (i in 9 downTo 0) {
//            assertThat(negativeHashMap.remove(NegativeHashItem(i))).isEqualTo(i)
//            assertThat(negativeHashMap.size).isEqualTo(i)
//        }
//    }
//
//    @Test fun arrayMapHandlesGrowingCorrectly() {
//        val numericMap = ArrayMap<Int, Int>(1)
//
//        for (i in 0..9999) {
//            numericMap.put(i, i)
//            assertThat(numericMap.size).isEqualTo(i + 1)
//        }
//
//        for (i in 0..9999) {
//            assertThat(numericMap[i]).isEqualTo(i)
//        }
//    }
//
//    @Test fun removingElementsDoesntBreakProbing() {
//        val hashCollisionMap = ArrayMap<HashCollisionItem, Int>()
//
//        for (i in 0..9) {
//            hashCollisionMap.put(HashCollisionItem(i), i)
//        }
//
//        for (i in 0..4) {
//            hashCollisionMap.remove(HashCollisionItem(i))
//        }
//
//        for (i in 0..4) {
//            assertThat(hashCollisionMap.containsKey(HashCollisionItem(i))).isFalse()
//        }
//
//        for (i in 5..9) {
//            // We can still find items added later into the map even when earlier items were removed
//            assertThat(hashCollisionMap[HashCollisionItem(i)]).isEqualTo(i)
//        }
//
//        for (i in 0..4) {
//            // We can put back the items, and they should reuse existing slots
//            hashCollisionMap.put(HashCollisionItem(i), i)
//        }
//
//        for (i in 0..9) {
//            assertThat(hashCollisionMap.containsKey(HashCollisionItem(i))).isTrue()
//        }
//    }
//
//    /**
//     * Removing elements leaves dead buckets behind. Make sure if we aggressively add and remove
//     * elements, these dead buckets still work fine.
//     */
//    @Test
//    fun removingElementsDoesntBreakGetQuery() {
//
//        val numericMap = ArrayMap<Int, Int>(10)
//        val capacity = numericMap.capacity
//
//        // Add and remove a key for each index of the map. This will effectively leave the map
//        // empty but full of dead spaces (which are saved for probing reasons).
//        for (i in 0 until capacity) {
//            numericMap.put(i, i)
//            numericMap.remove(i)
//        }
//
//        // Adding/removing keys shouldn't have triggered a resize
//        assertThat(numericMap.capacity).isEqualTo(capacity)
//        assertThat(numericMap.size).isEqualTo(0)
//
//        // Checking the value of a key should loop around the whole table once, since the probing will keep encountering
//        // dead spaces. The map should detect this and exit without running into an infinite loop.
//        assertThat(numericMap.containsKey(1)).isFalse()
//    }
//
//    @Test fun replaceCanOnlyReplaceExistingKeys() {
//        val numericStringMap = ArrayMap<Int, String>()
//        numericStringMap.put(1, "oone")
//
//        numericStringMap.replace(1, "one")
//        assertThat(numericStringMap[1]).isEqualTo("one")
//
//        try {
//            numericStringMap.replace(2, "two");
//            Assert.fail("Can only replace key if it is already in the map")
//        }
//        catch (e: IllegalStateException) {
//        }
//    }
//
//    @Test fun putOrReplaceCanBothPutAndReplace() {
//        val numericStringMap = ArrayMap<Int, String>()
//        numericStringMap.put(1, "oone")
//
//        numericStringMap.putOrReplace(1, "one")
//        numericStringMap.putOrReplace(2, "two")
//
//        assertThat(numericStringMap[1]).isEqualTo("one")
//        assertThat(numericStringMap[2]).isEqualTo("two")
//    }
//
//    @Test fun getValuesReturnsCompactListOfValues() {
//        val numericStringMap = ArrayMap<Int, String>(100)
//        numericStringMap.put(1, "one")
//        numericStringMap.put(2, "two")
//        numericStringMap.put(3, "three")
//
//        val values = numericStringMap.getValues()
//        assertThat(values.size).isEqualTo(3)
//        assertThat(values).containsExactly("one", "two", "three")
//    }
}