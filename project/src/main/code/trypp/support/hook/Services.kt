package trypp.support.hook

import trypp.support.hook.exceptions.BadServiceClassException
import trypp.support.hook.exceptions.ServiceExistsException
import trypp.support.hook.exceptions.ServiceNotFoundException
import trypp.support.kotlin.kClass
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.defaultType

/**
 * A collection of hooks points for replacing with updated implementations. See [Hook] for more
 * information.
 */
class Services internal constructor() {
    private val points = java.util.HashMap<KClass<out Any>, Any>()

    fun <T : Any> create(base: KClass<T>, defaultImpl: KClass<out T>) {
        if (points.containsKey(base)) {
            throw ServiceExistsException(base, get(base).kClass, defaultImpl)
        }

        try {
            val instance = defaultImpl.constructors.first { it.parameters.size == 0 }.call()
            points.put(base, instance)
        }
        catch (e: NoSuchElementException) {
            throw BadServiceClassException(defaultImpl)
        }
    }

    fun <T : Any> create(base: KClass<T>, defaultInstance: T) {
        if (points.containsKey(base)) {
            throw ServiceExistsException(base, get(base).kClass, defaultInstance.kClass)
        }

        points.put(base, defaultInstance);
    }

    // create method always ensures cast is good
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(base: KClass<T>): T {
        if (!points.containsKey(base)) {
            throw ServiceNotFoundException(base)
        }
        return points[base] as T
    }

    fun <T : Any> replace(base: KClass<T>, impl: KClass<out T>): T {
        val currentInstance = get(base)
        var newInstance: T
        try {
            newInstance = impl.constructors.first {
                it.parameters.size == 1 && it.parameters[0].type == base.defaultType
            }.call(currentInstance)
        }
        catch (e: NoSuchElementException) {
            try {
                newInstance = impl.constructors.first { it.parameters.size == 0 }.call()
            }
            catch (e: NoSuchElementException) {
                throw BadServiceClassException(impl)
            }
        }

        points.put(base, newInstance)

        return currentInstance
    }

    fun <T : Any> replace(base: KClass<T>, instance: T): T {
        val currentInstance = get(base)
        points.put(base, instance);

        return currentInstance
    }
}
