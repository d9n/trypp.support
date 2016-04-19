package trypp.support.hook.exceptions

import kotlin.reflect.KClass

/**
 * Thrown when you try to do an action which would overwrite an existing extension.
 */
class ExtensionExistsException(val base: KClass<out Any>) :
    HookException("Extension $base already exists") {
}
