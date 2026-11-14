package lk.sasindi.smartcity360.patterns.flyweight;

import lk.sasindi.smartcity360.model.StreetlightConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class FlyweightPatternTest {

    private StreetlightFlyweightFactory factory;

    @BeforeEach
    void setUp() {
        factory = new StreetlightFlyweightFactory();
    }

    @Test
    void sameConfigurationHashReturnsSameSharedInstance() {
        StreetlightConfiguration first = configuration(1, "RES-40", "res-40");
        StreetlightConfiguration second = configuration(99, "RES-40", "res-40");

        StreetlightConfiguration sharedFirst = factory.getFlyweight(first);
        StreetlightConfiguration sharedSecond = factory.getFlyweight(second);

        assertSame(sharedFirst, sharedSecond);
        assertEquals(1, factory.getCreatedCount());
    }

    @Test
    void differentHashesCreateDifferentFlyweights() {
        StreetlightConfiguration residential =
                factory.getFlyweight(configuration(1, "Residential", "res-40"));
        StreetlightConfiguration highway =
                factory.getFlyweight(configuration(2, "Highway", "high-120"));

        assertNotSame(residential, highway);
        assertEquals(2, factory.getCreatedCount());
    }

    @Test
    void rejectsDifferentSettingsUsingTheSameConfigurationHash() {
        StreetlightConfiguration original = configuration(
                1, "Residential", "shared-hash");
        factory.getFlyweight(original);

        StreetlightConfiguration conflicting = new StreetlightConfiguration(
                2, "Residential", 80, 4000, "AUTO", true, 40.0,
                "shared-hash");

        assertThrows(IllegalStateException.class,
                () -> factory.getFlyweight(conflicting));
        assertEquals(1, factory.getCreatedCount());
    }

    @Test
    void tenThousandStreetlightsReuseOnlyTwentyConfigurations() {
        final int streetlightCount = 10_000;
        final int configurationCount = 20;
        StreetlightConfiguration[] shared =
                new StreetlightConfiguration[configurationCount];

        for (int i = 0; i < streetlightCount; i++) {
            int index = i % configurationCount;
            StreetlightConfiguration current = factory.getFlyweight(
                    configuration(i + 1, "Configuration " + index,
                            "configuration-" + index));

            if (shared[index] == null) {
                shared[index] = current;
            } else {
                assertSame(shared[index], current);
            }
        }

        assertEquals(configurationCount, factory.getCreatedCount());
    }

    @Test
    void rejectsNullOrBlankConfigurationHash() {
        StreetlightConfiguration noHash =
                new StreetlightConfiguration(1, "Invalid", 40, 3000,
                        "AUTO", false, 40.0, " ");

        assertThrows(IllegalArgumentException.class,
                () -> factory.getFlyweight(null));
        assertThrows(IllegalArgumentException.class,
                () -> factory.getFlyweight(noHash));
        assertEquals(0, factory.getCreatedCount());
    }

    @Test
    void retainOnlyRemovesUnusedConfigurations() {
        factory.getFlyweight(configuration(1, "Residential", "res-40"));
        factory.getFlyweight(configuration(2, "Highway", "high-120"));
        factory.getFlyweight(configuration(3, "Park", "park-30"));

        factory.retainOnly(Set.of("res-40", "park-30"));

        assertEquals(2, factory.getCreatedCount());
        assertNotNull(factory.find("res-40"));
        assertNull(factory.find("high-120"));
        assertNotNull(factory.find("park-30"));
    }

    @Test
    void concurrentRequestsForSameHashShareOneInstance() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<StreetlightConfiguration>> tasks = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                int id = i + 1;
                tasks.add(() -> factory.getFlyweight(
                        configuration(id, "Commercial", "commercial-80")));
            }

            List<Future<StreetlightConfiguration>> results = executor.invokeAll(tasks);
            StreetlightConfiguration expected = results.get(0).get();
            for (Future<StreetlightConfiguration> result : results) {
                assertSame(expected, result.get());
            }
            assertEquals(1, factory.getCreatedCount());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void findReturnsNullForMissingOrInvalidHash() {
        assertNull(factory.find("missing"));
        assertNull(factory.find(null));
        assertNull(factory.find(" "));
    }

    private StreetlightConfiguration configuration(
            int id, String name, String hash) {
        return new StreetlightConfiguration(
                id, name, 65, 4000, "AUTO", true, 40.0, hash);
    }
}
