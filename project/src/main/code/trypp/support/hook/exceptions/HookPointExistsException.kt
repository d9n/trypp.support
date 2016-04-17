package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to do an action which would overwrite an existing exception.
 */
class HookPointExistsException(base: KClass<out Any>, prevImpl: KClass<out Any>,
                               newImpl: KClass<out Any>) :
    HookException("Hook Point $base already exists: $prevImpl, rejecting $newImpl") {
}
