package mongo.schedule;

import com.mongodb.MongoClient;
import mongo.scheduler.MongoScheduler;
import mongo.scheduler.SpringMongoSchedulerBuilder;
import mongo.scheduler.model.DefaultScheduledItem;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Ignore
public class BenchmarkTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkTest.class);

    private static MongoScheduler<DefaultScheduledItem> scheduler;

    private AtomicLong totalProcessedItems = new AtomicLong(0);

    @BeforeClass
    public static void prepareMongo() throws UnknownHostException {

        MongoClient client = new MongoClient("192.168.99.100", 32768);

        BenchmarkTest.scheduler = new SpringMongoSchedulerBuilder<DefaultScheduledItem>()
                .withMongoTemplate(new MongoTemplate(client, "schedulerBenchmark"))
                .withDatabase("schedulerBenchmark")
                .withSchedulerCollectionName("schedulerCollection")
                .withExpiration(5, TimeUnit.MINUTES)
                .withScheduledItemClass(DefaultScheduledItem.class)
                .validateAndBuild();
    }

    @Test
    public void benchmarkSingleThreadTest() throws InterruptedException {

        int totalItems = 2000;
        LOGGER.info("Scheduling {} items... (1 min ahead creation)", totalItems);


        for (int i = 0; i < totalItems; i++) {
            DefaultScheduledItem item = new DefaultScheduledItem();
            item.setRunDate(LocalDateTime.now().plusSeconds(30));
            scheduler.schedule(item);
        }

        LOGGER.info("Items scheduled.");
        LOGGER.info("Polling for items...");


        int receivedItems = 0;

        List<Double> deviationList = new ArrayList<>();
        List<DefaultScheduledItem> processedItems = new ArrayList<>();
        while (receivedItems < totalItems) {
            Optional<DefaultScheduledItem> opt = scheduler.poll();
            if (opt.isPresent()) {
                DefaultScheduledItem item = opt.get();
                LOGGER.info("Scheduled item received...: {}", item);
                receivedItems++;
                LocalDateTime runDate = item.getRunDate();
                double millisDiff = Math.abs(LocalDateTime.now().until(runDate, ChronoUnit.MILLIS));
                deviationList.add(millisDiff);
                processedItems.add(item);
            } else {
                Thread.sleep(500);
            }
        }

        LOGGER.info("Avg diff: [{}]", calcAvgDiff(deviationList));

        LOGGER.info("Removing schedules...");
        processedItems.parallelStream().forEach(i -> scheduler.delete(i));
        LOGGER.info("Done!");

    }

    private double calcAvgDiff(List<Double> deviationList) {
        DoubleSummaryStatistics statistics = deviationList.parallelStream().mapToDouble(val -> val).summaryStatistics();
        return statistics.getAverage();
    }

    @Test
    public void benchmark10ThreadsTest() throws InterruptedException, ExecutionException {

        int totalItems = 3000;
        LOGGER.info("Scheduling {} items... (0 to 30 secs ahead creation)", totalItems);


        Random random = new Random();
        for (int i = 0; i < totalItems; i++) {
            DefaultScheduledItem item = new DefaultScheduledItem();
            item.setRunDate(LocalDateTime.now().plusSeconds(random.nextInt(31)));
            scheduler.schedule(item);
        }

        LOGGER.info("Items scheduled.");
        LOGGER.info("Polling for items...");

        int threadCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();
        List<SchedulePoller> pollerList = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            SchedulePoller poller = new SchedulePoller(scheduler, totalItems);
            pollerList.add(poller);
            futures.add(executor.submit(poller));
        }

        List<DefaultScheduledItem> processedItems = new ArrayList<>();
        List<Double> deviationList = new ArrayList<>();
        List<Double> pollerDeviationList;
        List<DefaultScheduledItem> pollerProcessedItems;

        for (Future<?> future : futures) {
            future.get();
        }

        for (SchedulePoller schedulePoller : pollerList) {
            pollerProcessedItems = schedulePoller.getProcessedItems();
            pollerProcessedItems.forEach(processedItems::add);
            pollerDeviationList = schedulePoller.getDeviationList();
            pollerDeviationList.forEach(deviationList::add);
        }

        LOGGER.info("Avg diff: [{}]", calcAvgDiff(deviationList));

        LOGGER.info("Removing schedules...");
        processedItems.parallelStream().forEach(i -> scheduler.delete(i));
        LOGGER.info("Done!");

    }

    public class SchedulePoller implements Runnable {

        private final MongoScheduler<DefaultScheduledItem> scheduler;
        private final long totalItems;
        private List<Double> deviationList = new ArrayList<>();
        private List<DefaultScheduledItem> processedItems = new ArrayList<>();

        public SchedulePoller(MongoScheduler<DefaultScheduledItem> scheduler, long totalItems) {
            this.scheduler = scheduler;
            this.totalItems = totalItems;
        }

        @Override
        public void run() {

            while (totalProcessedItems.longValue() < totalItems) {
                Optional<DefaultScheduledItem> opt = scheduler.poll();
                if (opt.isPresent()) {
                    DefaultScheduledItem item = opt.get();
                    LOGGER.info("Scheduled item received...: {}", item);
                    totalProcessedItems.incrementAndGet();
                    LocalDateTime runDate = item.getRunDate();
                    double millisDiff = Math.abs(LocalDateTime.now().until(runDate, ChronoUnit.MILLIS));
                    this.deviationList.add(millisDiff);
                    this.processedItems.add(item);
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error while sleeping");
                    }
                }
            }

            LOGGER.info("Poller completed!");

        }

        public List<Double> getDeviationList() {
            return deviationList;
        }

        public List<DefaultScheduledItem> getProcessedItems() {
            return processedItems;
        }
    }

}
