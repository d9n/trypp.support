# Pattern.Observer

Source event and listener mechanisms.

## Event

An event represents a moment that zero or more observers can listen to. An event can (and often
will) include arguments to provide listeners with more detail, even occasionally giving them
a handle to affect the event.

For an event with no arguments, use `Event0`; for an event with 1 argument, use `Event1`;
etc.

```
class Person(name: String) {
    val onNameChanged = Event2<Person, String>()
    var name = name
        private set(value) {
            val oldName = name
            name = value
            onNameChanged(this, oldName)
        }
}

// Elsewhere...
p = Person()
p.onNameChanged += { p, oldName -> ... }
```

For readability, you can consider creating intermediate parameter classes:

```
class Person(name: String) {
    class NameChangedParams(val oldName)

    val onNameChanged = Event2<Person, NameChangedParams>()
    var name = name
        private set(value) {
            val oldName = name
            name = value
            onNameChanged(this, NameChangedParams(oldName))
        }
}

// Elsewhere...
p = Person()
p.onNameChanged += { p, params -> ... params.oldName ... }
```

If you need to remove a listener later, you should use the `addListener` method instead of the
`+=` operator, and keep the reference around until it can be unregistered:

```
val listener = demoEvent.addListener { ... }

// Later, in dispose
demoEvent.removeListener(listener) // Or, demoEvent.clearListeners()
```

Let's close this discussion out with another demo. Let's say we have a window class which has
`onOpened`, `onClosing`, and `onClosed` events. Furthermore, let's say `onClosing` takes an
additional parameter, `CancelParams`, which allows a listener to prevent the operation.

```
class CancelParams {
   var shouldCancel = false;
}

class Window {
    val onOpened = Event1<Window>()
    val onClosing = Event2<Window, CancelParams>()
    val onClosed = Event1<Window>()

    fun open() {
        onOpened(this)
    }

    fun close() {
        val p = CancelParams()
        onClosing(this, p);
        if (!p.shouldCancel) {
            onClosed(this)
        }
    }
}
```

Inspired by [C# event handling](https://msdn.microsoft.com/en-us/library/edzehd2t(v=vs.110).aspx).

## Topic

An alternate approach to `Event`s is to use a `Topic`. A topic is associated with an interface,
which listeners can register to with implementations of that interface. A special `broadcast` field
on the topic is provided with its own implementation of the interface. When that instance's methods
are called, any registered listeners will be notified.

The usefulness of this is easier to demonstration with an example:

```
interface ProgressListener {
    fun starting()
    fun ended()
}

val progress = Topic(ProgressListener::class)

demoTopic += (object : ProgressListener {
    override fun starting() { ... }
    override fun ended() { ... }
}

progress.broadcast.starting() // event → listeners
// Do some logic
progress.broadcast.ended() // event → listeners
```

Whereas the `Event` classes are great for representing independent, individual events, topics are
great for organizing a collection of related events which should all be considered together.

We can reproduce the `Window` example above using `Topic`s for a comparison of the two approaches:

```
    class CancelParams {
        var shouldCancel = false;
    }

    interface WindowEvents {
        fun onOpened(sender: Window)
        fun onClosing(sender: Window, params: CancelParams)
        fun onClosed(sender: Window)
    }

    class Window {
        val lifecycle = Topic(WindowEvents::class)

        fun open() {
            lifecycle.broadcast.onOpened(this)
        }

        fun close() {
            val p = CancelParams()
            lifecycle.broadcast.onClosing(this, p)
            if (!p.shouldCancel) {
                lifecycle.broadcast.onClosed(this)
            }
        }
    }
```

Inspired by [Game Programming Patterns: Observer](http://gameprogrammingpatterns.com/observer.html)
and [IntelliJ's Messaging Infrastructure](http://www.jetbrains.org/intellij/sdk/docs/reference_guide/messaging_infrastructure.html).

## Which to use?

Events and topics can be used together in the same codebase, and even in the same class. Still,
you should make the appropriate choice based on pros and cons.

### Event

**Pros**

* Individual events are easy to reason about
* Each event can be listened to independently
* Scales well as it is easy to add new events
* Something like `w.onOpened`, `w.onClosed` is easier to discover than `w.topic`
* Offers a convenient lambda syntax

**Cons**

* Lack of named args may make code hard to read
    * e.g. `event += { arg1, arg2 -> ... }`

### Topic

**Pros**

* You can use clear method and parameter names in your interface and listener implementations.
    * e.g. `object : LifecycleEvents { onOpened(sender), onClosed(sender) }`
* Can indicate to the developer that if they listen to one event, they should also be listening to
  its related events
* Can vastly reduce the number of fields, helping to organize an otherwise unruly, event heavy class

**Cons**

* It can be hard to come up with a good, consistent naming convention for topics
* Topics may end up becoming a dumping ground for new events, e.g. you you want to add a third
  event that's similar to the first two but it's not obvious that most people will care about it,
  but it seems wasteful to create a new topic or event for it...

Overall, you'll probably do fine sticking to `Event`s in most places, but `Topic`s are there for
cases where grouped events / readability is more important to the code.
