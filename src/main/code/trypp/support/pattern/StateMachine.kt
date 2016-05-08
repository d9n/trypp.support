package trypp.support.pattern

import trypp.support.memory.Pool
import trypp.support.memory.Poolable
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

    var currentState = initialState
        private set

    private val eventHandlers = HashMap<StateEventKey, EventHandler<S, E>>()
    private var fallbackHandler: EventHandler<S, E> = object : EventHandler<S, E> {
        override fun run(state: S, event: E, eventData: Any?): S {
            return state
        }
    }

    private val keyPool = Pool.of(StateEventKey::class, capacity = 1)


    /**
     * Reset this state machine back to its initial state.
     */
    fun reset() {
        currentState = initialState
    }

    /**
     * Set a handler which will be called in the case an event is triggered in some state that
     * doesn't have an [EventHandler] registered for it. This is usually a good place to log a
     * warning.
     *
     * The handler will be called on the same thread that [handleEvent] is called on.
     */
    fun setFallbackHandler(handler: EventHandler<S, E>) {
        fallbackHandler = handler
    }

    /**
     * Convenience method for [setFallbackHandler] that takes a lambda for conciseness.
     */
    fun setFallbackHandler(handle: (S, E, Any?) -> S) {
        fallbackHandler = object : EventHandler<S, E> {
            override fun run(state: S, event: E, eventData: Any?): S {
                return handle(state, event, eventData)
            }
        }
    }

    /**
     * Register a state and a handler for whenever we receive an event in that state.
     *
     * The handler will be called on the same thread that [handleEvent] is called on.
     *
     * @throws IllegalArgumentException if duplicate state/event pairs are registered
     */
    fun registerEvent(state: S, event: E, handler: EventHandler<S, E>) {
        val key = StateEventKey(state, event)
        if (eventHandlers.containsKey(key)) {
            throw IllegalArgumentException(
                "Duplicate registration of state+event pair: ${key.state}, ${key.event}.")
        }

        eventHandlers.put(key, handler)
    }

    /**
     * Convenience method for [registerEvent] that takes a lambda for conciseness.
     */
    fun registerEvent(state: S, event: E, handle: (S, E, Any?) -> S) {
        registerEvent(state, event, object : EventHandler<S, E> {
            override fun run(state: S, event: E, eventData: Any?): S {
                return handle(state, event, eventData)
            }
        })
    }

    /**
     * Tell the state machine to handle the passed in event given the current state.
     */
    fun handleEvent(event: E) {
        handleEvent(event, null)
    }

    /**
     * Like [handleEvent] but with some additional data that is related to the event.
     */
    fun handleEvent(event: E, eventData: Any?) {
        val key = keyPool.grabNew()
        key.state = currentState
        key.event = event

        val eventHandler = eventHandlers[key]
        keyPool.free(key)

        if (eventHandler == null) {
            currentState = fallbackHandler.run(currentState, event, eventData)
            return
        }

        currentState = eventHandler.run(currentState, event, eventData)
    }
}

