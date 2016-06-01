package trypp.support.opt

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test

class OptIntTest {

    @Test fun createOptionalWithNoValueWorks() {
        val floatOpt = OptInt.withNoValue()
        assertThat(floatOpt.hasValue()).isFalse()
    }

    @Test fun createOptionalWithValueWorks() {
        val floatOpt = OptInt.of(DUMMY_VALUE)
        assertThat(floatOpt.hasValue()).isTrue()
        assertThat(floatOpt.get()).isEqualTo(DUMMY_VALUE)
    }

    @Test fun settingOptionalValueWorks() {
        val floatOpt = OptInt.withNoValue()
        val zeroFloatOpt = OptInt.of(0)
        assertThat(floatOpt.hasValue()).isFalse()

        floatOpt.set(DUMMY_VALUE)
        assertThat(floatOpt.hasValue()).isTrue()
        assertThat(floatOpt.get()).isEqualTo(DUMMY_VALUE)

        floatOpt.clear()
        assertThat(floatOpt.hasValue()).isFalse()

        floatOpt.setFrom(zeroFloatOpt)
        assertThat(floatOpt.hasValue()).isTrue()
        assertThat(floatOpt.get()).isEqualTo(0)
    }

    @Test fun clearOptionalWorks() {
        val floatOpt = OptInt.of(DUMMY_VALUE)
        assertThat(floatOpt.hasValue()).isTrue()

        floatOpt.clear()
        assertThat(floatOpt.hasValue()).isFalse()
    }

    @Test fun getValueOrWorks() {
        val floatOpt = OptInt.of(DUMMY_VALUE)
        assertThat(floatOpt.getOr(0)).isEqualTo(DUMMY_VALUE)

        floatOpt.clear()
        assertThat(floatOpt.getOr(0)).isEqualTo(0)
    }

    @Test fun testOptionalEquality() {
        val floatOpt = OptInt.of(DUMMY_VALUE)
        val floatOpt2 = OptInt.of(DUMMY_VALUE)
        val emptyValue = OptInt.withNoValue()

        assertThat(floatOpt).isEqualTo(floatOpt2)
        assertThat(floatOpt).isNotEqualTo(emptyValue)
        assertThat(floatOpt.hashCode()).isEqualTo(floatOpt2.hashCode())
    }

    @Test fun getValueWithoutValueThrowsException() {
        val emptyfloatOpt = OptInt.withNoValue()
        try {
            emptyfloatOpt.get()
            Assert.fail("Can't get a value from a valueless optional")
        }
        catch (e: IllegalStateException) {}
    }

    companion object {
        private val DUMMY_VALUE = 1234
    }
}