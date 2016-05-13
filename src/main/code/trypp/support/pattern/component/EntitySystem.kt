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
abstract class EntitySystem(vararg componentTypes: KClass<out Component>) {
    private var componentTypes: List<KClass<out Component>> = componentTypes.asList()
    private var components: MutableList<Component> = ArrayList(componentTypes.size)

    internal fun updateEntity(elapsedTime: Duration, entity: Entity) {
        if (!isMatching(entity))
            return

        entity.updated = true
        components.clear()
        componentTypes.forEach {
            // getComponent always non-null because isMatching is true
            components.add(entity.findComponent(it)!!)
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
        return componentTypes.all { entity.hasComponent(it) }
    }

}