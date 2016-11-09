package mongo.scheduler;

import mongo.scheduler.exception.InvalidMongoSchedulerConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringMongoSchedulerBuilder<T extends ScheduledItem> extends MongoSchedulerBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMongoSchedulerBuilder.class);

    private MongoTemplate mongoTemplate;

    @Override
    public AbstractMongoScheduler<T> build() {
        validateConfiguration();
        return new SpringMongoScheduler<>(mongoTemplate, getDatabase(),
                getCollection(), getExpiraDuration(),
                getExpireDurationUnit(), getScheduleItemClass());
    }

    public void withMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void validateConfiguration() {
        if (mongoTemplate == null || getCollection() == null
                || getDatabase() == null || getCollection().isEmpty()
                || getDatabase().isEmpty()) {
            LOGGER.error("Invalid MongoScheduler configuration. " +
                            "Please verify params: " +
                            "[MongoTemplate not null? {}, Database: {}, Collection: {}]",
                    mongoTemplate != null, getDatabase(), getCollection());

            throw new InvalidMongoSchedulerConfigurationException();

        }
    }

}
