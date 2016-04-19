package trypp.support.dispose

/**
 * An interface to mark on classes that hold onto resources that should be explicitly let go of
 * (such as file / database handles, expensive image resources, etc.)
 *
 * See also [Disposer]
 */
interface Disposable {
    /**
     * Release this class's resources.
     *
     * DO NOT CALL THIS DIRECTLY! Instead, call [Disposer.dispose]
     */
    fun dispose()
}