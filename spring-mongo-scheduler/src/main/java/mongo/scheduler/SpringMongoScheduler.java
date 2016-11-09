package mongo.scheduler;

import mongo.scheduler.exception.SchedulerRuntimeException;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SpringMongoScheduler<T extends ScheduledItem> extends AbstractMongoScheduler<T> {

    private final MongoTemplate mongoTemplate;
    private final long defaultHeartbeatExpirationMillis;
    private final Class<T> scheduleItemClass;

    public SpringMongoScheduler(MongoTemplate mongoTemplate,
                                String database,
                                String collection,
                                long defaultHeartbeatExpiration,
                                TimeUnit defaultHeartbeatExpirationUnit,
                                Class<T> scheduleItemClass) {
        super(database, collection);
        this.mongoTemplate = mongoTemplate;
        this.defaultHeartbeatExpirationMillis = defaultHeartbeatExpirationUnit.toMillis(defaultHeartbeatExpiration);
        this.scheduleItemClass = scheduleItemClass;
    }

    @Override
    public void initDbIfNeeded() {
    }

    @Override
    public Optional<T> poll() {

        LocalDateTime expiredHeartbeatDate = LocalDateTime
                .now()
                .minus(defaultHeartbeatExpirationMillis, ChronoUnit.MILLIS);
        Criteria mainOr = generatePollCriteria(expiredHeartbeatDate);

        Update update = Update
                .update("status", ScheduledStatus.PROCESSING)
                .set("lastHeartbeat", LocalDateTime.now());

        Query query = Query.query(mainOr);

        FindAndModifyOptions opts = new FindAndModifyOptions()
                .returnNew(true);

        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, opts, scheduleItemClass));
    }

    private Criteria generatePollCriteria(LocalDateTime expiredHeartbeatDate) {
        Criteria heartBeatCriteria = new Criteria()
                .andOperator(
                        Criteria.where("status").is(ScheduledStatus.PROCESSING),
                        new Criteria().orOperator(
                                Criteria.where("heartBeat").lt(expiredHeartbeatDate),
                                Criteria.where("heartBeat").is(null)
                        ));

        Criteria pendingCriteria = Criteria
                .where("runDate")
                .lt(LocalDateTime.now())
                .and("status").is(ScheduledStatus.PENDING);

        return new Criteria().orOperator(pendingCriteria, heartBeatCriteria);
    }

    @Override
    public Optional<T> reschedule(T item) {
        throw new SchedulerRuntimeException();
    }

    @Override
    public Optional<T> heartbeat(T item) {

        if (item.getId() == null
                || item.getLastHeartbeat() == null
                || item.getStatus() == null) {
            return Optional.empty();
        }

        Criteria criteria = Criteria.where("_id")
                .is(new ObjectId(item.getId()))
                .and("heartbeat").is(item.getLastHeartbeat())
                .and("status").is(ScheduledStatus.PROCESSING);

        Query query = Query.query(criteria);

        Update update = Update.update("heartbeat", LocalDateTime.now());

        FindAndModifyOptions opts = new FindAndModifyOptions()
                .returnNew(true);

        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, opts, scheduleItemClass));
    }

    @Override
    public void delete(T item) {
        if (item.getId() == null) {
            return;
        }

        mongoTemplate.remove(item);
    }

}
