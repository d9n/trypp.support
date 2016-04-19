# Hook - A Plugin Framework

**Hook** is a collection of classes that makes it easy for your project to support plugins, e.g.
code hooks which external contributors can modify later.

## Overview

The library exposes two concepts: **services** and **extensions**.

A **service** is essentially an interface / implementation pair (see also: the
[Service](http://gameprogrammingpatterns.com/service-locator.html) pattern). An **extension**,
similarly, is an interface paired with a list of zero or more implementations.

**Services** and **extensions** both act as _hooks_ for providing implementations later.
A service represents a standalone component, while an extension represents a list of components.

```kotlin
service.doSomething()

for (extension in extensions) {
   extension.doSomething()
}
```

An application must always provide a default implementation for any **services** it creates. In
contrast, **extensions** are often left empty (but an application can populate it with some
initial implementations if it wants to).

## Creating Hooks

To get started, an application should create an instance of a `Hook` registry class. From there, it
can register services and extensions.

```kotlin
val hook = Hook()
hook.services.create(Logger::class, DefaultLogger::class)
hook.extensions.create(Dictionary::class)

// Updating hooks...
hook.services.replace(Logger::class, NoOpLogger::class);
hook.extensions.add(Dictionary::class, CommonWords::class);
hook.extensions.add(Dictionary::class, SwearWords::class);

// Using hooks...
hook.services[Logger::class].logError("This should never happen.")
val isValidSpelling = hook.extensions[Dictionary::class].any { it.hasWord(word) }
```

## Service

When an application exposes a **service**, it allows a plugin to later replace the
implementation entirely.

This is useful when the application developer wants to provide a simple, default component in their
own code while allowing the community to replace it later. For example, you might imagine replacing
a factory interface for creating generated assets. Your default factory might produce simple,
low-resolution textures, while a dedicated modder might provide fancy 4K textures.

Loggers are another possible example of something that can be replaced at runtime:

```kotlin
hook.services[Logger::class].logError("This should never happen.")
```

The above syntax works just fine, but a nice convention to follow with any interface you expose as a
hook is to provide a helper static method.
```kotlin
# G.kt

// Create a global singleton for this application's hooks
val hook = Hook()
```

```kotlin
# Logger.kt

interface Logger {
    companion object {
        fun get() {
            return G.hook.services[Logger::class]
        }
    }
}
```

```kotlin
Logger.get().logError("This should never happen.")
```

Usually, a service implementation class should have an empty constructor. However, if your class
has a constructor that takes its interface as a sole argument, `Hook` will pass in the current
implementation which you can use a delegation pattern.

```kotlin
# TimestampLogger.kt

// Add a timestamp to the beginning of all log messages!
class TimestampLogger(wrapped: Logger) : Logger {
    override fun logDebug(message: String) {
        wrapped.logDebug(decorate(message));
    }

    override fun logError(message: String) {
        wrapped.logError(decorate(message));
    }

    private fun decorate(message: String): String {
        return "#{getDate()}: #{message}";
    }

    private fun getDate(): Date {
        return Date()
    }
}
```

## Extension

When an application exposes an **extension**, it's allows plugins to populate it with additional
entries. When the application runs, it can loop through the extensions and call methods on them.

Extensions are useful for a variety of purposes: you can run multiple transforms on some original data,
or run a bunch of tests to reject some input. You can loop through factories that provide new
values, such as, perhaps, new character classes for a game.

In this example, we allow plugins to extend our application's initial dictionary:

```kotlin
fun isValidWord(word: String): Boolean {
    return hook.extensions[Dictionary::class].any { it.hasWord(word) }
}
```

Like services, there's a recommended convention you can use for extensions:
```kotlin
# Dictionary.kt

interface Dictionary {
    companion object {
        fun getAll() {
           return G.hook.extensions[Dictionary::class];
        }
    }
}

// Elsewhere...

val showUnderline = Dictionary.getAll().none { it.hasWord(word) }
```

## Jar of Hooks

The framework supports loading external `jar` files that declare their contents through a
manifest file. This file should be found in the jar's `META-INF` directory and be called
`hooks.json`. By declaring all services and extensions in a manifest file, the `Hook` class can be
pointed at the `jar` file and automatically register all of its dependencies.

For example, say we created a plugin which provides its own custom logger which prepends a timestamp
to each line, plus a log listener that records all log lines to a backup file.

Let's say we had an application with all code in a package called `hook.sample`:

```
# userdata/plugins/logger-plugin.jar

src/main/
   TimestampLogger.kt
   LogHistory.kt
META-INF/
   hooks.json
```

```json
# hooks.json

name: "Logger Plugin",
services: [
    { interface: "hook.sample.Logger", implementation: "TimestampLogger" }
]
extensions: [
    { interface: "hook.sample.LogListener", implementation: "LogHistory" }
]
```

Once a jar is prepared, loading it is trivial.
```kotlin
val hook = Hook()
// ... initialize ...

hook.loadJar("/path/to/plugin.jar")
```

## Plugin Conflict

Two plugins may conflict with one another if both require changing the same hook. If this happens,
`loadJar` will throw a `HookConflictException`. In such a case, you may want to consider disabling
all plugins temporarily and asking the user to disable some plugins.

The `Hook` class contains `pushMark`, `popMark` and `restoreToMark` methods to allow your
application to continue even in the case of plugin conflicts. `restoreToMark` will revert values
back to the way they were before, while `popMark` will remove the mark but leave values the same.

```kotlin
val hook = Hook()
// ... initialize ...

hook.pushMark()
try {
    val pluginsDir = File("/path/to/plugins")
    for (file in FileTreeWalk(pluginsDir) {
        if (file.extension == "jar") {
            hooks.loadJar(file)
        }
    }
}
catch (e: HookException) {
    hook.restoreToMark()
}
```

## Unit testing

`Hook` can be used to help with unit testing, since it effectively offers a way to switch out
implementations of major components at test time.

Keep in mind that explicit
[dependency injection](https://en.wikipedia.org/wiki/Dependency_injection) is always recommended
first, since it sets up a clear relationship between the class being tested and the service it
depends upon.

Still, the following pattern can occasionally be quite useful:

```kotlin
class SomeTest {
    var prevLoader: NetworkLoader by Delegates.notNull()

    @BeforeClass fun setUp() {
        prevLoader = G.hook.services.replace(NetworkLoader::class, TestNetworkLoader::class)
    }

    @AfterClass fun tearDown() {
        G.hook.services.replace(NetworkLoader::class, prevLoader)
    }

    @Test fun test() {
        // ... test ...
    }
}
```
