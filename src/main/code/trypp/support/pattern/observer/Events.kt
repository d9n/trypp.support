package trypp.support.pattern.observer

/**
 * A simple event with no arguments.
 *
 * (Consider preferring [Event1] with a sender parameter as its first argument.)
 */
class Event0 : Observable<(() -> Unit)>() {
    operator fun invoke() {
        listeners.forEach { it() }
    }
}

/**
 * An event which takes a single argument.
 *
 * A highly recommended pattern is to include the observer object's sender as the first param, e.g.
 *
 * ```
 * class Sender {
 *    val onSomethingHappened = Event1<Sender>()
 *
 *    fun doSomething() {
 *        onSomethingHappened(this);
 *    }
 * }
 * ```
 */
class Event1<T1> : Observable<((T1) -> Unit)>() {
    operator fun invoke(arg1: T1) {
        listeners.forEach { it(arg1) }
    }
}

/**
 * An event which takes two arguments.
 */
class Event2<T1, T2> : Observable<((T1, T2) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2) {
        listeners.forEach { it(arg1, arg2) }
    }
}

/**
 * An event which takes three arguments.
 */
class Event3<T1, T2, T3> : Observable<((T1, T2, T3) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2, arg3: T3) {
        listeners.forEach { it(arg1, arg2, arg3) }
    }
}

/**
 * An event which takes four arguments.
 */
class Event4<T1, T2, T3, T4> : Observable<((T1, T2, T3, T4) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4) {
        listeners.forEach { it(arg1, arg2, arg3, arg4) }
    }
}


/**
 * An event which takes five arguments.
 */
class Event5<T1, T2, T3, T4, T5> : Observable<((T1, T2, T3, T4, T5) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5) {
        listeners.forEach { it(arg1, arg2, arg3, arg4, arg5) }
    }
}

/**
 * An event which takes six arguments.
 */
class Event6<T1, T2, T3, T4, T5, T6> : Observable<((T1, T2, T3, T4, T5, T6) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6) {
        listeners.forEach { it(arg1, arg2, arg3, arg4, arg5, arg6) }
    }
}

/**
 * An event which takes seven arguments.
 */
class Event7<T1, T2, T3, T4, T5, T6, T7> : Observable<((T1, T2, T3, T4, T5, T6, T7) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7) {
        listeners.forEach { it(arg1, arg2, arg3, arg4, arg5, arg6, arg7) }
    }
}

/**
 * An event which takes eight arguments.
 */
class Event8<T1, T2, T3, T4, T5, T6, T7, T8> : Observable<((T1, T2, T3, T4, T5, T6, T7, T8) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7,
                        arg8: T8) {
        listeners.forEach { it(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) }
    }
}


/**
 * An event which takes nine arguments.
 */
class Event9<T1, T2, T3, T4, T5, T6, T7, T8, T9> : Observable<((T1, T2, T3, T4, T5, T6, T7, T8, T9) -> Unit)>() {
    operator fun invoke(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7,
                        arg8: T8, arg9: T9) {
        listeners.forEach { it(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) }
    }
}
