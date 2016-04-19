package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to create a service with a class that doesn't have a valid constructor.
 */
class BadServiceClassException(val impl: KClass<out Any>) :
    HookException("Service $impl doesn't contain an empty or wrapper constructor.") {
}
