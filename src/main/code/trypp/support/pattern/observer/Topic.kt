package trypp.support.pattern.observer

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

/**
 * A subject / observer pattern. Create a topic based on an interface type and then call
 * `topic.broadcast.method(...)` to trigger all registered listeners.
 */
class Topic<T : Any>(type: KClass<T>) : Observable<T>() {
    private val DELEGATE_HANDLER = InvocationHandler { proxy, method, args ->
        assert(method != null)
        listeners.forEach { l ->
            val m = l.javaClass.methods.find {
                it.name == method.name && it.parameterCount == method.parameterCount
            }!!
            m.invoke(l, *args)
        }
    }

    @Suppress("UNCHECKED_CAST")
    val broadcast: T = Proxy.newProxyInstance(type.java.classLoader,
                                              arrayOf(type.java),
                                              DELEGATE_HANDLER) as T
}
