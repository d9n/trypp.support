package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test
import trypp.support.time.Duration

class LerperTest {

    @Test fun testSimpleLerper() {
        val lerper = Lerper(Lerp.LINEAR, Duration.ofSeconds(10f))

        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(0f)
        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.finished).isFalse()
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(40f)

        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(80f)
        assertThat(lerper.finished).isFalse()

        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(100f)
        assertThat(lerper.finished).isTrue()
    }

    @Test fun testReverseLerper() {
        val lerper = Lerper()
        lerper.set(Lerp.reverse(Lerp.LINEAR), Duration.ofSeconds(10f))

        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(100f)
        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.finished).isFalse()
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(60f)

        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(20f)
        assertThat(lerper.finished).isFalse()

        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(0f)
        assertThat(lerper.finished).isTrue()
    }

    @Test fun testBouncingLooper() {
        val lerper = Lerper()
        lerper.set(Lerp.bounce(Lerp.LINEAR), Duration.ofSeconds(10f))

        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(0f)
        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.finished).isFalse()
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(80f)

        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(.1f).of(40f)
        assertThat(lerper.finished).isFalse()

        lerper.update(Duration.ofSeconds(4f))
        assertThat(lerper.lerp(0f, 100f)).isWithin(0f).of(0f)
        assertThat(lerper.finished).isTrue()
    }
}