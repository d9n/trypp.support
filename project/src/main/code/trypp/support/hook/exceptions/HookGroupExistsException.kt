package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to do an action which would overwrite an existing exception.
 */
class HookGroupExistsException(val base: KClass<out Any>) :
    HookException("Hook Group $base already exists") {
}
