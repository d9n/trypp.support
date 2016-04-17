package trypp.support.hook

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test
import trypp.support.hook.exceptions.BadHookPointClassException
import trypp.support.hook.exceptions.HookPointExistsException
import trypp.support.hook.exceptions.HookPointNotFoundException
import trypp.support.kotlin.clear

class HookPointsTest {

    interface Logger {
        fun log(message: String)
    }

    class DefaultLogger : Logger {
        override fun log(message: String) {
            println(message)
        }
    }

    class TestLogger(val lastLine: StringBuilder) : Logger {
        override fun log(message: String) {
            lastLine.clear()
            lastLine.append(message)
        }
    }

    class CountingLogger(val wrapped: Logger) : Logger {
        private var count = 0
        override fun log(message: String) {
            wrapped.log("$count: $message")
            ++count
        }
    }

    abstract class Counter {
        private var _count = 0
        val count: Int
            get() = _count

        abstract val countBy: Int

        fun countUp() {
            _count += countBy
        }
    }

    class OneCounter : Counter() {
        override val countBy: Int
            get() = 1
    }

    class TwoCounter : Counter() {
        override val countBy: Int
            get() = 2
    }

    @Test fun registeringHookPointByClassWorks() {
        val hookPoints = HookPoints()
        hookPoints.create(Counter::class, OneCounter::class)
        val getCounter = { -> hookPoints[Counter::class] }

        assertThat(getCounter().count).isEqualTo(0)
        getCounter().countUp()
        assertThat(getCounter().count).isEqualTo(1)
    }

    @Test fun registeringHookPointByInstanceWorks() {
        val hookPoints = HookPoints()
        hookPoints.create(Counter::class, object : Counter() {
            override val countBy: Int
                get() = 3
        })
        val getCounter = { -> hookPoints[Counter::class] }

        assertThat(getCounter().count).isEqualTo(0)
        getCounter().countUp()
        assertThat(getCounter().count).isEqualTo(3)
    }

    @Test fun replacingHookPointByClassWorks() {
        val hookPoints = HookPoints()
        hookPoints.create(Counter::class, OneCounter::class)
        val getCounter = { -> hookPoints.get(Counter::class) }

        hookPoints.replace(Counter::class, TwoCounter::class)
        assertThat(getCounter().count).isEqualTo(0)
        getCounter().countUp()
        assertThat(getCounter().count).isEqualTo(2)
    }

    @Test fun replacingHookPointWithWrappingClassWorks() {
        val hookPoints = HookPoints()
        val lastLine = StringBuilder()
        hookPoints.create(Logger::class, TestLogger(lastLine))
        hookPoints.replace(Logger::class, CountingLogger::class)
        val getLogger = { -> hookPoints.get(Logger::class) }

        assertThat(lastLine.toString()).isEmpty()
        getLogger().log("This is a test")
        assertThat(lastLine.toString()).isEqualTo("0: This is a test")
        getLogger().log("This is only a test")
        assertThat(lastLine.toString()).isEqualTo("1: This is only a test")
    }

    @Test fun creatingSameHookPointTwiceThrowsException() {
        val hookPoints = HookPoints()
        hookPoints.create(Counter::class, OneCounter::class)
        try {
            hookPoints.create(Counter::class, TwoCounter::class)
            Assert.fail()
        }
        catch (e: HookPointExistsException) {
        }

        try {
            hookPoints.create(Counter::class, object : Counter() {
                override val countBy: Int
                    get() = -1
            })
            Assert.fail()
        }
        catch (e: HookPointExistsException) {
        }
    }

    @Test fun replacingNonExistingHookPointThrowsException() {
        val hookPoints = HookPoints()
        try {
            hookPoints.replace(Counter::class, OneCounter::class)
            Assert.fail()
        }
        catch (e: HookPointNotFoundException) {
        }

        try {
            hookPoints.replace(Counter::class, object : Counter() {
                override val countBy: Int
                    get() = -2
            })
            Assert.fail()
        }
        catch (e: HookPointNotFoundException) {
        }
    }

    @Test fun createImplClassWithNoValidConstructorThrowsException() {
        val hookPoints = HookPoints()
        try {
            hookPoints.create(Logger::class, TestLogger::class)
            Assert.fail()
        }
        catch (e: BadHookPointClassException) {
        }
    }

    @Test fun replaceImplClassWithNoValidConstructorThrowsException() {
        val hookPoints = HookPoints()
        hookPoints.create(Logger::class, DefaultLogger::class)
        try {
            hookPoints.replace(Logger::class, TestLogger::class)
            Assert.fail()
        }
        catch (e: BadHookPointClassException) {
        }
    }
}
