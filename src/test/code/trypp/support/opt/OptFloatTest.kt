package trypp.support.opt

import org.testng.annotations.Test

import com.google.common.truth.Truth.assertThat
import org.testng.Assert

class OptFloatTest {

    @Test fun createOptionalWithNoValueWorks() {
        val floatOpt = OptFloat.withNoValue()
        assertThat(floatOpt.hasValue()).isFalse()
    }

    @Test fun createOptionalWithValueWorks() {
        val floatOpt = OptFloat.of(DUMMY_VALUE)
        assertThat(floatOpt.hasValue()).isTrue()
        assertThat(floatOpt.getValue()).isWithin(0f).of(DUMMY_VALUE)
    }

    @Test fun settingOptionalValueWorks() {
        val floatOpt = OptFloat.withNoValue()
        val zeroFloatOpt = OptFloat.of(0f)
        assertThat(floatOpt.hasValue()).isFalse()

        floatOpt.set(DUMMY_VALUE)
        assertThat(floatOpt.hasValue()).isTrue()
        assertThat(floatOpt.getValue()).isWithin(0f).of(DUMMY_VALUE)

        floatOpt.clear()
        assertThat(floatOpt.hasValue()).isFalse()

        floatOpt.setFrom(zeroFloatOpt)
        assertThat(floatOpt.hasValue()).isTrue()
        assertThat(floatOpt.getValue()).isWithin(0f).of(0f)
    }

    @Test fun clearOptionalWorks() {
        val floatOpt = OptFloat.of(DUMMY_VALUE)
        assertThat(floatOpt.hasValue()).isTrue()

        floatOpt.clear()
        assertThat(floatOpt.hasValue()).isFalse()
    }

    @Test fun getValueOrWorks() {
        val floatOpt = OptFloat.of(DUMMY_VALUE)
        assertThat(floatOpt.getValueOr(0f)).isWithin(0f).of(DUMMY_VALUE)

        floatOpt.clear()
        assertThat(floatOpt.getValueOr(0f)).isWithin(0f).of(0f)
    }

    @Test fun testOptionalEquality() {
        val floatOpt = OptFloat.of(DUMMY_VALUE)
        val floatOpt2 = OptFloat.of(DUMMY_VALUE)
        val emptyValue = OptFloat.withNoValue()

        assertThat(floatOpt).isEqualTo(floatOpt2)
        assertThat(floatOpt).isNotEqualTo(emptyValue)
        assertThat(floatOpt.hashCode()).isEqualTo(floatOpt2.hashCode())
    }

    @Test fun getValueWithoutValueThrowsException() {
        val emptyfloatOpt = OptFloat.withNoValue()
        try {
            emptyfloatOpt.getValue()
            Assert.fail("Can't get a value from a valueless optional")
        }
        catch (e: IllegalStateException) {}
    }

    companion object {
        private val DUMMY_VALUE = -1234.5f
    }
}