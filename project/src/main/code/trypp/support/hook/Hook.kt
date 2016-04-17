package trypp.support.hook

/**
 * Registry for all [HookPoints] and [HookGroups].
 *
 * To start using:
 *
 * ```
 * val hook = Hook()
 * hook.points.create(Logger::class, DefaultLogger::class)
 * hook.groups.create(Dictionary::class)
 *
 * // Updating hooks...
 * hook.points.replace(Logger::class, NoOpLogger::class);
 * hook.groups.add(Dictionary::class, CommonWords::class);
 * hook.groups.add(Dictionary::class, SwearWords::class);
 *
 * // Using hooks...
 * hook.points[Logger::class].logError("This should never happen.")
 * val isValidSpelling = hook.groups[Dictionary::class].any { it.hasWord(word) }
 * ```
 */
class Hook {
    val points = HookPoints()
    val groups = HookGroups()
}
