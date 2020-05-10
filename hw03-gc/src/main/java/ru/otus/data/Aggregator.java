package ru.otus.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.GarbageCollectionNotificationInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
public class Aggregator {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private VMLog vmLog;

    public synchronized String dump(long timestamp, int lastProcessedIndex, String gcName) {
        vmLog.setTotalTime(timestamp);
        vmLog.setVmName(gcName);
        vmLog.setLastProcessedIndex(lastProcessedIndex);
        try {
            return MAPPER.writeValueAsString(vmLog) + "\n";
        } catch (JsonProcessingException e) {
            log.error("Cannot write json string: {}", vmLog);
            return "{}";
        }
    }

    public static Optional<VMLog> from(String text) {
        try {
            return Optional.of(MAPPER.readValue(text, VMLog.class));
        } catch (JsonProcessingException e) {
            log.error("Cannot read json string: {}", text);
            return Optional.empty();
        }
    }

    synchronized public void insert(GarbageCollectionNotificationInfo info, String gcType) {
        vmLog.getItems().add(VMLog.LogItem.builder().gcAction(info.getGcAction()).gcDuration(info.getGcInfo()
                .getDuration()).gcType(gcType).build());
    }

    public void init() {
        vmLog = new VMLog();
        vmLog.setItems(new ArrayList<>());
    }
}
