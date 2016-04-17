package trypp.support.kotlin

import kotlin.reflect.KClass

/**
 * A shortcut for `javaClass.kotlin`
 */
val Any.kClass : KClass<out Any>
    get() = javaClass.kotlin