package trypp.support.extensions

/**
 * Take the current item and swap it to the end of the list before removing it. This is a useful
 * operation because removing something from the end of a list is often a much more lightweight
 * operation than removing from the middle.
 *
 * @throws IllegalArgumentException if the specified item is not in the list.
 */
fun <T> MutableList<T>.swapToEndAndRemove(item: T): T {
    var itemIndex = -1;
    for (i in 0 until size) {
        if (get(i) === item) {
            itemIndex = i;
            break;
        }
    }

    return swapToEndAndRemove(itemIndex);
}

/**
 * Like [swapToEndAndRemove] but where the list index is known.
 */
fun <T> MutableList<T>.swapToEndAndRemove(itemIndex: Int): T {
    if (itemIndex < 0 || itemIndex >= size) {
        throw IllegalArgumentException("Trying to remove an item that's not in the list");
    }

    // Swap item to the end before removing (since removing from the end avoids shifting elements)
    if (itemIndex != lastIndex) {
        val temp = get(lastIndex);
        set(lastIndex, get(itemIndex));
        set(itemIndex, temp);
    }

    return removeAt(lastIndex);
}
