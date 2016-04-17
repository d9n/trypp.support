package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to request a hook point that doesn't exist.
 */
class HookPointNotFoundException constructor(base: KClass<out Any>) :
    HookException("Hook Point $base not found") {

}
