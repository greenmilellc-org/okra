package okra.builder;

import okra.base.AbstractOkra;
import okra.base.OkraItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class OkraBuilder<T extends OkraItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkraBuilder.class);

    private String collection;
    private String database;
    private Class<T> scheduleItemClass;
    private Long expireDuration;
    private TimeUnit expireDurationUnit;

    public OkraBuilder<T> withScheduledItemClass(Class<T> scheduledItemClass) {
        this.scheduleItemClass = scheduledItemClass;
        return this;
    }

    public OkraBuilder<T> withSchedulerCollectionName(String collectionName) {
        this.collection = collectionName;
        return this;
    }

    public OkraBuilder<T> withDatabase(String database) {
        this.database = database;
        return this;
    }

    public OkraBuilder<T> withExpiration(long duration, TimeUnit durationUnit) {
        this.expireDuration = duration;
        this.expireDurationUnit = durationUnit;
        return this;
    }

    public abstract AbstractOkra<T> build();

    abstract void validateConfiguration();

    public String getCollection() {
        return collection;
    }

    public String getDatabase() {
        return database;
    }

    public Class<T> getScheduleItemClass() {
        return scheduleItemClass;
    }

    public Long getExpireDuration() {
        return expireDuration;
    }

    public TimeUnit getExpireDurationUnit() {
        return expireDurationUnit;
    }
}
