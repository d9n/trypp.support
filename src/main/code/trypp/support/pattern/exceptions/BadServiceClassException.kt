package trypp.support.pattern.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to create a service with a class that doesn't have a valid constructor.
 */
class BadServiceClassException(impl: KClass<out Any>) :
    Exception("Service $impl doesn't contain an empty or wrapper constructor.") {
}
