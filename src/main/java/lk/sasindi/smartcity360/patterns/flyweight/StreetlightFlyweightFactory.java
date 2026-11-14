package lk.sasindi.smartcity360.patterns.flyweight;

import lk.sasindi.smartcity360.model.StreetlightConfiguration;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StreetlightFlyweightFactory {

    private static final StreetlightFlyweightFactory INSTANCE =
            new StreetlightFlyweightFactory();

    private final Map<String, StreetlightConfiguration> cache =
            new ConcurrentHashMap<>();

    public StreetlightFlyweightFactory() {
    }

    public static StreetlightFlyweightFactory getInstance() {
        return INSTANCE;
    }

    public StreetlightConfiguration getFlyweight(
            StreetlightConfiguration configuration) {

        if (configuration == null ||
                configuration.getConfigHash() == null ||
                configuration.getConfigHash().isBlank()) {
            throw new IllegalArgumentException(
                    "A valid configuration hash is required."
            );
        }

        String configHash = configuration.getConfigHash().trim();

        return cache.compute(configHash, (key, existing) -> {
            if (existing == null) {
                return configuration;
            }

            if (!hasSameSharedSettings(existing, configuration)) {
                throw new IllegalStateException(
                        "Configuration hash '" + key
                                + "' is already associated with different "
                                + "streetlight settings."
                );
            }

            return existing;
        });
    }

    private boolean hasSameSharedSettings(
            StreetlightConfiguration first,
            StreetlightConfiguration second
    ) {
        return first.getBrightnessPercent() == second.getBrightnessPercent()
                && first.getColorTemperature() == second.getColorTemperature()
                && Objects.equals(
                first.getOperatingMode(),
                second.getOperatingMode()
        )
                && first.isMotionSensorEnabled()
                == second.isMotionSensorEnabled()
                && Objects.equals(
                first.getPowerRatingWatts(),
                second.getPowerRatingWatts()
        );
    }

    public StreetlightConfiguration find(String configHash) {

        if (configHash == null || configHash.isBlank()) {
            return null;
        }

        return cache.get(configHash);
    }

    public void retainOnly(Set<String> validHashes) {
        cache.keySet().retainAll(validHashes);
    }

    public int getCreatedCount() {
        return cache.size();
    }

    public Collection<StreetlightConfiguration> getFlyweights() {
        return cache.values();
    }

    public void clear() {
        cache.clear();
    }
}
