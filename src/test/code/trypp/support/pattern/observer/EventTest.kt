package trypp.support.pattern.observer

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class EventTest {
    class CancelParams {
        var shouldCancel = false;
    }

    class Window {
        val onOpening = Event2<Window, CancelParams>()
        val onOpened = Event1<Window>()

        fun open() {
            val p = CancelParams()
            onOpening(this, p);
            if (!p.shouldCancel) {
                onOpened(this)
            }
        }
    }

    @Test
    fun eventsCanBeUsedToPreventOrAllowBehavior() {
        val w = Window()
        var opened = false
        w.onOpened += { opened = true }
        w.onOpening += { w, params -> params.shouldCancel = true }

        w.open()
        assertThat(opened).isFalse()

        w.onOpening.clearListeners()
        w.open()
        assertThat(opened).isTrue()
    }

    @Test
    fun addThenRemoveListenerWorks() {
        val event = Event0()

        var listenCount = 0
        val listener = event.addListener { listenCount++ }
        event()
        assertThat(listenCount).isEqualTo(1)

        event -= listener
        event()
        assertThat(listenCount).isEqualTo(1)

        event += listener
        event()
        assertThat(listenCount).isEqualTo(2)
    }

    @Test
    fun addThenClearListenersWorks() {
        val event = Event0()

        var listenCount = 0

        event += { ++listenCount }
        event += { listenCount += 10 }
        event()
        assertThat(listenCount).isEqualTo(11)

        event.clearListeners()
        event()
        assertThat(listenCount).isEqualTo(11)
    }

    @Test
    fun event0Works() {
        val event = Event0()
        var handled = false
        event += { handled = true }

        assertThat(handled).isFalse()
        event()
        assertThat(handled).isTrue()
    }

    @Test
    fun event1Works() {
        val event = Event1<String>()
        var value = ""
        event += { value = it }

        assertThat(value).isEmpty()
        event("1")
        assertThat(value).isEqualTo("1")
    }

    @Test
    fun event2Works() {
        val event = Event2<String, String>()
        var value = ""
        event += { s1, s2 -> value = s1 + s2 }

        assertThat(value).isEmpty()
        event("1", "2")
        assertThat(value).isEqualTo("12")
    }

    @Test
    fun event3Works() {
        val event = Event3<String, String, String>()
        var value = ""
        event += { s1, s2, s3 -> value = s1 + s2 + s3 }

        assertThat(value).isEmpty()
        event("1", "2", "3")
        assertThat(value).isEqualTo("123")
    }

    @Test
    fun event4Works() {
        val event = Event4<String, String, String, String>()
        var value = ""
        event += { s1, s2, s3, s4 -> value = s1 + s2 + s3 + s4 }

        assertThat(value).isEmpty()
        event("1", "2", "3", "4")
        assertThat(value).isEqualTo("1234")
    }

    @Test
    fun event5Works() {
        val event = Event5<String, String, String, String, String>()
        var value = ""
        event += { s1, s2, s3, s4, s5 -> value = s1 + s2 + s3 + s4 + s5 }

        assertThat(value).isEmpty()
        event("1", "2", "3", "4", "5")
        assertThat(value).isEqualTo("12345")
    }

    @Test
    fun event6Works() {
        val event = Event6<String, String, String, String, String, String>()
        var value = ""
        event += { s1, s2, s3, s4, s5, s6 -> value = s1 + s2 + s3 + s4 + s5 + s6 }

        assertThat(value).isEmpty()
        event("1", "2", "3", "4", "5", "6")
        assertThat(value).isEqualTo("123456")
    }

    @Test
    fun event7Works() {
        val event = Event7<String, String, String, String, String, String, String>()
        var value = ""
        event += { s1, s2, s3, s4, s5, s6, s7 -> value = s1 + s2 + s3 + s4 + s5 + s6 + s7 }

        assertThat(value).isEmpty()
        event("1", "2", "3", "4", "5", "6", "7")
        assertThat(value).isEqualTo("1234567")
    }

    @Test
    fun event8Works() {
        val event = Event8<String, String, String, String, String, String, String, String>()
        var value = ""
        event += { s1, s2, s3, s4, s5, s6, s7, s8 -> value = s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 }

        assertThat(value).isEmpty()
        event("1", "2", "3", "4", "5", "6", "7", "8")
        assertThat(value).isEqualTo("12345678")
    }

    @Test
    fun event9Works() {
        val event = Event9<String, String, String, String, String, String, String, String, String>()
        var value = ""
        event += { s1, s2, s3, s4, s5, s6, s7, s8, s9 -> value = s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 }

        assertThat(value).isEmpty()
        event("1", "2", "3", "4", "5", "6", "7", "8", "9")
        assertThat(value).isEqualTo("123456789")
    }
}