package trypp.support.hook

import com.google.common.truth.Truth.assertThat
import org.testng.Assert
import org.testng.annotations.Test
import trypp.support.hook.exceptions.BadHookGroupClassException
import trypp.support.hook.exceptions.HookGroupExistsException
import trypp.support.hook.exceptions.HookGroupNotFoundException

class HookGroupsTest {

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
        val hookGroups = HookGroups()
        hookGroups.create(WordMatcher::class)
        val getMatchers = { -> hookGroups[WordMatcher::class] }

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isFalse()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()

        hookGroups.add(WordMatcher::class, DigitMatcher::class)

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isTrue()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()

        hookGroups.add(WordMatcher::class, LetterMatcher::class)

        assertThat(getMatchers().any { it.match("l") }).isTrue()
        assertThat(getMatchers().any { it.match("eight") }).isTrue()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()
    }

    @Test fun registeringHookGroupByInstanceWorks() {
        val hookGroups = HookGroups()
        hookGroups.create(WordMatcher::class)
        val getMatchers = { -> hookGroups[WordMatcher::class] }

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isFalse()
        assertThat(getMatchers().any { it.match(":(") }).isFalse()

        hookGroups.add(WordMatcher::class, object : WordMatcher() {
            override fun match(word: String): Boolean {
                return word == ":)"
            }
        })

        assertThat(getMatchers().any { it.match("l") }).isFalse()
        assertThat(getMatchers().any { it.match("eight") }).isFalse()
        assertThat(getMatchers().any { it.match(":)") }).isTrue()
    }

    @Test fun creatingSameHookGroupTwiceThrowsException() {
        val hookGroups = HookGroups()
        hookGroups.create(WordMatcher::class)
        try {
            hookGroups.create(WordMatcher::class)
            Assert.fail()
        }
        catch (e: HookGroupExistsException) {
        }
    }

    @Test fun addingToNonExistingHookGroupThrowsException() {
        val hookGroups = HookGroups()
        try {
            hookGroups.add(WordMatcher::class, LetterMatcher::class)
            Assert.fail()
        }
        catch (e: HookGroupNotFoundException) {
        }
    }

    @Test fun addImplClassWithNoValidConstructorThrowsException() {
        val hookGroups = HookGroups()
        hookGroups.create(WordMatcher::class)
        try {
            hookGroups.add(WordMatcher::class, SingleWordMatcher::class)
            Assert.fail()
        }
        catch (e: BadHookGroupClassException) {
        }
    }

}
