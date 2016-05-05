package trypp.support.extensions

import kotlin.reflect.KClass

/**
 * A shortcut for `javaClass.extensions`
 */
val Any.kClass : KClass<out Any>
    get() = javaClass.kotlin