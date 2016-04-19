package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to create an extension with a class that doesn't have a valid constructor.
 */
class BadExtensionClassException(val impl: KClass<out Any>) :
    HookException("Extension $impl doesn't contain an empty constructor.") {
}
