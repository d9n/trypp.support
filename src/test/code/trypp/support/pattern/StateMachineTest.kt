package trypp.support.pattern

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
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

    private var fsm: StateMachine<TestState, TestEvent> by Delegates.notNull()

    @BeforeMethod fun setUp() {
        fsm = StateMachine(TestState.A)

        fsm.registerTransition(TestState.A, TestEvent.A_TO_B, { s, e, data -> TestState.B })
        fsm.registerTransition(TestState.A, TestEvent.A_TO_C, { s, e, data -> TestState.C })
        fsm.registerTransition(TestState.B, TestEvent.B_TO_C, { s, e, data -> TestState.C })
        fsm.registerTransition(TestState.B, TestEvent.ANY_TO_A, { s, e, data -> TestState.A })
        fsm.registerTransition(TestState.C, TestEvent.ANY_TO_A, { s, e, data -> TestState.A })
        // Tests must freeze fsm before using it!
    }

    @Test fun stateMachineStartsInStateSetInConstructor() {
        val fsmA = StateMachine<TestState, TestEvent>(TestState.A)
        val fsmC = StateMachine<TestState, TestEvent>(TestState.C)

        assertThat(fsmA.currentState).isEqualTo(TestState.A)
        assertThat(fsmC.currentState).isEqualTo(TestState.C)
    }

    @Test fun testStateMachineChangesStateAsExpected() {
        fsm.freeze()

        assertThat(fsm.currentState).isEqualTo(TestState.A)
        fsm.handle(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.B)
        fsm.handle(TestEvent.B_TO_C)
        assertThat(fsm.currentState).isEqualTo(TestState.C)
        fsm.handle(TestEvent.ANY_TO_A)
        assertThat(fsm.currentState).isEqualTo(TestState.A)
        fsm.handle(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.B)
        fsm.handle(TestEvent.B_TO_C)
        assertThat(fsm.currentState).isEqualTo(TestState.C)

        fsm.reset()
        assertThat(fsm.currentState).isEqualTo(TestState.A)
        assertThat(fsm.frozen).isFalse()

        // Resetting should clear previously registered listeners
        fsm.freeze()
        fsm.handle(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.A)
    }

    @Test fun freezeRemovesRegisteredTransitions() {
        fsm.freeze()
        assertThat(fsm.currentState).isEqualTo(TestState.A)
        fsm.handle(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.B)

        fsm.reset()
        fsm.freeze()

        assertThat(fsm.currentState).isEqualTo(TestState.A)
        fsm.handle(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.A)
    }

    @Test fun unhandledListenerCatchesUnregisteredEvent() {
        fsm.freeze()

        var ranCount = 0
        fsm.onUnhandled += { s, e, d -> ranCount++ }
        assertThat(ranCount).isEqualTo(0)

        fsm.handle(TestEvent.A_TO_B)
        assertThat(ranCount).isEqualTo(0)

        fsm.handle(TestEvent.UNREGISTERED_EVENT)
        assertThat(ranCount).isEqualTo(1)

        assertThat(fsm.currentState).isEqualTo(TestState.B)
        fsm.handle(TestEvent.A_TO_B)
        assertThat(ranCount).isEqualTo(2)
    }

    @Test fun eventDataIsPassedOn() {
        val dummyData = Object()

        var handlerCalled = false
        fsm.registerTransition(TestState.A, TestEvent.EVENT_WITH_DATA, { s, e, data ->
            handlerCalled = true
            assertThat(data).isEqualTo(dummyData)
            s // Stay in same state
        })
        fsm.freeze()

        assertThat(handlerCalled).isFalse()
        fsm.handle(TestEvent.EVENT_WITH_DATA, dummyData)
        assertThat(handlerCalled).isTrue()
    }

    @Test fun eventListenerWorksAsExpected() {
        val dummyData = Object()

        var listenerCalled = false
        fsm.registerTransition(TestState.A, TestEvent.EVENT_WITH_DATA, { s, e, d -> TestState.B })
        fsm.onTransition += ({ s1, evt, s2, data ->
             listenerCalled = true
             assertThat(s1).isEqualTo(TestState.A)
             assertThat(evt).isEqualTo(TestEvent.EVENT_WITH_DATA)
             assertThat(s2).isEqualTo(TestState.B)
             assertThat(data).isEqualTo(dummyData)
         })
        fsm.freeze()

        assertThat(listenerCalled).isFalse()
        fsm.handle(TestEvent.EVENT_WITH_DATA, dummyData)
        assertThat(listenerCalled).isTrue()
    }

    @Test fun duplicateRegistrationThrowsException() {
        try {
            fsm.registerTransition(TestState.A, TestEvent.A_TO_B, { s, e, data -> TestState.B })
            Assert.fail("Duplicate event registration is not allowed")
        }
        catch (e: IllegalArgumentException) {
        }
    }

    @Test fun freezingTwiceThrowsException() {
        fsm.freeze()
        try {
            fsm.freeze()
            Assert.fail("Cannot freeze a frozen state machine")
        }
        catch (e: IllegalStateException) {
        }
    }

    @Test fun cannotRegisterTransitionAfterFrozen() {
        fsm.freeze()
        try {
            assertThat(fsm.frozen).isTrue()
            fsm.registerTransition(TestState.A, TestEvent.EVENT_WITH_DATA, { s, e, d -> TestState.A })
            Assert.fail("Cannot register transition on frozen state machine")
        }
        catch (e: IllegalStateException) {
        }
    }

    @Test fun handleCannotBeCalledBeforeFreezing() {
        try {
            assertThat(fsm.frozen).isFalse()
            fsm.handle(TestEvent.A_TO_B)
            Assert.fail("Cannot fire event on non-frozen state machine")
        }
        catch (e: IllegalStateException) {
        }

        fsm.freeze()
        fsm.handle(TestEvent.A_TO_B)
        assertThat(fsm.currentState).isEqualTo(TestState.B)
    }
}
