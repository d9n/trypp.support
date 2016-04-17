package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to request a hook point that doesn't exist.
 */
class HookGroupNotFoundException constructor(base: KClass<out Any>) :
    HookException("Hook Group $base not found") {

}
