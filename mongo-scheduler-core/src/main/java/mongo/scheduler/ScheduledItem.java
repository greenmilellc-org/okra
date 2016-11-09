package mongo.scheduler;

import java.time.LocalDateTime;

/**
 * @author Fernando Nogueira
 * @since 11/8/16 7:35 PM
 */
public interface ScheduledItem {

    String getId();

    /**
     * Latest date this entry received a heartBeat
     *
     * @return The date of the latest heartbeat
     */
    LocalDateTime getLastHeartbeat();

    /**
     * The date this entry should run
     *
     * @return The date this entry should run
     */
    LocalDateTime getRunDate();

    /**
     * @return The current status of this scheduled item
     */
    ScheduledStatus getStatus();

}
