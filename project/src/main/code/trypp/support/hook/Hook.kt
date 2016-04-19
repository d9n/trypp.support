package trypp.support.hook

/**
 * Registry for all [Components] and [Extensions].
 *
 * To start using:
 *
 * ```
 * val hook = Hook()
 * hook.services.create(Logger::class, DefaultLogger::class)
 * hook.extensions.create(Dictionary::class)
 *
 * // Updating hooks...
 * hook.services.replace(Logger::class, NoOpLogger::class);
 * hook.extensions.add(Dictionary::class, CommonWords::class);
 * hook.extensions.add(Dictionary::class, SwearWords::class);
 *
 * // Using hooks...
 * hook.services[Logger::class].logError("This should never happen.")
 * val isValidSpelling = hook.extensions[Dictionary::class].any { it.hasWord(word) }
 * ```
 */
class Hook {
    val services = Services()
    val extensions = Extensions()
}
