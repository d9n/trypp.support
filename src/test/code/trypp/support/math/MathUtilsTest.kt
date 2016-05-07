package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class MathUtilsTest {

    @Test
    fun integerClampWorks() {
        assertThat(clamp(-2, 1, 5)).isEqualTo(1)
        assertThat(clamp(1, 1, 5)).isEqualTo(1)
        assertThat(clamp(5, 1, 5)).isEqualTo(5)
        assertThat(clamp(3, 1, 5)).isEqualTo(3)
        assertThat(clamp(8, 1, 5)).isEqualTo(5)
    }

    @Test
    fun floatClampWorks() {
        assertThat(clamp(-2.5f, 1.3f, 5.8f)).isWithin(0f).of(1.3f)
        assertThat(clamp(1.3f, 1.3f, 5.8f)).isWithin(0f).of(1.3f)
        assertThat(clamp(3.2f, 1.3f, 5.8f)).isWithin(0f).of(3.2f)
        assertThat(clamp(5.8f, 1.3f, 5.8f)).isWithin(0f).of(5.8f)
        assertThat(clamp(9.1f, 1.3f, 5.8f)).isWithin(0f).of(5.8f)
    }

    @Test
    fun testLog2() {
        assertThat(log2(1)).isEqualTo(0)
        assertThat(log2(2)).isEqualTo(1)
        assertThat(log2(3)).isEqualTo(2)
        assertThat(log2(4)).isEqualTo(2)
        assertThat(log2(5)).isEqualTo(3)
        assertThat(log2(6)).isEqualTo(3)
        assertThat(log2(7)).isEqualTo(3)
        assertThat(log2(8)).isEqualTo(3)
        assertThat(log2(9)).isEqualTo(4)

        assertThat(log2(1073741824)).isEqualTo(30)
    }

    @Test
    fun testIsPowerOf2() {
        assertThat(isPowerOfTwo(1)).isTrue()
        assertThat(isPowerOfTwo(2)).isTrue()
        assertThat(isPowerOfTwo(3)).isFalse()
        assertThat(isPowerOfTwo(4)).isTrue()
        assertThat(isPowerOfTwo(5)).isFalse()
        assertThat(isPowerOfTwo(6)).isFalse()
        assertThat(isPowerOfTwo(7)).isFalse()
        assertThat(isPowerOfTwo(8)).isTrue()
        assertThat(isPowerOfTwo(9)).isFalse()

        assertThat(isPowerOfTwo(1073741823)).isFalse()
        assertThat(isPowerOfTwo(1073741824)).isTrue()
        assertThat(isPowerOfTwo(1073741825)).isFalse()
    }
}