package mongo.scheduler;

import mongo.scheduler.exception.SchedulerRuntimeException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SpringMongoScheduler<T extends ScheduledItem> extends AbstractMongoScheduler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMongoScheduler.class);

    private final MongoTemplate mongoTemplate;
    private final long defaultheartbeatExpirationMillis;
    private final Class<T> scheduleItemClass;

    public SpringMongoScheduler(MongoTemplate mongoTemplate,
                                String database,
                                String collection,
                                long defaultheartbeatExpiration,
                                TimeUnit defaultheartbeatExpirationUnit,
                                Class<T> scheduleItemClass) {
        super(database, collection);
        this.mongoTemplate = mongoTemplate;
        this.defaultheartbeatExpirationMillis = defaultheartbeatExpirationUnit.toMillis(defaultheartbeatExpiration);
        this.scheduleItemClass = scheduleItemClass;
    }

    @Override
    public void initDbIfNeeded() {
    }

    @Override
    public Optional<T> poll() {

        LocalDateTime expiredheartbeatDate = LocalDateTime
                .now()
                .minus(defaultheartbeatExpirationMillis, ChronoUnit.MILLIS);
        Criteria mainOr = generatePollCriteria(expiredheartbeatDate);

        Update update = Update
                .update("status", ScheduledStatus.PROCESSING)
                .set("heartbeat", LocalDateTime.now());

        Query query = Query.query(mainOr);

        FindAndModifyOptions opts = new FindAndModifyOptions()
                .returnNew(true);

        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, opts, scheduleItemClass));
    }

    private Criteria generatePollCriteria(LocalDateTime expiredheartbeatDate) {
        Criteria heartbeatCriteria = new Criteria()
                .andOperator(
                        Criteria.where("status").is(ScheduledStatus.PROCESSING),
                        new Criteria().orOperator(
                                Criteria.where("heartbeat").lt(expiredheartbeatDate),
                                Criteria.where("heartbeat").is(null)
                        ));

        Criteria pendingCriteria = new Criteria().andOperator(
                Criteria.where("runDate")
                        .lt(LocalDateTime.now()),
                Criteria.where("status").is(ScheduledStatus.PENDING)
        );

        return new Criteria().orOperator(pendingCriteria, heartbeatCriteria);
    }

    @Override
    public Optional<T> reschedule(T item) {
        throw new SchedulerRuntimeException();
    }

    @Override
    public Optional<T> heartbeat(T item) {
        return heartbeatAndUpdateCustomAttrs(item, null);
    }

    @Override
    public Optional<T> heartbeatAndUpdateCustomAttrs(T item, Map<String, Object> attrs) {
        if (item.getId() == null
                || item.getHeartbeat() == null
                || item.getStatus() == null) {
            return Optional.empty();
        }

        Criteria criteria = Criteria.where("_id")
                .is(new ObjectId(item.getId()))
                .and("heartbeat").is(item.getHeartbeat())
                .and("status").is(ScheduledStatus.PROCESSING);

        Query query = Query.query(criteria);

        Update update = Update.update("heartbeat", LocalDateTime.now());

        if (attrs != null && !attrs.isEmpty()) {
            attrs.forEach(update::set);
        }

        FindAndModifyOptions opts = new FindAndModifyOptions()
                .returnNew(true);

        LOGGER.info("Querying for schedules using query: {}", query);

        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, opts, scheduleItemClass));
    }

    @Override
    public void delete(T item) {
        if (item.getId() == null) {
            return;
        }

        mongoTemplate.remove(item);
    }

    @Override
    public void schedule(T item) {
        validateSchedule(item);
        item.setStatus(ScheduledStatus.PENDING);
        mongoTemplate.save(item);
    }

    private void validateSchedule(T item) {
        if (item.getId() != null) {
            LOGGER.error("Impossible to schedule item because it already has an ID. Item: {}", item);
            throw new SchedulerRuntimeException();
        }
        if (item.getRunDate() == null) {
            LOGGER.error("Impossible to schedule item because it doesn't have a schedule date. Item: {}", item);
            throw new SchedulerRuntimeException();
        }
    }

}
