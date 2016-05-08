package trypp.support.memory.dispose

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test

class DisposerTest {
    class TestDisposable : Disposable {
        var disposed = false

        override fun dispose() {
            assertThat(disposed).isFalse()
            disposed = true
        }
    }

    @Test fun disposingRootDisposableWorks() {
        val d = Disposer()

        val td1 = TestDisposable()
        val td2 = TestDisposable()
        d.register(td1)
        d.register(td2)
        assertThat(td1.disposed).isFalse()
        assertThat(td2.disposed).isFalse()
        d.dispose(td1)
        assertThat(td1.disposed).isTrue()
        assertThat(td2.disposed).isFalse()
        d.dispose(td2)
        assertThat(td1.disposed).isTrue()
        assertThat(td2.disposed).isTrue()
    }

    @Test fun disposingParentDisposableWorks() {
        val d = Disposer()

        val td1 = TestDisposable()
        val td11 = TestDisposable()
        val td12 = TestDisposable()

        d.register(td1)
        d.register(td1, td11)
        d.register(td1, td12)

        assertThat(td1.disposed).isFalse()
        assertThat(td11.disposed).isFalse()
        assertThat(td12.disposed).isFalse()

        d.dispose(td11)
        assertThat(td1.disposed).isFalse()
        assertThat(td11.disposed).isTrue()
        assertThat(td12.disposed).isFalse()

        d.dispose(td1)
        assertThat(td1.disposed).isTrue()
        assertThat(td11.disposed).isTrue()
        assertThat(td12.disposed).isTrue()
    }

    @Test fun childrenCanBeDisposedFirst() {
        val d = Disposer()

        val td1 = TestDisposable()
        val td11 = TestDisposable()
        val td111 = TestDisposable()

        d.register(td1)
        d.register(td1, td11)
        d.register(td11, td111)
        assertThat(td1.disposed).isFalse()
        assertThat(td11.disposed).isFalse()
        assertThat(td111.disposed).isFalse()
        d.dispose(td111)
        assertThat(td1.disposed).isFalse()
        assertThat(td11.disposed).isFalse()
        assertThat(td111.disposed).isTrue()
        d.dispose(td1)
    }

    @Test fun getStackTraceMethodsCountMatchesExpected() {
        val d = Disposer()

        val td1 = TestDisposable()
        val td2 = TestDisposable()
        val td3 = TestDisposable()

        val td21 = TestDisposable()
        val td22 = TestDisposable()
        val td221 = TestDisposable()
        val td222 = TestDisposable()
        val td223 = TestDisposable()

        val td31 = TestDisposable()

        d.register(td1)
        d.register(td2)
        d.register(td3)

        d.register(td2, td21)
        d.register(td2, td22)
        d.register(td22, td221)
        d.register(td22, td222)
        d.register(td22, td223)

        d.register(td3, td31)

        assertThat(d.getRootStackTraces().size).isEqualTo(3)
        assertThat(d.getAllStackTraces().size).isEqualTo(9)

        d.dispose(td3)

        assertThat(d.getRootStackTraces().size).isEqualTo(2)
        assertThat(d.getAllStackTraces().size).isEqualTo(7)

        d.dispose(td2)

        assertThat(d.getRootStackTraces().size).isEqualTo(1)
        assertThat(d.getAllStackTraces().size).isEqualTo(1)

        d.dispose(td1)

        assertThat(d.getRootStackTraces().size).isEqualTo(0)
        assertThat(d.getAllStackTraces().size).isEqualTo(0)
    }

    @Test fun getStackTraceMethodsStartWithDisposerRegister() {
        val d = Disposer()

        val td1 = TestDisposable()
        val td2 = TestDisposable()
        val td3 = TestDisposable()

        val td21 = TestDisposable()
        val td22 = TestDisposable()
        val td221 = TestDisposable()
        val td222 = TestDisposable()
        val td223 = TestDisposable()

        val td31 = TestDisposable()

        d.register(td1)
        d.register(td2)
        d.register(td3)

        d.register(td2, td21)
        d.register(td2, td22)
        d.register(td22, td221)
        d.register(td22, td222)
        d.register(td22, td223)

        d.register(td3, td31)

        d.getAllStackTraces().forEach { it.stackTrace[0].className.contains("Disposer.register") }
    }


    @Test fun parentDisposableMustAlreadyBeRegistered() {
        val d = Disposer()

        val td1 = TestDisposable()
        val td11 = TestDisposable()

        try {
            d.register(td1, td11)
            Assert.fail()
        }
        catch (e: IllegalArgumentException) {
        }
    }

    @Test fun disposeMustBeCalledOnRegisteredDisposable() {
        val d = Disposer()

        val td1 = TestDisposable()

        try {
            d.dispose(td1)
            Assert.fail()
        }
        catch (e: IllegalArgumentException) {
        }
    }
}