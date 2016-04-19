package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to do an action which would overwrite an existing service.
 */
class ServiceExistsException(base: KClass<out Any>, prevImpl: KClass<out Any>,
                             newImpl: KClass<out Any>) :
    HookException("Service $base already exists: $prevImpl, rejecting $newImpl") {
}
