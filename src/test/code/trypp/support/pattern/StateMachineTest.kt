package trypp.support.pattern

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import trypp.support.pattern.StateMachine.UnhandledListener
import kotlin.properties.Delegates

class StateMachineTest {

    enum class TestState {
        A,
        B,
        C
    }

    enum class TestEvent {
        A_TO_B,
        A_TO_C,
        B_TO_C,
        ANY_TO_A,
        UNREGISTERED_EVENT,
        EVENT_WITH_DATA
    }

    class TestUnhandledListener : UnhandledListener<TestState, TestEvent> {
        var ranCount = 0
            private set

        override fun run(state: TestState, event: TestEvent, eventData: Any?) {
            ranCount++
        }
    }

    private var fsm: StateMachine<TestState, TestEvent> by Delegates.notNull()
    private var unhandledListener: TestUnhandledListener by Delegates.notNull()

    @BeforeMethod fun setUp() {
        fsm = StateMachine(TestState.A)
        unhandledListener = TestUnhandledListener()

        fsm.registerEvent(TestState.A, TestEvent.A_TO_B, { s, e, data -> TestState.B })
        fsm.registerEvent(TestState.A, TestEvent.A_TO_C, { s, e, data -> TestState.C })
        fsm.registerEvent(TestState.B, TestEvent.B_TO_C, { s, e, data -> TestState.C })
        fsm.registerEvent(TestState.B, TestEvent.ANY_TO_A, { s, e, data -> TestState.A })
        fsm.registerEvent(TestState.C, TestEvent.ANY_TO_A, { s, e, data -> TestState.A })
        fsm.setUnhandledListener(unhandledListener)
    }

    @Test fun stateMachineStartsInStateSetInConstructor() {
        val fsmA = StateMachine<TestState, TestEvent>(TestState.A)
        val fsmC = StateMachine<TestState, TestEvent>(TestState.C)

        assertThat(fsmA.currentState).isEqualTo(TestState.A)
        assertThat(fsmC.currentState).isEqualTo(TestState.C)
    }

    @Test fun testStateMachineChangesStateAsExpected() {
        assertThat(fsm.currentState).isEqualTo(TestState.A)
        fsm.handleEvent(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.B)
        fsm.handleEvent(TestEvent.B_TO_C)
        assertThat(fsm.currentState).isEqualTo(TestState.C)
        fsm.handleEvent(TestEvent.ANY_TO_A)
        assertThat(fsm.currentState).isEqualTo(TestState.A)
        fsm.handleEvent(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.B)
        fsm.handleEvent(TestEvent.B_TO_C)
        assertThat(fsm.currentState).isEqualTo(TestState.C)

        fsm.reset()
        assertThat(fsm.currentState).isEqualTo(TestState.A)
    }

    @Test fun unhandledListenerCatchesUnregisteredEvent() {
        assertThat(unhandledListener.ranCount).isEqualTo(0)

        fsm.handleEvent(TestEvent.A_TO_B)
        assertThat(unhandledListener.ranCount).isEqualTo(0)

        fsm.handleEvent(TestEvent.UNREGISTERED_EVENT)
        assertThat(unhandledListener.ranCount).isEqualTo(1)

        assertThat(fsm.currentState).isEqualTo(TestState.B)
        fsm.handleEvent(TestEvent.A_TO_B)
        assertThat(unhandledListener.ranCount).isEqualTo(2)
    }

    @Test fun duplicateRegistrationThrowsException() {
        try {
            fsm.registerEvent(TestState.A, TestEvent.A_TO_B, { s, e, data -> TestState.B })
            Assert.fail("Duplicate event registration is not allowed")
        }
        catch (e: IllegalArgumentException) {
        }
    }

    @Test fun eventDataIsPassedOn() {
        val dummyData = Object()

        var handlerCalled = false
        fsm.registerEvent(TestState.A, TestEvent.EVENT_WITH_DATA, { s, e, data ->
            handlerCalled = true
            assertThat(data).isEqualTo(dummyData)
            s // Stay in same state
        })

        assertThat(handlerCalled).isFalse()
        fsm.handleEvent(TestEvent.EVENT_WITH_DATA, dummyData)
        assertThat(handlerCalled).isTrue()
    }

    @Test fun eventListenerWorksAsExpected() {
        val dummyData = Object()

        var listenerCalled = false
        fsm.registerEvent(TestState.A, TestEvent.EVENT_WITH_DATA, { s, e, d -> TestState.B })
        fsm.registerListener({ s1, evt, s2, data ->
             listenerCalled = true
             assertThat(s1).isEqualTo(TestState.A)
             assertThat(evt).isEqualTo(TestEvent.EVENT_WITH_DATA)
             assertThat(s2).isEqualTo(TestState.B)
             assertThat(data).isEqualTo(dummyData)
         })

        assertThat(listenerCalled).isFalse()
        fsm.handleEvent(TestEvent.EVENT_WITH_DATA, dummyData)
        assertThat(listenerCalled).isTrue()
    }

    @Test fun setUnhandledListenerByLambdaWorksAsExpected() {
        var listenerCalled = false
        fsm.setUnhandledListener { s, e, d -> listenerCalled = true }

        assertThat(listenerCalled).isFalse()
        fsm.handleEvent(TestEvent.UNREGISTERED_EVENT)
        assertThat(listenerCalled).isTrue()
    }
}