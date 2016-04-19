package trypp.support.hook

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test
import trypp.support.hook.exceptions.BadExtensionClassException
import trypp.support.hook.exceptions.ExtensionExistsException
import trypp.support.hook.exceptions.ExtensionNotFoundException

class ExtensionsTest {

    abstract class WordMatcher {
        abstract fun match(word: String): Boolean
    }

    class DigitMatcher : WordMatcher() {
        val digits = setOf(
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

        override fun match(word: String): Boolean {
            return digits.contains(word)
        }
    }

    class LetterMatcher : WordMatcher() {
        override fun match(word: String): Boolean {
            return word.length == 1 && word[0].isLetter()
        }
    }

    class SingleWordMatcher(val word: String) : WordMatcher() {
        override fun match(word: String): Boolean {
            return this.word == word
        }
    }

    @Test fun registeringHookGroupByClassWorks() {
        val extensions = Extensions()
        extensions.create(WordMatcher::class)
        val getMatchers = { -> extensions[WordMatcher::class] }

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isFalse()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()

        extensions.add(WordMatcher::class, DigitMatcher::class)

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isTrue()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()

        extensions.add(WordMatcher::class, LetterMatcher::class)

        assertThat(getMatchers().any { it.match("l") }).isTrue()
        assertThat(getMatchers().any { it.match("eight") }).isTrue()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()
    }

    @Test fun registeringHookGroupByInstanceWorks() {
        val extensions = Extensions()
        extensions.create(WordMatcher::class)
        val getMatchers = { -> extensions[WordMatcher::class] }

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isFalse()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()

        extensions.add(WordMatcher::class, object : WordMatcher() {
            override fun match(word: String): Boolean {
                return word == ":)"
            }
        })

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isFalse()
        assertThat(getMatchers().any { it.match(":)") }).isTrue()
    }

    @Test fun creatingSameHookGroupTwiceThrowsException() {
        val extensions = Extensions()
        extensions.create(WordMatcher::class)
        try {
            extensions.create(WordMatcher::class)
            Assert.fail()
        }
        catch (e: ExtensionExistsException) {
        }
    }

    @Test fun addingToNonExistingHookGroupThrowsException() {
        val extensions = Extensions()
        try {
            extensions.add(WordMatcher::class, LetterMatcher::class)
            Assert.fail()
        }
        catch (e: ExtensionNotFoundException) {
        }
    }

    @Test fun addImplClassWithNoValidConstructorThrowsException() {
        val extensions = Extensions()
        extensions.create(WordMatcher::class)
        try {
            extensions.add(WordMatcher::class, SingleWordMatcher::class)
            Assert.fail()
        }
        catch (e: BadExtensionClassException) {
        }
    }

}
