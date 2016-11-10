package okra;

import com.mongodb.MongoClient;
import okra.base.Okra;
import okra.builder.SpringOkraBuilder;
import okra.model.DefaultScheduledItem;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.GenericContainer;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EnsurePollDoesntRetrieveTheSameItemTwiceTest {

    @ClassRule
    public static GenericContainer mongo =
            new GenericContainer("mongo:3.2")
                    .withExposedPorts(27017);

    private static Okra<DefaultScheduledItem> scheduler;

    @BeforeClass
    public static void prepareScheduler() throws UnknownHostException {
        MongoClient client = new MongoClient(
                mongo.getContainerIpAddress(),
                mongo.getMappedPort(27017));

        scheduler = new SpringOkraBuilder<DefaultScheduledItem>()
                .withMongoTemplate(new MongoTemplate(client, "schedulerBenchmark"))
                .withDatabase("schedulerBenchmark")
                .withSchedulerCollectionName("schedulerCollection")
                .withExpiration(5, TimeUnit.MINUTES)
                .withScheduledItemClass(DefaultScheduledItem.class)
                .validateAndBuild();
    }

    @Test
    public void ensurePollDoesntRetrieveTheSameItemTwiceTest() {
        given_that_an_item_was_scheduled();

        Optional<DefaultScheduledItem> retrievedOpt = scheduler.poll();

        assertThat(retrievedOpt.isPresent()).isTrue();

        DefaultScheduledItem item = retrievedOpt.get();

        Optional<DefaultScheduledItem> optThatShouldBeEmpty = scheduler.poll();

        assertThat(optThatShouldBeEmpty.isPresent()).isFalse();

        // Then... Delete acquired item
        scheduler.delete(item);
    }

    private void given_that_an_item_was_scheduled() {
        DefaultScheduledItem item = new DefaultScheduledItem();
        item.setRunDate(LocalDateTime.now().minusNanos(100));
        scheduler.schedule(item);
    }

}
