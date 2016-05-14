package trypp.support.pattern.component

import trypp.support.extensions.kClass
import trypp.support.memory.Poolable
import java.util.*
import kotlin.reflect.KClass

/**
 * A skeletal game object whose behavior is implemented by [Component]s.
 *
 *
 * Allocate an entity and its components through a manager, using [EntityManager.newEntity],
 * [EntityManager.newEntity], and [EntityManager.newComponent]. You can then
 * free an entity and its components by calling [EntityManager.freeEntity].
 */
class Entity
/**
 * Restricted access - use [EntityManager.newEntity] instead.
 */
internal constructor(val manager: EntityManager) : Poolable {

    // Map a component's type to the component itself
    private val components = ArrayList<Component>()

    /**
     * Set to true when this entity is updated for the first time. Once initialized, you cannot
     * add any new components to this entity!
     */
    var initialized = false
        internal set

    /**
     * Number of times this entity was updated in this last update loop. It will always be 0 at the
     * beginning up the update loop and 0 or more by the end. Note that [EntityManager] will throw
     * an exception if an entity wasn't processed, as this is a likely a bug.
     */
    internal var updateCount = 0
        internal set

    /**
     * Add a component to the entity. You can safely add components after you've created an entity
     * but before an [EntityManager] first uses it.
     *
     * @throws IllegalStateException if you try to add a component to an entity that's already in
     * use (that is, has been initialized already).
     */
    fun <C : Component> addComponent(componentClass: KClass<C>): C {
        if (initialized) {
            throw IllegalStateException("Can't add a component to an Entity that's already in use.")
        }

        val component = manager.newComponent(componentClass)
        this.components.add(component)
        return component
    }

    /**
     * Test if this entity has this component. Same as calling `getComponent(class) != null`
     */
    fun <C : Component> hasComponent(classType: KClass<C>): Boolean {
        return findComponent(classType) != null
    }

    /**
     * Returns the component that matches the input type, if found.
     *
     * Use [getComponent] if you know the component will be found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <C : Component> findComponent(classType: KClass<C>): C? {
        for (i in 0 until components.size) {
            val component = components[i]
            if (component.kClass == classType) {
                return component as C
            }
        }
        return null
    }

    /**
     * Require that there be at least one instance of the specified [Component] on this entity, and
     * return the first one.
     *
     * @return the first matching component
     * @throws IllegalArgumentException if there aren't any components that match the class type
     * parameter.
     */
    @Suppress("UNCHECKED_CAST")
    fun <C : Component> getComponent(classType: KClass<C>): C {
        val numComponets = components.size
        for (i in 0 until numComponets) {
            val component = components[i]
            if (component.kClass == classType) {
                return component as C
            }
        }

        throw IllegalArgumentException(
            "Entity doesn't have any instances of ${classType.simpleName}")
    }

    override fun reset() {
        components.clear()
        initialized = false
    }

    /**
     * Free this entity when you are done with it. Shorthand for [EntityManager.freeEntity].
     */
    fun free() {
        manager.freeEntity(this)
    }

    // Called by EntityManager
    internal fun freeComponents() {
        for (i in 0 until components.size) {
            manager.freeComponent(components[i])
        }
    }

    override fun toString(): String {
        val compStrs = components.joinToString(transform = { it.kClass.simpleName.toString() })
        return "Entity {$compStrs}"
    }
}
