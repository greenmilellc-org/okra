package mongo.scheduler.base;

public abstract class AbstractMongoScheduler<T extends ScheduledItem>
        implements MongoScheduler<T> {

    private final String collection;

    private final String database;

    public AbstractMongoScheduler(String database,
                                  String collection) {
        this.collection = collection;
        this.database = database;
        initDbIfNeeded();
    }

    protected abstract void initDbIfNeeded();

    String getCollection() {
        return collection;
    }

    String getDatabase() {
        return database;
    }

}
