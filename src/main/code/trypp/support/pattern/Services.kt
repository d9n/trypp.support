package trypp.support.pattern

import trypp.support.pattern.exceptions.BadServiceClassException
import trypp.support.pattern.exceptions.ServiceExistsException
import trypp.support.pattern.exceptions.ServiceNotFoundException
import trypp.support.extensions.kClass
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.defaultType

/**
 * A collection of services, which is basically a mapping of interface to implementation pairs.
 * Though conceptually simple, the
 * [Service pattern](http://gameprogrammingpatterns.com/service-locator.html) has many advantages
 * over the traditional Singleton pattern, not the least of which it can act as a main home for the
 * rest of the singletons in your application.
 *
 * To use:
 *
 * ```
 * val services = Services()
 * services.create(Logger::class, DefaultLogger::class)
 * services.replace(Logger::class, NullLogger::class)
 * services[Logger::class].logError("This should never happen")
 * ```
 *
 * Note that your service implementation can provide a delegation constructor. If provided, it will
 * be initialized with the previous service which it can then use to handle any heavy lifting.
 *
 * ```
 * class TimestampLogger(val wrapped: Logger) : Logger {
 *    override fun logDebug(message: String) { wrapped.logDebug(decorate(message)) }
 *    override fun logError(message: String) { wrapped.logError(decorate(message)) }
 *    private fun decorate(message: String): String { ... }
 * }
 * ```
 */
class Services {
    private val serviceImpls = java.util.HashMap<KClass<out Any>, Any>()

    fun <T : Any> create(base: KClass<T>, defaultImpl: KClass<out T>) {
        if (serviceImpls.containsKey(base)) {
            throw ServiceExistsException(base, get(base).kClass, defaultImpl)
        }

        try {
            val instance = defaultImpl.constructors.first { it.parameters.size == 0 }.call()
            serviceImpls.put(base, instance)
        }
        catch (e: NoSuchElementException) {
            throw BadServiceClassException(defaultImpl)
        }
    }

    fun <T : Any> create(base: KClass<T>, defaultInstance: T) {
        if (serviceImpls.containsKey(base)) {
            throw ServiceExistsException(base, get(base).kClass, defaultInstance.kClass)
        }

        serviceImpls.put(base, defaultInstance);
    }

    // create method always ensures cast is good
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(base: KClass<T>): T {
        if (!serviceImpls.containsKey(base)) {
            throw ServiceNotFoundException(base)
        }
        return serviceImpls[base] as T
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

        serviceImpls.put(base, newInstance)

        return currentInstance
    }

    fun <T : Any> replace(base: KClass<T>, instance: T): T {
        val currentInstance = get(base)
        serviceImpls.put(base, instance);

        return currentInstance
    }
}
