package trypp.support.hook

import trypp.support.hook.exceptions.BadExtensionClassException
import trypp.support.hook.exceptions.ExtensionExistsException
import trypp.support.hook.exceptions.ExtensionNotFoundException
import java.util.*
import kotlin.reflect.KClass

/**
 * A collection of hooks for looping through. See [Hook] for more information.
 */
class Extensions internal constructor() {
    private val groups = java.util.HashMap<KClass<out Any>, List<Any>>()

    fun <T : Any> create(base: KClass<T>) {
        if (groups.containsKey(base)) {
            throw ExtensionExistsException(base)
        }

        groups.put(base, ArrayList<Any>(1))
    }

    operator fun <T : Any> get(base: KClass<T>): Iterable<T> {
        return getAsList(base)
    }

    fun <T : Any> add(base: KClass<T>, impl: KClass<out T>) {
        val group = getAsList(base)
        try {
            val instance = impl.constructors.first { it.parameters.size == 0 }.call()
            group.add(instance)
        }
        catch (e: NoSuchElementException) {
            throw BadExtensionClassException(impl)
        }

    }

    fun <T : Any> add(base: KClass<T>, instance: T) {
        val group = getAsList(base)
        group.add(instance)
    }

    // [create] always ensures cast is good
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getAsList(base: KClass<T>): MutableList<T> {
        if (!groups.containsKey(base)) {
            throw ExtensionNotFoundException(base)
        }
        return groups[base] as MutableList<T>
    }
}
