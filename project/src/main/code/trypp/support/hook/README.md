# Hook - A Plugin Framework

**Hook** is a collection of classes that provide code hooks which you can define an initial
implementation for but that can be overloaded later. This can be useful for modding and also unit
testing.

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

## Getting Started

An application likely will want to create two singletons, one for `Services` and one for
`Extensions`.

```kotlin
val services = Services()
val extensions = Extensions()

services.create(Logger::class, DefaultLogger::class)
extensions.create(Dictionary::class)

// Later...
services.replace(Logger::class, NoOpLogger::class);
extensions.add(Dictionary::class, CommonWords::class);
extensions.add(Dictionary::class, SwearWords::class);

// In use...
services[Logger::class].logError("This should never happen.")
val isValidSpelling = extensions[Dictionary::class].any { it.hasWord(word) }
```

## Service

When an application exposes a **service**, it allows someone to later replace the implementation
entirely.

Loggers are a common example for a service:

```kotlin
services[Logger::class].logError("This should never happen.")
```

The above syntax works just fine, but a nice convention to follow with any interface you expose as a
hook is to provide a helper static method.
```kotlin
# G.kt

// Create a global singleton for this application's services
val services = Services()
```

```kotlin
# Logger.kt

interface Logger {
    companion object {
        fun get() {
            return G.services[Logger::class]
        }
    }
}
```

```kotlin
Logger.get().logError("This should never happen.")
```

Usually, you will define a service with an empty constructor. However, you can also specify a
delegating service, one whose constructor takes the previously registered service. In this case,
the new service will be passed in the previously registered service which you can use however you
want.

```kotlin
# TimestampLogger.kt

// Add a timestamp to the beginning of all log messages!
class TimestampLogger(val wrapped: Logger) : Logger {
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

When an application exposes an **extension**, it's allows anyone to populate it with additional
entries later. This is particularly useful for modding. When the application runs, it can loop
through the extensions and call methods on them.

Extensions are useful for a variety of purposes: you can run multiple transforms on some original
data, or run a bunch of tests to reject some input. You can loop through factories that provide new
values, such as, perhaps, new character classes for a game.

In this example, we allow plugins to extend our application's initial dictionary:

```kotlin
fun isValidWord(word: String): Boolean {
    return extensions[Dictionary::class].any { it.hasWord(word) }
}
```

Like services, there's a recommended convention you can use for extensions:
```kotlin
# G.kt

val extensions = Extensions()

# Dictionary.kt

interface Dictionary {
    companion object {
        fun getAll() {
           return G.extensions[Dictionary::class];
        }
    }
}

// Elsewhere...

val underlineWord = Dictionary.getAll().none { it.hasWord(word) }
```

## Plugin Jar

The framework supports loading external `jar` files that declare their contents through a
manifest file. This file should be found in the jar's `META-INF` directory and be called
`plugin.json`. By declaring all services and extensions in a manifest file, the `Plugin` class can
be pointed at the `jar` file and automatically register all of its dependencies.

For example, say we created a plugin which provides its own custom logger which prepends a timestamp
to each line, plus a log listener that records all log lines to a backup file.

```
# userdata/plugins/logger-plugin.jar

src/main/
   TimestampLogger.kt
   LogHistory.kt
META-INF/
   plugin.json
```

```json
# plugin.json

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
val services = Services()
val extensions = Extensions()
val pluginLoader = PluginLoader(services, extensions)
// ... initialize ...

pluginLoader.loadJar("/path/to/plugin.jar")
```

## Unit testing

The `Service` can be used to help with unit testing, since it effectively offers a way to switch out
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
        prevLoader = G.services.replace(NetworkLoader::class, TestNetworkLoader::class)
    }

    @AfterClass fun tearDown() {
        G.services.replace(NetworkLoader::class, prevLoader)
    }

    @Test fun test() {
        // ... test ...
    }
}
```
