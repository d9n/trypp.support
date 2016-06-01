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

    private data class Entry(val entity: Entity, val components: List<Component>)

    companion object {
        // Returned if user tries to access an optional component that wasn't found
        private val INVALID_COMPONENT = object : Component {
            override fun reset() {}
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

        entries.add(Entry(entity, components))

        init(entity, components)
    }

    internal fun entityRemoved(entity: Entity) {
        for (i in 0 until entries.size) {
            if (entries[i].entity === entity) {
                entries.removeAt(i)
                break
            }
        }
    }

    internal fun update(elapsedTime: Duration) {
        entries.forEach {
            it.entity.updateCount++
            update(elapsedTime, it.entity, it.components)
        }
    }

    protected open fun init(entity: Entity, components: List<Component>) {}

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