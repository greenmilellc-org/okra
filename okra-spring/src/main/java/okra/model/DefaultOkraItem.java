package okra.model;

import lombok.Data;
import okra.base.OkraItem;
import okra.base.OkraStatus;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class DefaultOkraItem implements OkraItem {

    @Id
    private String id;

    private LocalDateTime heartbeat;

    private LocalDateTime runDate;

    private OkraStatus status;

}
