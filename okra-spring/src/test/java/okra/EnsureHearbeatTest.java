package okra;

import okra.base.Okra;
import okra.model.DefaultOkraItem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EnsureHearbeatTest extends OkraBaseContainerTest {

    private static Okra<DefaultOkraItem> scheduler;

    @BeforeClass
    public static void init() throws UnknownHostException {
        scheduler = prepareDefaultScheduler();
    }

    @Test
    public void ensureHeartbeatTest() {

        given_an_item_is_scheduled();

        // Retrieve the item
        Optional<DefaultOkraItem> item = scheduler.poll();

        assertThat(item.isPresent()).isTrue();

        // then, try to heartbeat it
        Optional<DefaultOkraItem> itemHeartbeatOpt = scheduler.heartbeat(item.get());

        // Must be succeeded
        assertThat(itemHeartbeatOpt.isPresent()).isTrue();

        DefaultOkraItem itemHeartbeat = itemHeartbeatOpt.get();

        assertThat(itemHeartbeat).isNotEqualTo(item.get());
        assertThat(itemHeartbeat.getRunDate()).isNotEqualTo(item.get().getHeartbeat());
    }

    private void given_an_item_is_scheduled() {
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusSeconds(1));
        scheduler.schedule(item);
    }

}
