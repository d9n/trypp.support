package trypp.support.pattern.observer

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class TopicTest {

    class CancelParams {
        var shouldCancel = false;
    }

    interface OpenEvents {
        fun onOpening(sender: Window, params: CancelParams)
        fun onOpened(sender: Window)
    }

    class Window {
        val openEvents = Topic(OpenEvents::class)

        fun open() {
            val p = CancelParams()
            openEvents.broadcast.onOpening(this, p)
            if (!p.shouldCancel) {
                openEvents.broadcast.onOpened(this)
            }
        }
    }

    @Test
    fun topicsCanBeUsedToPreventOrAllowBehavior() {
        val w = Window()
        var allowOpen = false
        var opened = false

        w.openEvents += object : OpenEvents {
            override fun onOpening(sender: Window, params: CancelParams) {
                params.shouldCancel = !allowOpen
            }

            override fun onOpened(sender: Window) {
                opened = true
            }
        }

        w.open()
        assertThat(opened).isFalse()

        allowOpen = true
        w.open()
        assertThat(opened).isTrue()
    }
}
