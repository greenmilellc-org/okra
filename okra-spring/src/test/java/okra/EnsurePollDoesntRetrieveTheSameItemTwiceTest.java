package okra;

import okra.base.Okra;
import okra.model.DefaultOkraItem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EnsurePollDoesntRetrieveTheSameItemTwiceTest extends OkraBaseContainerTest {

    private static Okra<DefaultOkraItem> scheduler;

    @BeforeClass
    public static void init() throws UnknownHostException {
        scheduler = prepareDefaultScheduler();
    }

    @Test
    public void ensurePollDoesntRetrieveTheSameItemTwiceTest() {
        given_that_an_item_was_scheduled();

        Optional<DefaultOkraItem> retrievedOpt = scheduler.poll();

        assertThat(retrievedOpt.isPresent()).isTrue();

        DefaultOkraItem item = retrievedOpt.get();

        Optional<DefaultOkraItem> optThatShouldBeEmpty = scheduler.poll();

        assertThat(optThatShouldBeEmpty.isPresent()).isFalse();

        // Then... Delete acquired item
        scheduler.delete(item);
    }

    private void given_that_an_item_was_scheduled() {
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusNanos(100));
        scheduler.schedule(item);
    }

}
