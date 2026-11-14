package lk.sasindi.smartcity360;

import lk.sasindi.smartcity360.model.Streetlight;
import lk.sasindi.smartcity360.model.StreetlightConfiguration;
import lk.sasindi.smartcity360.patterns.flyweight.StreetlightFlyweightFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StreetlightFlyweightDemo {

    private static final int DEFAULT_LIGHTS = 50_000;
    private static final int CONFIGURATION_TYPES = 6;

    public static void main(String[] args) throws Exception {

        int lights = args.length > 0
                ? Integer.parseInt(args[0])
                : DEFAULT_LIGHTS;

        System.out.println("SmartCity360 - Flyweight demonstration");
        System.out.println("Streetlights to create : " + lights);
        System.out.println("Distinct configurations: " + CONFIGURATION_TYPES);
        System.out.println();

        Result without = run(lights, false);
        Result with = run(lights, true);

        print("WITHOUT Flyweight", without);
        print("WITH Flyweight   ", with);

        System.out.println();
        System.out.printf(
                "Configuration objects : %,d  ->  %,d  (%.2f%% fewer)%n",
                without.configurationObjects,
                with.configurationObjects,
                100.0 * (without.configurationObjects
                        - with.configurationObjects)
                        / without.configurationObjects
        );

        if (without.memoryBytes > 0 && with.memoryBytes > 0) {
            System.out.printf(
                    "Approx. memory        : %s  ->  %s  (%.1f%% less)%n",
                    mb(without.memoryBytes),
                    mb(with.memoryBytes),
                    100.0 * (without.memoryBytes - with.memoryBytes)
                            / without.memoryBytes
            );
        }

        System.out.println();
        threadSafetyCheck();
    }

    private static Result run(int lights, boolean useFlyweight) {

        StreetlightFlyweightFactory factory =
                new StreetlightFlyweightFactory();

        gc();
        long before = usedMemory();
        long start = System.nanoTime();

        List<Streetlight> list = new ArrayList<>(lights);

        for (int i = 0; i < lights; i++) {

            int type = i % CONFIGURATION_TYPES;

            StreetlightConfiguration configuration =
                    new StreetlightConfiguration(
                            type + 1,
                            "Config " + type,
                            40 + type * 10,
                            3000 + type * 500,
                            type % 2 == 0 ? "AUTO" : "MANUAL",
                            type % 2 == 0,
                            30.0 + type * 10,
                            "hash-" + type
                    );

            if (useFlyweight) {
                configuration = factory.getFlyweight(configuration);
            }

            list.add(new Streetlight(
                    i,
                    "POLE-" + i,
                    LocalDate.of(2024, 1, 1),
                    "Street " + (i % 100),
                    "District " + (i % 10),
                    "Active",
                    i % 100,
                    configuration,
                    6.9 + i * 0.00001,
                    79.8 + i * 0.00001,
                    i % 10
            ));
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        gc();
        long after = usedMemory();

        Set<StreetlightConfiguration> distinct =
                Collections.newSetFromMap(new IdentityHashMap<>());

        for (Streetlight light : list) {
            distinct.add(light.getConfiguration());
        }

        Result result = new Result(
                list.size(),
                distinct.size(),
                Math.max(0, after - before),
                elapsedMs
        );

        if (list.isEmpty()) {
            System.out.println();
        }

        return result;
    }

    private static void threadSafetyCheck() throws Exception {

        StreetlightFlyweightFactory factory =
                new StreetlightFlyweightFactory();

        ExecutorService pool = Executors.newFixedThreadPool(16);

        for (int i = 0; i < 20_000; i++) {
            pool.submit(() -> factory.getFlyweight(
                    new StreetlightConfiguration(
                            1, "Shared", 80, 4000, "AUTO",
                            true, 45.0, "hash-shared"
                    )
            ));
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println(
                "Thread-safety check: 20,000 requests from 16 threads "
                        + "for one configuration -> "
                        + factory.getCreatedCount()
                        + " shared object (expected 1)"
        );
    }

    private static void print(String label, Result r) {
        System.out.printf(
                "%s : lights=%,d | config objects=%,d | memory~%s | time=%d ms%n",
                label,
                r.lights,
                r.configurationObjects,
                mb(r.memoryBytes),
                r.elapsedMs
        );
    }

    private static void gc() {
        for (int i = 0; i < 3; i++) {
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static String mb(long bytes) {
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private record Result(
            int lights,
            int configurationObjects,
            long memoryBytes,
            long elapsedMs
    ) {
    }
}
