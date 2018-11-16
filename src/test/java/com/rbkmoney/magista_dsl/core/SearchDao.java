package com.rbkmoney.magista_dsl.core;

import java.util.*;
import java.util.stream.Collectors;

public class SearchDao {

    public static final List<Dummy> allDummies = Arrays.asList(
            new Dummy(1L, "kek", "2016-03-22T06:12:27Z", "2016-03-22T06:12:27Z"),
            new Dummy(2L, "kek", "2016-03-22T06:12:27Z", "2016-03-22T06:12:27Z"),
            new Dummy(3L, "kek", "2016-03-22T06:12:27Z", "2016-03-22T06:12:27Z"),
            new Dummy(4L, "kek", "2016-03-22T06:12:27Z", "2016-03-22T06:12:27Z")
            );
    public Collection<Map.Entry<Long, Dummy>> getDummies(String id) {
        return allDummies.stream().filter(d -> id.equals(d.getId().toString())).map(d -> new AbstractMap.SimpleEntry<>(d.getId(), d)).collect(Collectors.toList());
    }
}
