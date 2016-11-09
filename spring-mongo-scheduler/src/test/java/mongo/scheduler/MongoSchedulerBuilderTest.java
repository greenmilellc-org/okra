package mongo.scheduler;

import org.junit.Test;

public class MongoSchedulerBuilderTest {

    @Test
    public void mongoSchedulerBuilderTest() {
        MongoScheduler scheduler = new SpringMongoSchedulerBuilder()
                .withDatabase("dbName")
                .withDriver(null)
                .withSchedulerCollectionName("schedulerCollection")
                .build();
    }
}
