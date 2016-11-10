package okra.builder;

import okra.SpringOkra;
import okra.base.AbstractOkra;
import okra.base.OkraItem;
import okra.exception.InvalidOkraConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringOkraBuilder<T extends OkraItem> extends OkraBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringOkraBuilder.class);

    private MongoTemplate mongoTemplate;

    @Override
    AbstractOkra<T> build() {
        validateConfiguration();
        return new SpringOkra<>(mongoTemplate, getDatabase(),
                getCollection(), getExpireDuration(),
                getExpireDurationUnit(), getScheduleItemClass());
    }

    public SpringOkraBuilder<T> withMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        return this;
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

            throw new InvalidOkraConfigurationException();

        }
    }

}
