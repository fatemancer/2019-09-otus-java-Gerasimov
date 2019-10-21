package ru.otus;

import com.google.common.collect.Comparators;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class Main {

    public static void main(String[] args) {
        testAssignmentContractMethods();
        testServiceMethods();
    }

    private static void testAssignmentContractMethods() {
        Stream.of(
                testAddAll(),
                testCopy(),
                testSort()
        ).forEach(System.out::println);
    }

    private static void testServiceMethods() {
        Stream.of(
                testResize(),
                testRemove(),
                testIterateAndRemove()
        ).forEach(System.out::println);
    }

    private static Result testIterateAndRemove() {
        final String NAME = "Removal while iterating works";
        try {
            DIYArrayList<Integer> collectionToClear = provideRandomIntegers(25);
            Iterator<Integer> i = collectionToClear.listIterator();

            while (i.hasNext()) {
                i.next();
                i.remove();
            }
            return new Result(collectionToClear.isEmpty(), NAME);
        } catch (Exception e) {
            return new Result(false, NAME, e);
        }
    }

    private static Result testSort() {
        final String NAME = "Collections.sort works";
        try {
            DIYArrayList<Integer> collectionToSort = provideRandomIntegers(20);
            Collections.sort(collectionToSort);
            return new Result(Comparators.isInOrder(collectionToSort, Comparator.naturalOrder()), NAME);
        } catch (Exception e) {
            return new Result(false, NAME, e);
        }
    }

    private static Result testCopy() {
        final String NAME = "Collections.copy works";
        try {
            DIYArrayList<Integer> source = provideRandomIntegers(20000);
            DIYArrayList<Integer> destination = provideRandomIntegers(20000);
            Collections.copy(destination, source);
            compareCollections(source, destination);
            return new Result(true, NAME);
        } catch (Exception e) {
            return new Result(false, NAME, e);
        }
    }

    private static Result testAddAll() {
        final String NAME = "Collections.addAll works";
        try {
            Integer[] array = new Integer[20000];
            DIYArrayList<Integer> source = provideRandomIntegers(20000);
            DIYArrayList<Integer> destination = new DIYArrayList<>();
            Collections.addAll(destination, source.toArray(array));
            return new Result(compareCollections(source, destination), NAME);
        } catch (Exception e) {
            return new Result(false, NAME, e);
        }
    }

    private static Result testResize() {
        final String NAME = "Adding elements resizes underlying array";
        try {
            DIYArrayList<Integer> expected = Stream.generate(() -> 1)
                    .limit(2000)
                    .collect(Collectors.toCollection(DIYArrayList::new));
            DIYArrayList<Integer> empty = new DIYArrayList<>();
            while (empty.size() < 2000) {
                empty.add(1);
            }
            return new Result(compareCollections(expected, empty), NAME);
        } catch (Exception e) {
            return new Result(false, NAME, e);
        }
    }

    private static Result testRemove() {
        final String NAME = "Element removal reallocates array";
        DIYArrayList<Integer> expected = Stream.iterate(1, i -> i + 1)
                .limit(1999)
                .collect(Collectors.toCollection(DIYArrayList::new));
        DIYArrayList<Integer> full = Stream.iterate(0, i -> i + 1)
                .limit(2000)
                .collect(Collectors.toCollection(DIYArrayList::new));
        full.remove(0);
        return new Result(compareCollections(expected, full), NAME);
    }

    private static DIYArrayList<Integer> provideRandomIntegers(int amount) {
        return Stream
                .generate(() -> (int) (Math.random() * 10000))
                .limit(amount)
                .collect(Collectors.toCollection(DIYArrayList::new));
    }

    private static <E extends Comparable> boolean compareCollections(
            DIYArrayList<E> source,
            DIYArrayList<E> destination
    ) {
        for (int i = 0; i < source.size(); i++) {
            if (!source.get(i).equals(destination.get(i))) {
                throw new IllegalStateException(
                        String.format("Collections diverge from symbol #%s (%s, %s)",
                                i,
                                source.get(i),
                                destination.get(i)
                        )
                );
            }
        }
        return true;
    }

    static class Result {

        boolean isOk;
        String testName;
        Exception message;

        public Result(boolean isOk, String testName) {
            this.isOk = isOk;
            this.testName = testName;
        }

        public Result(boolean isOk, String testName, Exception message) {
            this.isOk = isOk;
            this.testName = testName;
            this.message = message;
        }

        @Override
        public String toString() {
            if (isOk) {
                return String.format("Test %s is OK", testName);
            } else {
                return String.format("Test %s is FAILED: %s", testName, message == null ? "" : message.toString());
            }
        }
    }
}
