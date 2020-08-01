package ru.otus.sequence;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class SequenceHandler {

    private final StateHolder stateHolder = new StateHolder();
    private final int start;
    private final int end;
    private final int threads;

    public SequenceHandler(int start, int end, int threads) {
        this.start = start;
        this.end = end;
        this.threads = threads;
        stateHolder.setLastValue(start);
    }

    public void printSequence() {
        for (int i = 0; i < threads; i++) {
            new Thread(provideSequencePrinter(i, threads, end, stateHolder)).start();
        }
    }

    Runnable provideSequencePrinter(int threadNumber, int maxThreads, int end, StateHolder monitor) {
        return () -> {
            log.debug("Thread {} started", threadNumber);
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (monitor) {
                    int lastValue = monitor.getLastValue();
                    int threadsAlreadyPrinted = monitor.getProcessedThreads().getOrDefault(lastValue, 0);
                    if (monitor.areValuesAvailable(end)) {
                        if (threadsAlreadyPrinted < maxThreads) {
                            prettyPrint(lastValue);
                            supressedSleep();
                            monitor.getProcessedThreads().put(lastValue, threadsAlreadyPrinted + 1);
                            monitor.notify();
                            suppressedWait(monitor);
                        } else {
                            monitor.setLastValue(monitor.nextValue(lastValue));
                            System.out.println();
                            monitor.notify();
                        }
                    } else {
                        if (monitor.isSwapped()) {
                            log.debug("Thread {} reporting sequence end", threadNumber);
                            Thread.currentThread().interrupt();
                        } else {
                            log.debug("Thread {} reporting sequence swap for all threads", threadNumber);
                            monitor.setSwapped(true);
                            monitor.setProcessedThreads(new HashMap<>());
                        }
                    }
                }
            }
        };
    }

    private void prettyPrint(int lastValue) {
        System.out.print(lastValue + " ");
    }

    @SneakyThrows
    private void suppressedWait(StateHolder monitor) {
        monitor.wait();
    }

    @SneakyThrows
    private void supressedSleep() {
        Thread.sleep(266);
    }

    @Data
    class StateHolder {
        volatile HashMap<Integer, Integer> processedThreads = new HashMap<>();
        volatile boolean swapped = false;
        volatile int lastValue;
        volatile int counter;

        int nextValue(int someValue) {
            if (swapped) {
                return someValue - 1;
            } else {
                return someValue + 1;
            }
        }

        boolean areValuesAvailable(int end) {
            if (swapped) {
                return this.getLastValue() > 0;
            } else {
                return this.getLastValue() < end;
            }
        }
    }

}
