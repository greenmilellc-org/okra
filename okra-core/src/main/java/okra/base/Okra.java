/*
 *  Copyright (c) 2016 Fernando Nogueira
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package okra.base;

import java.util.Map;
import java.util.Optional;

public interface Okra<T extends OkraItem> {

    /**
     * Retrieves a scheduled item from the backend
     *
     * @return An optional containing the item if found, otherwise an empty optional
     */
    Optional<T> poll();

    /**
     * Reschedule an item that was previously retrieved from the scheduled items pool
     *
     * @param item The item that will be rescheduled
     * @return The rescheduled item if success, otherwise an empty optional
     */
    Optional<T> reschedule(T item);

    /**
     * Heartbeat an item to prevent that other scheduled item consumers acquire this same item
     *
     * @param item The item to heartbeat
     * @return The updated item if success, otherwise an empty optional
     */
    Optional<T> heartbeat(T item);

    /**
     * Heartbeat an item to prevent that other scheduled item consumers acquire this same item
     * This operation also updates custom attributes
     *
     * @param item The item to heartbeat
     * @return The updated item if success, otherwise an empty optional
     */
    Optional<T> heartbeatAndUpdateCustomAttrs(T item, Map<String, Object> attrs);

    /**
     * Delete a scheduled item
     *
     * @param item The item to be deleted
     */
    void delete(T item);

    /**
     * Schedule an item
     *
     * @param item The item to schedule
     */
    void schedule(T item);

}
