package okra;

import okra.base.Okra;
import okra.model.DefaultOkraItem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
        Optional<DefaultOkraItem> itemOpt = scheduler.poll();

        assertThat(itemOpt.isPresent()).isTrue();

        DefaultOkraItem item = itemOpt.get();

        assertThat(item.getHeartbeat()).isNotNull();
        assertThat(
                Math.abs(item.getHeartbeat().until(LocalDateTime.now(), ChronoUnit.MICROS)))
                .isLessThan(TimeUnit.MILLISECONDS.toNanos(100));

        // then, try to heartbeat it
        Optional<DefaultOkraItem> itemHeartbeatOpt = scheduler.heartbeat(item);

        // Must be succeeded
        assertThat(itemHeartbeatOpt.isPresent()).isTrue();

        DefaultOkraItem itemHeartbeat = itemHeartbeatOpt.get();
        assertThat(
                Math.abs(itemHeartbeat.getHeartbeat().until(LocalDateTime.now(), ChronoUnit.MICROS)))
                .isLessThan(TimeUnit.MILLISECONDS.toNanos(100));

        assertThat(itemHeartbeat).isNotEqualTo(itemOpt.get());
        assertThat(itemHeartbeat.getHeartbeat()).isNotEqualTo(itemOpt.get().getHeartbeat());
    }

    private void given_an_item_is_scheduled() {
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusSeconds(1));
        scheduler.schedule(item);
    }

}
