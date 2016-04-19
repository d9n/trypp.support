package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to request an extension that doesn't exist.
 */
class ExtensionNotFoundException constructor(base: KClass<out Any>) :
    HookException("Extension $base not found") {

}
