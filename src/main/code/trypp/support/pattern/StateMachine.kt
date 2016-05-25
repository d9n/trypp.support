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

    /**
     * Method that is called anytime an event is fired which results in a successful state
     * transition.
     */
    interface EventListener<S : Enum<S>, E : Enum<E>> {
        fun run(stateOld: S, event: E, stateNew: S, eventData: Any?)
    }

    /**
     * Method that is called anytime an event is unhandled. Useful for logging.
     */
    interface UnhandledListener<S : Enum<S>, E : Enum<E>> {
        fun run(state: S, event: E, eventData: Any?): Unit
    }


    var currentState = initialState
        private set

    private val eventHandlers = HashMap<StateEventKey, EventHandler<S, E>>()
    private var eventListener: EventListener<S, E> = object : EventListener<S, E> {
        override fun run(stateOld: S, event: E, stateNew: S, eventData: Any?) {}
    }
    private var unhandledListener: UnhandledListener<S, E> = object : UnhandledListener<S, E> {
        override fun run(state: S, event: E, eventData: Any?) {}
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
    fun setUnhandledListener(listener: UnhandledListener<S, E>) {
        unhandledListener = listener
    }

    /**
     * Convenience method for [setUnhandledListener] that takes a lambda for conciseness.
     */
    fun setUnhandledListener(listen: (S, E, Any?) -> Unit) {
        unhandledListener = object : UnhandledListener<S, E> {
            override fun run(state: S, event: E, eventData: Any?) {
                return listen(state, event, eventData)
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
     * Register a listener for whenever we arrive into a new state after an event.
     *
     * The listener will be called on the same thread that [handleEvent] is called on.
     */
    fun registerListener(listener: EventListener<S, E>) {
        eventListener = listener;
    }

    /**
     * Convenience method for [registerListener] that takes a lambda for conciseness.
     */
    fun registerListener(listen: (S, E, S, Any?) -> Unit) {
        eventListener = object : EventListener<S, E> {
            override fun run(stateOld: S, event: E, stateNew: S, eventData: Any?) {
                listen(stateOld, event, stateNew, eventData)
            }
        }
    }

    /**
     * Tell the state machine to handle the passed in event given the current state.
     */
    fun handleEvent(event: E): Boolean {
        return handleEvent(event, null)
    }

    /**
     * Like [handleEvent] but with some additional data that is related to the event.
     */
    fun handleEvent(event: E, eventData: Any?): Boolean {
        val key = keyPool.grabNew()
        key.state = currentState
        key.event = event

        val eventHandler = eventHandlers[key]
        keyPool.free(key)

        val prevState = currentState
        if (eventHandler != null) {
            currentState = eventHandler.run(prevState, event, eventData)
            eventListener.run(prevState, event, currentState, eventData)
            return true
        }
        else {
            unhandledListener.run(currentState, event, eventData)
            return false
        }
    }
}

