package com.thanlinardos.spring_enterprise_library.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class ObjectUtilsTest {

    private static Stream<Arguments> isAllObjectsNotNullAndEqualsParams() {
        return Stream.of(
                Arguments.argumentSet("2 nulls", false, null, null, null),
                Arguments.argumentSet("1 null, 1 not null", false, null, new Object[]{LocalDate.MAX}),
                Arguments.argumentSet("1 not null, 1 null", false, LocalDate.MAX, new Object[]{null}),
                Arguments.argumentSet("1 not null, 1 not equal", false, LocalDate.MAX, new Object[]{LocalDate.MIN}),
                Arguments.argumentSet("1 not null, 1 equal", true, LocalDate.MAX, new Object[]{LocalDate.MAX}),
                Arguments.argumentSet("1 not null, 2 equal", true, LocalDate.MAX, new Object[]{LocalDate.MAX, LocalDate.MAX}),
                Arguments.argumentSet("1 not null, 2 not equal", false, LocalDate.MAX, new Object[]{LocalDate.MIN, LocalDate.of(2024, 1, 1)}),
                Arguments.argumentSet("1 not null, 1 equal, 1 not equal", false, LocalDate.MAX, new Object[]{LocalDate.MAX, LocalDate.MIN})
        );
    }

    @ParameterizedTest
    @MethodSource("isAllObjectsNotNullAndEqualsParams")
    void isAllObjectsNotNullAndEquals(boolean expected, Object object, Object... others) {
        boolean actual = ObjectUtils.isAllObjectsNotNullAndEquals(object, others);
        Assertions.assertEquals(expected, actual);
    }
}