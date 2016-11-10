package okra.model;

import lombok.Data;
import okra.base.OkraItem;
import okra.base.OkraStatus;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @author Fernando Nogueira
 * @since 11/9/16 9:23 AM
 */
@Data
public class DefaultScheduledItem implements OkraItem {

    @Id
    private String id;

    private LocalDateTime heartbeat;

    private LocalDateTime runDate;

    private OkraStatus status;

}
