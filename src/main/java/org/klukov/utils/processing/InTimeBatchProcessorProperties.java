package org.klukov.utils.processing;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record InTimeBatchProcessorProperties(
        @NonNull Duration duration,
        @NonNull Supplier<Instant> currentTimeSupplier,
        String processName) {

    public static InTimeBatchProcessorProperties of(
            @NonNull Duration duration, @NonNull Supplier<Instant> currentTimeSupplier) {
        return InTimeBatchProcessorProperties.builder()
                .duration(duration)
                .currentTimeSupplier(currentTimeSupplier)
                .build();
    }

    public static InTimeBatchProcessorProperties of(@NonNull Duration duration) {
        return InTimeBatchProcessorProperties.builder()
                .duration(duration)
                .currentTimeSupplier(Instant::now)
                .build();
    }

    public Optional<String> getProcessName() {
        return Optional.ofNullable(processName);
    }
}
