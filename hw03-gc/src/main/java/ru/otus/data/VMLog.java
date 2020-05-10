package ru.otus.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.math.Quantiles;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VMLog {
    List<LogItem> items;
    Long totalTime;
    String vmName;
    Integer lastProcessedIndex;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogItem {
        String gcType;
        String gcAction;
        Long gcDuration;
    }

    @JsonIgnore
    public Map<String, Double> getMedianGcDuration() {
        return Map.of("Major GC",
                getMajorGc().map(LogItem::getGcDuration)
                        .mapToDouble(Long::doubleValue)
                        .reduce(Quantiles.median()::compute)
                        .orElse(0.0d),
                "Minor GC",
                getMinorGc().map(LogItem::getGcDuration)
                        .mapToDouble(Long::doubleValue)
                        .reduce(Quantiles.median()::compute)
                        .orElse(0.0d)
        );
    }

    @JsonIgnore
    public Map<String, Double> getMeanGcDuration() {
        double size = items.size();
        return Map.of("Major GC",
                getMajorGc().map(LogItem::getGcDuration).reduce(Long::sum).map(s -> s / size).orElse(0.0d), "Minor " +
                        "GC",
                getMinorGc().map(LogItem::getGcDuration).reduce(Long::sum).map(s -> s / size).orElse(0.0d)
        );
    }

    Stream<LogItem> getMinorGc() {
        return items.stream().filter(i -> i.getGcAction().equals("end of minor GC"));
    }

    Stream<LogItem> getMajorGc() {
        return items.stream().filter(i -> i.getGcAction().equals("end of major GC"));
    }
}
