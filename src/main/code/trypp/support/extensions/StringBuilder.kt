package trypp.support.extensions

fun StringBuilder.clear() {
    replace(0, length, "")
}