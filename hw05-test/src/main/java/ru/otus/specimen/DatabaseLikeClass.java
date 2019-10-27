package ru.otus.specimen;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseLikeClass {

    List<String> someInfo;

    public DatabaseLikeClass() {
        someInfo = new LinkedList<>();
    }

    public void put(String... data) {
        someInfo.addAll(List.of(data));
    }

    public void put(List<String> data) {
        someInfo.addAll(data);
    }

    public List<String> read() {
        return ImmutableList.copyOf(someInfo);
    }

    public void reset() {
        this.someInfo = new ArrayList<>();
    }

    public int size() {
       return someInfo.size();
    }
}
