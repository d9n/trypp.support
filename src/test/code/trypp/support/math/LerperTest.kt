package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test
import trypp.support.math.Lerper.Mode
import trypp.support.time.Duration

class LerperTest {

    @Test fun testRunOnceLerper() {
        val lerper = Lerper(Lerper.LINEAR, Duration.ofSeconds(10f))

        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(0f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(80f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(100f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(100f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(100f)
    }

    @Test fun testLoopingLerper() {
        val lerper = Lerper()
        lerper.set(Lerper.LINEAR, Duration.ofSeconds(10f), Mode.LOOP)

        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(0f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(80f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(60f) // 160 -> 60
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(40f) // 240 -> 40
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(20f) // 320 -> 20
    }

    @Test fun testBouncingLooper() {
        val lerper = Lerper()
        lerper.set(Lerper.LINEAR, Duration.ofSeconds(10f), Mode.BOUNCE)

        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(0f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(80f)
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(40f) // 80 -> 100 -> 40
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(40f) // 40 -> 0 -> 40
        lerper.update(Duration.ofSeconds(8f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(80f) // 40 -> 100 -> 80
    }

    @Test fun testReverseLerper() {
        val lerper = Lerper()
        lerper.set(Lerper.REVERSE, Duration.ofSeconds(10f))

        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(100f)
        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(60f)
        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(20f)
        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(0f)
    }
}