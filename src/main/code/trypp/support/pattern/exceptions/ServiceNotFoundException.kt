package trypp.support.pattern.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to request a service that doesn't exist.
 */
class ServiceNotFoundException constructor(base: KClass<out Any>) :
    Exception("Service $base not found")
