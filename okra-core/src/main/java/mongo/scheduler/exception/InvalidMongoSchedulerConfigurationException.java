package mongo.scheduler.exception;

/**
 * @author Fernando Nogueira
 * @since 11/8/16 7:49 PM
 */
public class InvalidMongoSchedulerConfigurationException extends RuntimeException {
    public InvalidMongoSchedulerConfigurationException() {
        super("MongoScheduler.InvalidConfiguration");
    }
}
