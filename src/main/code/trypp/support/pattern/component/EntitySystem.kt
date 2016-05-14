package trypp.support.pattern.component

import trypp.support.time.Duration
import java.util.*
import kotlin.reflect.KClass

/**
 * A class which represents logic to run on a collection of components.
 *
 * For example, a MovementSystem might collect all entities which have both a PositionComponent and
 * a VelocityComponent and update the position that way.
 *
 * Register a system with [EntityManager.registerSystem].
 *
 * TODO: We can optimize this class by caching entity:component pairs, if it turns out we need to
 * optimize. For now, we dynamically fetch each time because it's easier and most entities will
 * only have a few components anyway.
 */
abstract class EntitySystem(required: Array<out KClass<out Component>>,
                            optional: Array<out KClass<out Component>>) {

    constructor(vararg types: KClass<out Component>) : this(types, emptyArray())

    companion object {
        // Returned if user tries to access an optional component that wasn't found
        private val INVALID_COMPONENT = object : Component {
            override fun reset() {}
        }
    }

    private var requiredTypes: Array<out KClass<out Component>> = required.clone()
    private var optionalTypes: Array<out KClass<out Component>> = optional.clone()
    private var components: MutableList<Component> = ArrayList(required.size + optional.size)

    internal fun updateEntity(elapsedTime: Duration, entity: Entity) {
        if (!isMatching(entity))
            return

        entity.updateCount++
        components.clear()
        requiredTypes.forEach {
            // getComponent always non-null because isMatching is true
            components.add(entity.getComponent(it))
        }
        optionalTypes.forEach {
            components.add(entity.findComponent(it) ?: INVALID_COMPONENT)
        }

        update(elapsedTime, entity, components)
    }

    /**
     * Process an entity's components. The components will be provided in the same order as what was
     * passed into the constructor.
     */
    protected abstract fun update(elapsedTime: Duration, entity: Entity,
                                  components: List<Component>)

    private fun isMatching(entity: Entity): Boolean {
        return requiredTypes.all { entity.hasComponent(it) }
    }
}