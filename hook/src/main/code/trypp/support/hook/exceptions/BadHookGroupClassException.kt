package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to create a hook group with a class that doesn't have a valid constructor.
 */
class BadHookGroupClassException(val impl: KClass<out Any>) :
    HookException("Hook Group $impl doesn't contain an empty constructor.") {
}
