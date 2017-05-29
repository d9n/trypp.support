package trypp.support.pattern.component

import trypp.support.collections.ArrayMap
import trypp.support.extensions.kClass
import trypp.support.memory.HeapPool
import trypp.support.time.Duration
import java.util.*
import kotlin.reflect.KClass

/**
 * A class which manages of collection of entities.
 */
class EntityManager(maxEntityCount: Int) {

    interface EntityInitializer {
        fun initialize(entity: Entity)
    }

    private val entityPool = HeapPool({ Entity(this) }, { it.reset() }, maxEntityCount)
    private val componentPools = ArrayMap<KClass<*>, HeapPool<*>>(32)
    private val initializers = ArrayMap<Enum<*>, EntityInitializer>()
    private val systems = ArrayList<EntitySystem>()
    private val queuedForRemoval = Stack<Entity>()

    init {
        queuedForRemoval.ensureCapacity(maxEntityCount)
    }

    /**
     * Convenience method to call [registerInitializer] with a lambda.
     */
    fun registerInitializer(id: Enum<*>, initialize: (Entity) -> Unit) {
        registerInitializer(id, object : EntityInitializer {
            override fun initialize(entity: Entity) {
                initialize(entity)
            }
        })
    }

    /**
     * Register an initializer so future code can create a new entity given an ID.
     *
     * @see newEntity
     */
    fun registerInitializer(id: Enum<*>, entityInitializer: EntityInitializer) {
        if (getEntityInitializer(id) != null) {
            throw IllegalArgumentException("Attempt to register duplicate entity id $id")
        }

        initializers.put(id, entityInitializer)
    }

    /**
     * Create a new entity using an id previously registered with [registerInitializer].
     *
     * @throws IllegalArgumentException if no intializer was registered with the target id
     */
    fun newEntity(id: Enum<*>): Entity {
        val initializer = getEntityInitializer(id) ?:
            throw IllegalArgumentException("Attempt to create entity from invalid id $id")

        val entity = newEntity()
        initializer.initialize(entity)

        return entity
    }

    /**
     * Create a new, empty entity shell, which you can populate with components using
     * [Entity.addComponent].
     *
     * Consider using [registerInitializer] then [newEntity] instead, as that makes it very easy to
     * create many entity instances of similar types.
     */
    fun newEntity(): Entity {
        return entityPool.grabNew()
    }

    /**
     * Request a new component instance from the manager's pool. Don't use directly; instead, use
     * [Entity.addComponent] which delegates to this method.
     */
    @Suppress("UNCHECKED_CAST")
    internal fun <C : Component> newComponent(componentClass: KClass<C>): C {
        if (!componentPools.containsKey(componentClass)) {
            val componentPool = HeapPool.of(componentClass, entityPool.capacity)
            componentPool.makeResizable(entityPool.maxCapacity)
            componentPools.put(componentClass, componentPool)
        }

        return componentPools[componentClass].grabNew() as C
    }

    /**
     * Add a new entity system to this manager. The order of registration matters, as the first
     * system registered will run first.
     */
    fun registerSystem(system: EntitySystem) {
        systems.add(system)
        entityPool.itemsInUse.forEach { if (it.initialized) system.entityAdded(it) }
    }

    /**
     * Call when you are done with this entity and want to release its resources. If an update cycle
     * is in progress, it will be freed after the cycle has finished.
     */
    fun freeEntity(entity: Entity) {
        // It's possible that this method can get called more than once before we have a chance to
        // actually remove the entity, so we guard against that here.
        if (!queuedForRemoval.contains(entity)) {
            queuedForRemoval.push(entity)
        }
    }

    /**
     * Update all [EntitySystem]s managed by this manager, processing all entities.
     */
    fun update(elapsedTime: Duration) {
        val entities = entityPool.itemsInUse
        entities.forEach { e ->
            if (!e.initialized) systems.forEach { s -> s.entityAdded(e) }
            e.initialized = true
        }
        systems.forEach { it.update(elapsedTime) }
        entities.forEach {
            if (it.updateCount == 0) {
                throw IllegalStateException("Entity registered but not processed by any system: $it")
            }
            it.updateCount = 0
        }

        // Kill any dead objects from the last cycle
        while (!queuedForRemoval.empty()) {
            freeEntityInternal(queuedForRemoval.pop())
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun freeComponent(component: Component) {
        if (!componentPools.containsKey(component.kClass)) {
            throw IllegalArgumentException(
                "Can't free component type ${component.kClass} as we don't own it.")
        }

        (componentPools[component.kClass] as HeapPool<Component>).free(component)
    }

    private fun getEntityInitializer(id: Enum<*>): EntityInitializer? {
        return initializers.getIf(id)
    }

    private fun freeEntityInternal(entity: Entity) {
        systems.forEach { it.entityRemoved(entity) }
        entity.freeComponents()
        entityPool.free(entity)
    }
}
