package mongo.scheduler;

import org.junit.Test;

/**
 * @author Fernando Nogueira
 * @since 11/8/16 7:47 PM
 */
public class MongoSchedulerBuilderTest {

    @Test
    public void mongoSchedulerBuilderTest() {
        MongoScheduler scheduler = MongoSchedulerBuilder.newBuilder()
                .withDatabase("dbName")
                .withDriver(null)
                .withSchedulerCollectionName("schedulerCollection")
                .build();

    }
}
