package trypp.support.dispose

import trypp.support.kotlin.kClass
import java.util.*
import kotlin.reflect.jvm.jvmName

/**
 * A class which manages a tree of [Disposable]s.
 *
 * To register a new disposable, use [Disposer.register]. To free it later, use [Disposer.dispose]
 *
 * As Kotlin objects are non-deterministically destructed by the Garbage Collector, it can be useful
 * to have objects which have a much stricter, more explicit lifecycle (particularly if they hold
 * onto expensive resources).
 *
 * This class works by having you set up a hierarchical relationship between instances, and then,
 * when appropriate, disposing the root [Disposable]. For example, say you bring up a UI dialog
 * which should clean itself up after you close it. Then, it will act as the parent disposable for
 * its children panels, which in turn act as the parent disposables for any of their contents that
 * must also be disposed.
 */
class Disposer {

    /**
     * A disposable paired with a stack trace representing where it was registered.
     */
    data class DisposableStackTrace(val disposable: Disposable, val stackTrace: List<StackTraceElement>)

    private class Node(val parent: Node?, val disposable: Disposable) {
        val children: MutableList<Node> = ArrayList(0)

        // Remove "Disposer$Node" entries from stacktrace, leaves top element "Disposer.register"
        val stackTrace = Throwable().stackTrace.dropWhile { it.className == kClass.jvmName }

        fun addChild(child: Disposable): Node {
            val childNode = Node(this, child)
            children.add(childNode)
            return childNode
        }

        fun remove() {
            parent!!.children.remove(this)
        }

        /**
         * Walk the tree in post-order (children first, then parents). This walking excludes the
         * top root (parentless) node, as it is essentially a dummy node.
         */
        fun walkPostOrder(callback: (Node) -> Unit) {
            for (child in children) {
                child.walkPostOrder(callback)
            }

            if (parent != null) {
                callback(this)
            }
        }
    }

    private class RootDisposable : Disposable {
        override fun dispose() {
            throw UnsupportedOperationException("Root disposable is never supposed to be disposed")
        }
    }

    private val rootNode = Node(null, RootDisposable())
    private val nodes = HashMap<Disposable, Node>()

    /**
     * Register a top-level [Disposable]. This class will only be disposed when you explicitly
     * release it.
     */
    fun register(disposable: Disposable) {
        nodes.put(disposable, rootNode.addChild(disposable))
    }

    /**
     * Register a [Disposable] whose life-cycle is tied to some parent [Disposable]. This object can
     * be released directly, or it may be disposed when one of its ancestors is released.
     */
    fun register(parent: Disposable, child: Disposable) {
        val parentNode = nodes[parent] ?: throw IllegalArgumentException(
            "Can't register disposable for unregistered parent $parent")
        nodes.put(child, parentNode.addChild(child))
    }

    /**
     * Dispose a previously registered [Disposable].
     */
    fun dispose(disposable: Disposable) {
        val parentNode = nodes[disposable] ?: throw IllegalArgumentException(
            "Trying to dispose $disposable, which is already disposed or was never registered")

        parentNode.walkPostOrder { node ->
            nodes.remove(node.disposable)
            node.disposable.dispose()
        }

        parentNode.remove()
    }

    /**
     * Return the registration stack traces of any root [Disposable]s, if any. You can call this
     * right before your application exists to test for memory leaks.
     */
    fun getRootStackTraces(): List<DisposableStackTrace> {
        return rootNode.children.map { DisposableStackTrace(it.disposable, it.stackTrace) }
    }

    /**
     * Like [getRootStackTraces] but for all [Disposable]s registered with this class.
     */
    fun getAllStackTraces(): List<DisposableStackTrace> {
        val stackTraceEntries = ArrayList<DisposableStackTrace>(nodes.size)
        rootNode.walkPostOrder {
            stackTraceEntries.add(DisposableStackTrace(it.disposable, it.stackTrace))
        }
        return stackTraceEntries
    }
}