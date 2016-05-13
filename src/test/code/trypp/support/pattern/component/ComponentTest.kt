package trypp.support.pattern.component

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test
import trypp.support.time.Duration

class ComponentTest {

    class CountComponent : Component {
        var count = 0
        override fun reset() {
            count = 0
        }
    }

    class MarkerComponent : Component {
        override fun reset() { }
    }

    // Position in meters
    class PosComponent : Component {
        var x: Float = 0f
        var y: Float = 0f

        override fun reset() {
            x = 0f
            y = 0f
        }
    }

    // Velocity in meters / second
    class VelComponent : Component {
        var x: Float = 0f
        var y: Float = 0f

        override fun reset() {
            x = 0f
            y = 0f
        }
    }

    enum class Entities {
        Tree, // Doesn't move
        Player, // Moves around like a boss
    }

    class MovementSystem : EntitySystem(PosComponent::class, VelComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            val pos = components[0] as PosComponent
            val vel = components[1] as VelComponent

            pos.x += vel.x * elapsedTime.getSeconds()
            pos.y += vel.y * elapsedTime.getSeconds()
        }
    }

    class RenderSystem : EntitySystem(PosComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            // Do nothing here but in production render the entity at its position!
        }
    }

    class AddSystem : EntitySystem(CountComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            (components[0] as CountComponent).count++
        }
    }

    class AddMarkedOnlySystem : EntitySystem(CountComponent::class, MarkerComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            (components[0] as CountComponent).count++
        }
    }

    class QuadrupleSystem : EntitySystem(CountComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            (components[0] as CountComponent).count *= 4
        }
    }

    class FreeOnThreeSystem : EntitySystem(CountComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            if ((components[0] as CountComponent).count == 3) {
                entity.free()
            }
        }
    }

    @Test fun managerCanCreateEntities() {
        val manager = EntityManager(1)
        val entity = manager.newEntity()
        assertThat(entity.manager).isEqualTo(manager)
    }

    @Test fun managerThrowsExceptionIfSurpassingMaxEntities() {
        val manager = EntityManager(1)
        manager.newEntity()

        try {
            manager.newEntity()
            Assert.fail()
        }
        catch(e: IllegalStateException) {
        }
    }

    @Test fun managerCanRegisterEntitiesByTemplate() {
        val manager = EntityManager(2)
        manager.registerInitializer(Entities.Tree, {
            it.addComponent(PosComponent::class)
        })

        manager.registerInitializer(Entities.Player, {
            it.addComponent(PosComponent::class)
            it.addComponent(VelComponent::class)
        })

        val tree = manager.newEntity(Entities.Tree)
        val player = manager.newEntity(Entities.Player)

        assertThat(tree.hasComponent(PosComponent::class)).isTrue()
        assertThat(tree.hasComponent(VelComponent::class)).isFalse()
        assertThat(player.hasComponent(PosComponent::class)).isTrue()
        assertThat(player.hasComponent(VelComponent::class)).isTrue()
    }

    @Test fun entitySystemVisitsMatchingEntities() {
        val manager = EntityManager(2)
        manager.registerSystem(AddSystem())
        manager.registerSystem(AddMarkedOnlySystem())

        val entityA = manager.newEntity()
        val countComponentA = entityA.addComponent(CountComponent::class)
        entityA.addComponent(MarkerComponent::class)

        val entityB = manager.newEntity()
        val countComponentB = entityB.addComponent(CountComponent::class)

        assertThat(countComponentA.count).isEqualTo(0)
        assertThat(countComponentB.count).isEqualTo(0)

        manager.update(Duration.zero())

        assertThat(countComponentA.count).isEqualTo(2)
        assertThat(countComponentB.count).isEqualTo(1)
    }

    @Test fun updateDurationPassedToSystem() {
        val manager = EntityManager(2)
        manager.registerSystem(MovementSystem())
        manager.registerSystem(RenderSystem())

        manager.registerInitializer(Entities.Tree, {
            it.addComponent(PosComponent::class)
        })

        manager.registerInitializer(Entities.Player, {
            it.addComponent(PosComponent::class)
            it.addComponent(VelComponent::class)
        })

        val tree = manager.newEntity(Entities.Tree)
        val player = manager.newEntity(Entities.Player)

        val treePos = tree.getComponent(PosComponent::class)
        val playerPos = player.getComponent(PosComponent::class)
        val playerVel = player.getComponent(VelComponent::class)

        treePos.x = -10f
        treePos.y = 30f
        playerPos.x = 20f
        playerPos.y = 20f
        playerVel.x = 10f
        playerVel.y = 5f

        manager.update(Duration.ofSeconds(2f))

        assertThat(treePos.x).isWithin(0f).of(-10f)
        assertThat(treePos.y).isWithin(0f).of(30f)
        assertThat(playerPos.x).isWithin(0f).of(40f)
        assertThat(playerPos.y).isWithin(0f).of(30f)
    }

    @Test fun entitySystemCanFreeEntity() {
        val manager = EntityManager(1)
        manager.registerSystem(AddSystem())
        manager.registerSystem(FreeOnThreeSystem())

        val countComponent = manager.newEntity().addComponent(CountComponent::class)

        assertThat(countComponent.count).isEqualTo(0)

        manager.update(Duration.zero())
        assertThat(countComponent.count).isEqualTo(1)

        manager.update(Duration.zero())
        assertThat(countComponent.count).isEqualTo(2)

        manager.update(Duration.zero()) // Entity freed here
        assertThat(countComponent.count).isEqualTo(0)

        manager.update(Duration.zero()) // Running again doesn't increase count anymore
        assertThat(countComponent.count).isEqualTo(0)
    }

    @Test fun entityCantBeModifiedAfterInitialized() {
        var manager = EntityManager(1)
        manager.registerSystem(AddSystem())
        val entity = manager.newEntity()

        entity.addComponent(CountComponent::class)
        manager.update(Duration.zero())
        try {
            entity.addComponent(VelComponent::class)
            Assert.fail("Entity can't be modified once initialized")
        }
        catch(e: IllegalStateException) {
        }
    }

    @Test fun getComponentThrowsExceptionIfComponentNotFound() {
        var manager = EntityManager(1)
        val entity = manager.newEntity()

        assertThat(entity.findComponent(CountComponent::class)).isNull()
        try {
            entity.getComponent(CountComponent::class)
            Assert.fail("Entity component not found")
        }
        catch(e: IllegalArgumentException) {
        }
    }

    @Test fun entityManagerThrowsExceptionIfEntityIsNotProcessed() {
        var manager = EntityManager(1)
        manager.newEntity()

        try {
            manager.update(Duration.zero())
            Assert.fail("Entity not updated")
        }
        catch(e: IllegalStateException) {
        }
    }

    @Test fun entitySystemRegistrationOrderMatters() {
        var managerA = EntityManager(1)
        managerA.registerSystem(AddSystem())
        managerA.registerSystem(QuadrupleSystem())
        val countA = managerA.newEntity().addComponent(CountComponent::class)

        var managerB = EntityManager(1)
        managerB.registerSystem(QuadrupleSystem())
        managerB.registerSystem(AddSystem())
        val countB = managerB.newEntity().addComponent(CountComponent::class)

        managerA.update(Duration.zero())
        managerB.update(Duration.zero())

        assertThat(countA.count).isEqualTo(4) // ((0 + 1) * 4)
        assertThat(countB.count).isEqualTo(1) // ((0 * 4) + 1)
    }


}