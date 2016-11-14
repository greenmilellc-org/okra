package okra;

import com.mongodb.MongoClient;
import okra.base.Okra;
import okra.builder.OkraSpringBuilder;
import okra.model.DefaultOkraItem;
import org.junit.ClassRule;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.GenericContainer;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public abstract class OkraBaseContainerTest {

    @ClassRule
    public static GenericContainer mongo =
            new GenericContainer("mongo:3.2")
                    .withExposedPorts(27017);

    public static Okra<DefaultOkraItem> prepareDefaultScheduler() throws UnknownHostException {
        MongoClient client = new MongoClient(
                mongo.getContainerIpAddress(),
                mongo.getMappedPort(27017));

        return new OkraSpringBuilder<DefaultOkraItem>()
                .withMongoTemplate(new MongoTemplate(client, "schedulerBenchmark"))
                .withDatabase("schedulerBenchmark")
                .withSchedulerCollectionName("schedulerCollection")
                .withExpiration(5, TimeUnit.MINUTES)
                .withScheduledItemClass(DefaultOkraItem.class)
                .build();

    }

}
