package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to create a hook point with a class that doesn't have a valid constructor.
 */
class BadHookPointClassException(val impl: KClass<out Any>) :
    HookException("Hook Point $impl doesn't contain an empty or wrapper constructor.") {
}
