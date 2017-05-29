package trypp.support.pattern

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test
import trypp.support.extensions.clear
import trypp.support.pattern.exceptions.BadServiceClassException
import trypp.support.pattern.exceptions.ServiceExistsException
import trypp.support.pattern.exceptions.ServiceNotFoundException

class ServicesTest {

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

    class ToUpperLogger(val wrapped: Logger) : Logger {
        override fun log(message: String) {
            wrapped.log(message.toUpperCase())
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
        val services = Services()
        services.create(Counter::class, OneCounter::class)
        val getCounter = { services[Counter::class] }

        assertThat(getCounter().count).isEqualTo(0)
        getCounter().countUp()
        assertThat(getCounter().count).isEqualTo(1)
    }

    @Test fun registeringHookPointByInstanceWorks() {
        val services = Services()
        services.create(Counter::class, object : Counter() {
            override val countBy: Int
                get() = 3
        })
        val getCounter = { -> services[Counter::class] }

        assertThat(getCounter().count).isEqualTo(0)
        getCounter().countUp()
        assertThat(getCounter().count).isEqualTo(3)
    }

    @Test fun replacingHookPointByClassWorks() {
        val services = Services()
        services.create(Counter::class, OneCounter::class)
        val getCounter = { -> services[Counter::class] }

        services.replace(Counter::class, TwoCounter::class)
        assertThat(getCounter().count).isEqualTo(0)
        getCounter().countUp()
        assertThat(getCounter().count).isEqualTo(2)
    }

    @Test fun replacingHookPointWithWrappingClassWorks() {
        val services = Services()
        val lastLine = StringBuilder()
        services.create(Logger::class, TestLogger(lastLine))
        services.replace(Logger::class, CountingLogger::class)
        val getLogger = { -> services[Logger::class] }

        assertThat(lastLine.toString()).isEmpty()
        getLogger().log("This is a test")
        assertThat(lastLine.toString()).isEqualTo("0: This is a test")
        getLogger().log("This is only a test")
        assertThat(lastLine.toString()).isEqualTo("1: This is only a test")

        services.replace(Logger::class, ToUpperLogger::class)
        getLogger().log("of the emergency broadcast system")
        assertThat(lastLine.toString()).isEqualTo("2: OF THE EMERGENCY BROADCAST SYSTEM")
    }

    @Test fun creatingSameHookPointTwiceThrowsException() {
        val services = Services()
        services.create(Counter::class, OneCounter::class)
        try {
            services.create(Counter::class, TwoCounter::class)
            Assert.fail()
        }
        catch (e: ServiceExistsException) {
        }

        try {
            services.create(Counter::class, object : Counter() {
                override val countBy: Int
                    get() = -1
            })
            Assert.fail()
        }
        catch (e: ServiceExistsException) {
        }
    }

    @Test fun replacingNonExistingHookPointThrowsException() {
        val services = Services()
        try {
            services.replace(Counter::class, OneCounter::class)
            Assert.fail()
        }
        catch (e: ServiceNotFoundException) {
        }

        try {
            services.replace(Counter::class, object : Counter() {
                override val countBy: Int
                    get() = -2
            })
            Assert.fail()
        }
        catch (e: ServiceNotFoundException) {
        }
    }

    @Test fun createImplClassWithNoValidConstructorThrowsException() {
        val services = Services()
        try {
            services.create(Logger::class, TestLogger::class)
            Assert.fail()
        }
        catch (e: BadServiceClassException) {
        }
    }

    @Test fun replaceImplClassWithNoValidConstructorThrowsException() {
        val services = Services()
        services.create(Logger::class, DefaultLogger::class)
        try {
            services.replace(Logger::class, TestLogger::class)
            Assert.fail()
        }
        catch (e: BadServiceClassException) {
        }
    }
}
