package okra.base;

public abstract class AbstractOkra<T extends OkraItem>
        implements Okra<T> {

    private final String collection;

    private final String database;

    public AbstractOkra(String database,
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
