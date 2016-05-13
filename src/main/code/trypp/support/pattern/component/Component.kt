package trypp.support.pattern.component

import trypp.support.memory.Poolable

/**
 * The base class for all components. A component is simply a collection of data, while an [Entity]
 * is a collection of components (whose behavior is entirely defined by the components it owns).
 *
 * Register an [EntitySystem] with an [EntityManager], as that is the class which will be
 * responsible for iterating over all components and acting on them.
 *
 * @see [EntityManager.newComponent]
 */
interface Component : Poolable