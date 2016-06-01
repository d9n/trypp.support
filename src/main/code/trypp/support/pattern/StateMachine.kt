package trypp.support.pattern

import trypp.support.memory.Pool
import trypp.support.memory.Poolable
import trypp.support.pattern.observer.Observable
import java.util.*

/**
 * Encapsulation of a finite state machine.
 *
 * You instantiate a state machine by registering a list of states and a list of events that it can
 * accept in each state.
 *
 * @param S An enumeration type that represents the known states this machine can get into.
 * @param E An enumeration type that represents the known events this machine can accept.
 * @param initialState The first state this machine will start out in
 */
class StateMachine<S : Enum<S>, E : Enum<E>>(private val initialState: S) {

    internal data class StateEventKey(var state: Any?, var event: Any?) : Poolable {
        constructor() : this(null, null)

        override fun reset() {
            state = null
            event = null
        }
    }

    /**
     * Method for handling a state transition. Given a state and an event, run some logic and then
     * return the new state that the state machine should be in.
     */
    interface EventHandler<S : Enum<S>, E : Enum<E>> {
        fun run(state: S, event: E, eventData: Any?): S
    }

    /**
     * Event that is triggered anytime a successful state transition occurs.
     *
     * The event will be run on the same thread [handle] is called on.
     */
    class TransitionEvent<S : Enum<S>, E : Enum<E>> : Observable<((S, E, S, Any?) -> Unit)>() {
        internal operator fun invoke(stateOld: S, event: E, stateNew: S, eventData: Any?) {
            listeners.forEach { it(stateOld, event, stateNew, eventData) }
        }
    }

    /**
     * Method that is called anytime an event is unhandled. Often useful for logging.
     *
     * The event will be run on the same thread [handle] is called on.
     */
    class UnhandledEvent<S : Enum<S>, E : Enum<E>> : Observable<((S, E, Any?) -> Unit)>() {
        internal operator fun invoke(state: S, event: E, eventData: Any?) {
            listeners.forEach { it(state, event, eventData) }
        }
    }

    var currentState = initialState
        private set

    private val eventHandlers = HashMap<StateEventKey, EventHandler<S, E>>()

    val onTransition = TransitionEvent<S, E>()
    val onUnhandled = UnhandledEvent<S, E>()

    private val keyPool = Pool.of(StateEventKey::class, capacity = 1)


    /**
     * Reset this state machine back to its initial state.
     */
    fun reset() {
        currentState = initialState
    }

    /**
     * Register a state and a handler for whenever we receive an event in that state.
     *
     * The handler will be called on the same thread that [handle] is called on.
     *
     * @throws IllegalArgumentException if duplicate state/event pairs are registered
     */
    fun registerTransition(state: S, event: E, handler: EventHandler<S, E>) {
        val key = StateEventKey(state, event)
        if (eventHandlers.containsKey(key)) {
            throw IllegalArgumentException(
                "Duplicate registration of state+event pair: ${key.state}, ${key.event}.")
        }

        eventHandlers.put(key, handler)
    }

    /**
     * Convenience method for [registerTransition] that takes a lambda for conciseness.
     */
    fun registerTransition(state: S, event: E, handle: (S, E, Any?) -> S) {
        registerTransition(state, event, object : EventHandler<S, E> {
            override fun run(state: S, event: E, eventData: Any?): S {
                return handle(state, event, eventData)
            }
        })
    }

    /**
     * Tell the state machine to handle the passed in event given the current state.
     */
    fun handle(event: E): Boolean {
        return handle(event, null)
    }

    /**
     * Like [handle] but with some additional data that is related to the event.
     */
    fun handle(event: E, eventData: Any?): Boolean {
        val key = keyPool.grabNew()
        key.state = currentState
        key.event = event

        val eventHandler = eventHandlers[key]
        keyPool.free(key)

        val prevState = currentState
        if (eventHandler != null) {
            currentState = eventHandler.run(prevState, event, eventData)
            onTransition(prevState, event, currentState, eventData)
            return true
        }
        else {
            onUnhandled(currentState, event, eventData)
            return false
        }
    }
}

