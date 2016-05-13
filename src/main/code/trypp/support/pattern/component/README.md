# Pattern.Component

A component / entity framework. Components are data classes, entities are a collection of
components, entity systems process entities which are a mix of just the right components, and
entity managers act as an entry point to manage them all.

All memory is pre-allocated, and any new component you create will also implement the Poolable
interface.

A high-level example of this framework (some lines excluded for simplicity):

```kotlin
    // Position in meters
    class PosComponent : Component {
        var x: Float = 0f
        var y: Float = 0f
    }

    // Velocity in meters / second
    class VelComponent : Component {
        var x: Float = 0f
        var y: Float = 0f
    }

    class ImageComponent : Component {
        var tex: Texture? = null
    }

    class MovementSystem : EntitySystem(PosComponent::class, VelComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            val pos = components[0] as PosComponent
            val vel = components[1] as VelComponent

            pos.x += vel.x * elapsedTime.getSeconds()
            pos.y += vel.y * elapsedTime.getSeconds()
        }
    }

    class RenderSystem : EntitySystem(ImageComponent::class, PosComponent::class) {
        override fun update(elapsedTime: Duration, entity: Entity, components: List<Component>) {
            val image = components[0] as ImageComponent
            val pos = components[1] as PosComponent

            image.tex.renderAt(pos.x, pos.y)
        }
    }

    // In a game loop somewhere...

    val manager = EntityManager(maxEntityCount = 1000)
    manager.registerSystem(MovementSystem())
    manager.registerSystem(RenderSystem())

    manager.newEntity(Entities.Player)
    manager.newEntity(Entities.Ghost)
    manager.newEntity(Entities.Ghost)
    manager.newEntity(Entities.Ghost)
    manager.newEntity(Entities.Wall)

    ...

    while (!shouldQuit) {
        manager.update(timeSoFar)
    }

```

Inspired both by [Game Programming Patterns: Component](http://gameprogrammingpatterns.com/component.html)
and [Ashley](https://github.com/libgdx/ashley).

Ashley itself is based on [Ash](http://www.ashframework.org/) and
[Artemis](https://thelinuxlich.github.io/artemis_CSharp/).