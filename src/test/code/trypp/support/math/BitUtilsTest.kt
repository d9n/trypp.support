package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class BitUtilsTest {

    @Test fun testRequireSingleBit() {
        for (i in 0..31) {
            assertThat(BitUtils.hasSingleBit(1 shl i)).isTrue()
        }

        assertThat(BitUtils.hasSingleBit(3)).isFalse()
        assertThat(BitUtils.hasSingleBit(11)).isFalse()
        assertThat(BitUtils.hasSingleBit(99381)).isFalse()
    }

    @Test fun testGetBitIndex() {
        for (i in 0..31) {
            assertThat(BitUtils.getBitIndex(1 shl i)).isEqualTo(i)
        }
    }
}