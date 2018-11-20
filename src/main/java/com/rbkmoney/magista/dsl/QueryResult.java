package com.rbkmoney.magista.dsl;

import java.util.stream.Stream;

public interface QueryResult<T, CT> {

    Stream<T> getDataStream();

    CT getCollectedStream();

}

