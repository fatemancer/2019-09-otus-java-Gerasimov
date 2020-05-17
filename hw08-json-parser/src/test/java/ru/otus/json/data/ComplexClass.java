package ru.otus.json.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class ComplexClass {
    Collection<SimpleClass> classList = List.of(
            new SimpleClass(1, 1.0f, 2.0d, 'a', "st1"),
            new SimpleClass(2, 2.0f, 4.0d, 'b', "st2")
    );
    SimpleClass[] classArray = new SimpleClass[] {
        new SimpleClass(3, 1.0f, 2.0d, 'a', "st1"), new SimpleClass(4, 2.0f, 4.0d, 'b', "st2")
    };
    int[] ints = new int[]{5, 6, 7};
    Set<Long> longs = Set.of(5L, 6L, 8L);
    Long[] longsArray = { 2L, 3L, 4L};
    int anInt;
    Integer objInt = 3;
    Integer nullInt;
}
