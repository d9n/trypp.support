package trypp.support.kotlin

fun StringBuilder.clear() {
    replace(0, length, "")
}