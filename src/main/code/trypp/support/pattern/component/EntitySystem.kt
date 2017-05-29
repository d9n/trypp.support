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
 */
abstract class EntitySystem(required: Array<out KClass<out Component>>,
                            optional: Array<out KClass<out Component>>) {

    constructor(vararg types: KClass<out Component>) : this(types, emptyArray())

    private data class Entry(val entity: Entity, val components: List<Component>, val data: Any?)

    companion object {
        // Returned if user tries to access an optional component that wasn't found
        private val INVALID_COMPONENT = object : Component {
            override fun reset() {
            }
        }
    }

    private var requiredTypes: Array<out KClass<out Component>> = required.clone()
    private var optionalTypes: Array<out KClass<out Component>> = optional.clone()
    private var entries: MutableList<Entry> = ArrayList()

    internal fun entityAdded(entity: Entity) {
        if (!isMatching(entity)) return

        val components = ArrayList<Component>(requiredTypes.size + optionalTypes.size)
        requiredTypes.forEach {
            // getComponent always non-null because isMatching is true
            components.add(entity.getComponent(it))
        }
        optionalTypes.forEach {
            components.add(entity.findComponent(it) ?: INVALID_COMPONENT)
        }

        val data = onEntityAdded(entity, components);
        entries.add(Entry(entity, components, data))
    }

    internal fun entityRemoved(entity: Entity) {
        for (i in 0 until entries.size) {
            if (entries[i].entity === entity) {
                val entry = entries.removeAt(i)
                onEntityRemoved(entry.entity, entry.components, entry.data)
                break
            }
        }
    }

    internal fun update(elapsedTime: Duration) {
        entries.forEach {
            it.entity.updateCount++
            update(elapsedTime, it.entity, it.components, it.data)
        }
    }

    /**
     * Allow a system to respond to the addition of a new entity. This is a great place to
     * initialize any local state related to the entity.
     *
     * @return Optional data that should be associated with this entity. If returned, it will be
     * passed down again in each call to [update]
     */
    protected open fun onEntityAdded(entity: Entity, components: List<Component>): Any? {
        return null
    }

    /**
     * Allow a system to respond to the removal an entity. This is a great place to clean up
     * any disposable resources (like event listeners) initialized by [onEntityAdded]
     */
    protected open fun onEntityRemoved(entity: Entity, components: List<Component>, data: Any?) {
    }

    /**
     * Process an entity's components. The components will be provided in the same order as what was
     * passed into the constructor.
     *
     * @param data Data that may have optionally been created by [onEntityAdded]
     */
    protected abstract fun update(elapsedTime: Duration, entity: Entity,
                                  components: List<Component>,
                                  data: Any?)

    private fun isMatching(entity: Entity): Boolean {
        return requiredTypes.all { entity.hasComponent(it) }
    }
}