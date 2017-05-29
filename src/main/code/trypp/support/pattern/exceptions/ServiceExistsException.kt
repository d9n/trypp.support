package trypp.support.pattern.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to do an action which would overwrite an existing service.
 */
class ServiceExistsException(base: KClass<out Any>, prevImpl: KClass<out Any>,
                             newImpl: KClass<out Any>) :
    Exception("Service $base already exists: $prevImpl, rejecting $newImpl")
