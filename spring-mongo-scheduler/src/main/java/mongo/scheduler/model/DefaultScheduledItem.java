package mongo.scheduler.model;

import lombok.Data;
import mongo.scheduler.ScheduledItem;
import mongo.scheduler.ScheduledStatus;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @author Fernando Nogueira
 * @since 11/9/16 9:23 AM
 */
@Data
public class DefaultScheduledItem implements ScheduledItem {

    @Id
    private String id;

    private LocalDateTime lastHeartbeat;

    private LocalDateTime runDate;

    private ScheduledStatus status;

}
