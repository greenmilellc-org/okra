package mongo.scheduler;

import java.util.Map;
import java.util.Optional;

/**
 * @author Fernando Nogueira
 * @since 11/9/16 9:43 AM
 */
public interface MongoScheduler<T extends ScheduledItem> {

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
