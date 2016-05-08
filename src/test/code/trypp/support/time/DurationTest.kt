package trypp.support.time

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class DurationTest {

    @Test fun testFromSeconds() {
        val duration = Duration.fromSeconds(90f)

        assertThat(duration.getMinutes()).isWithin(0f).of(1.5f)
        assertThat(duration.getSeconds()).isWithin(0f).of(90f)
        assertThat(duration.getMilliseconds()).isWithin(0f).of(90000f)
    }

    @Test fun testFromMinutes() {
        val duration = Duration.fromMinutes(1.5f)

        assertThat(duration.getMinutes()).isWithin(0f).of(1.5f)
        assertThat(duration.getSeconds()).isWithin(0f).of(90f)
        assertThat(duration.getMilliseconds()).isWithin(0f).of(90000f)
    }

    @Test fun testFromMilliseconds() {
        val duration = Duration.fromMilliseconds(90000f)

        assertThat(duration.getMinutes()).isWithin(0f).of(1.5f)
        assertThat(duration.getSeconds()).isWithin(0f).of(90f)
        assertThat(duration.getMilliseconds()).isWithin(0f).of(90000f)
    }

    @Test fun testFromOtherDuration() {
        val otherDuration = Duration.fromSeconds(9000f)
        val duration = Duration.from(otherDuration)

        assertThat(duration.getSeconds()).isWithin(0f).of(9000f)
    }

    @Test fun testEmptyDuration() {
        val duration = Duration.zero()

        assertThat(duration.getSeconds()).isWithin(0f).of(0f)
        assertThat(duration.isZero).isTrue()
    }

    @Test fun testSetMethods() {
        val duration = Duration.zero()

        duration.setSeconds(3f)
        assertThat(duration.getSeconds()).isWithin(0f).of(3f)

        duration.setMinutes(4f)
        assertThat(duration.getMinutes()).isWithin(0f).of(4f)

        duration.setMilliseconds(5f)
        assertThat(duration.getMilliseconds()).isWithin(0f).of(5f)

        val otherDuration = Duration.fromSeconds(6f)
        duration.setFrom(otherDuration)
        assertThat(duration.getSeconds()).isWithin(0f).of(6f)

        duration.setZero()
        assertThat(duration.getSeconds()).isWithin(0f).of(0f)
    }

    @Test fun testAddMethods() {
        val duration = Duration.zero()

        duration.addSeconds(1f)
        assertThat(duration.getSeconds()).isWithin(0f).of(1f)

        duration.addMilliseconds(500f)
        assertThat(duration.getSeconds()).isWithin(0f).of(1.5f)

        duration.addMinutes(2f)
        assertThat(duration.getSeconds()).isWithin(.1f).of(121.5f)

        val otherDuration = Duration.fromMilliseconds(1500f)
        duration.add(otherDuration)
        assertThat(duration.getSeconds()).isWithin(.1f).of(123f)
    }

    @Test fun testSubtractMethods() {
        val duration = Duration.fromMinutes(10f)

        duration.subtract(Duration.fromMinutes(3f))
        assertThat(duration.getMinutes()).isWithin(0f).of(7f)

        duration.subtractMinutes(5f)
        assertThat(duration.getMinutes()).isWithin(0f).of(2f)

        duration.subtractSeconds(30f)
        assertThat(duration.getMinutes()).isWithin(0f).of(1.5f)

        duration.subtractMilliseconds(1000f)
        assertThat(duration.getMinutes()).isWithin(.1f).of(1.4f)
    }

    @Test fun settingNegativeDurationBecomesZero() {
        assertThat(Duration.fromSeconds(-5f).getSeconds()).isWithin(0f).of(0f)
    }

}