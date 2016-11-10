package mongo.scheduler;

import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class MongoSchedulerBuilder<T extends ScheduledItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSchedulerBuilder.class);

    private String collection;
    private String database;
    private Class<T> scheduleItemClass;
    private Long expiraDuration;
    private TimeUnit expireDurationUnit;

    public MongoSchedulerBuilder<T> withScheduledItemClass(Class<T> scheduledItemClass) {
        this.scheduleItemClass = scheduledItemClass;
        return this;
    }

    public MongoSchedulerBuilder<T> withSchedulerCollectionName(String collectionName) {
        this.collection = collectionName;
        return this;
    }

    public MongoSchedulerBuilder<T> withDatabase(String database) {
        this.database = database;
        return this;
    }

    public MongoSchedulerBuilder<T> withExpiration(long duration, TimeUnit durationUnit) {
        this.expiraDuration = duration;
        this.expireDurationUnit = durationUnit;
        return this;
    }

    abstract AbstractMongoScheduler<T> build();

    abstract void validateConfiguration();

    public AbstractMongoScheduler<T> validateAndBuild() {
        validateConfiguration();
        return build();
    }

    public String getCollection() {
        return collection;
    }

    public String getDatabase() {
        return database;
    }

    public Class<T> getScheduleItemClass() {
        return scheduleItemClass;
    }

    public Long getExpiraDuration() {
        return expiraDuration;
    }

    public TimeUnit getExpireDurationUnit() {
        return expireDurationUnit;
    }
}
