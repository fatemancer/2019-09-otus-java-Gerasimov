package ru.otus;

import com.sun.management.GarbageCollectionNotificationInfo;
import ru.otus.data.Aggregator;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {

    public static void main(String[] args) throws Exception {

        String type = args[0];
        System.out.println("VM started, type " + type);

        int i = 0;
        ArrayList<Integer> integers = new ArrayList<>();
        Aggregator aggregator = new Aggregator();
        switchOnMonitoring(aggregator, type);
        aggregator.init();

        long startTime = System.currentTimeMillis();
        while (i < 10000000) {
            try {
                List<Integer> collect =
                        Stream.generate(() -> Double.valueOf(Math.random() * 1000).intValue()).limit(50000 * i).collect(
                                Collectors.toList());
                integers.addAll(collect);
                integers.removeIf(x -> Math.random() > 0.5);
                System.out.print("\rWorking");
                Thread.sleep(50);
                System.out.print("\rWorking...");
                System.out.print("\rWorking... ...");
                i++;
            } catch (OutOfMemoryError e) {
                Files.writeString(Path.of(Config.STAT_FILE), aggregator.dump(
                        System.currentTimeMillis() - startTime,
                        i,
                        type
                ), StandardOpenOption.APPEND);
                System.exit(0);
            }
        }
    }

    private static void switchOnMonitoring(Aggregator aggregator, String gcType) {
        List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcbean : gcbeans) {
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = (notification, handback) -> {
                if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                    GarbageCollectionNotificationInfo info =
                            GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                    aggregator.insert(info, gcType);
                }
            };
            emitter.addNotificationListener(listener, null, null);
        }
    }
}
