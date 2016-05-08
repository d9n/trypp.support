# Disposer

_Destructing classes in a garbage collected world_

## Overview

**Disposer** and **Disposable** are two classes which work together to allow for when you need to
explicitly clean up resources in Java or Kotlin.

```kotlin
interface Disposable {
    fun dispose()
}

class Disposer {
    fun register(parent: Disposable, child: Disposable)
}
```

In most cases, you can rely on the Garbage Collector to handle releasing your memory allocations,
but there are also times where you need to mark a class as dead at which point it should release
some resources. Furthermore, code is naturally hierarchical (`A` contains `B` contains `C`), and it
would be nice if destroying the parent object automatically released any children objects as well.

Use a `Disposer` to register any class that implements `Disposable` and then `dispose` them later.
Note that every `Disposable` class has a `dispose` method you could potentially call directly, but
you're not supposed to. Always use `Disposer` for registration / disposing:

```kotlin
class PrintOnDispose(val id: String) : Disposable {
    override fun dispose() {
        println("Disposed: $id")
    }
}

val d = Disposer()

val parent = PrintOnDispose("p")
val child1 = PrintOnDispose("c1")
val child2 = PrintOnDispose("c2")
val grandchild11 = PrintOnDispose("gc11")

d.register(parent)
d.register(parent, child1)
d.register(parent, child2)
d.register(child1, grandchild11)

d.dispose(parent)

// Output:
// Disposed: gc11
// Disposed: c1
// Disposed: c2
// Disposed: p
```

## Memory Leaks

It is encouraged that you check for any memory leaks before exiting your application:

```kotlin
val d = Disposer()
// ... a bunch of register and dispose calls later ...

fun exitApplication() {
    val stackTraces = d.getRootStackTraces()
    if (stackTraces.size > 0) {
        log.error("Memory leak detected! ${stackTraces.size} root disposables never released")
        stackTraces.forEach {
        d.getAllStackTraces().forEach {
            println("Unreleased: ${it.disposable}\n\t${it.stackTrace.joinToString("\n\t")}")
        }
    }
}
```

## Caveats

Remember, registering your `Disposable` with `Disposer` means there's now a reference to it that's
keeping it alive. This means the garbage collector cannot collect it! Which is pretty ironic, if by
trying to manage your memory more carefully, you actually start wasting it.

A good rule of thumb is to only use `Disposer` and `Disposable` when you know you'll be holding
onto resources you should explicitly release when there's a very clear lifecycle to the objects you
want to dispose.