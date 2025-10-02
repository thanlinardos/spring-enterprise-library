package com.thanlinardos.spring_enterprise_library.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class IntervalTest {

    private static final Interval JAN_2024 = new Interval(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
    private static final Interval FEB_2024 = new Interval(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29));
    private static final Interval MAR_2024 = new Interval(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31));
    private static final Interval APR_2024 = new Interval(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 30));
    private static final Interval JUN_2024 = new Interval(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30));

    private static final Interval JAN_TO_MAR_2024 = new Interval(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31));
    private static final Interval FEB_TO_APR_2024 = new Interval(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 4, 30));
    private static final Interval MAR_TO_JUL_2024 = new Interval(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 7, 31));
    private static final Interval MAY_TO_JUL_14_2024 = new Interval(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 7, 14));
    private static final Interval JUL_15_TO_31_2025 = new Interval(LocalDate.of(2024, 7, 15), LocalDate.of(2024, 7, 31));
    private static final Interval JUL_15_TO_DEC_2025 = new Interval(LocalDate.of(2024, 7, 15), LocalDate.of(2025, 12, 31));
    private static final Interval AUG_TO_DEC_2025 = new Interval(LocalDate.of(2024, 8, 1), LocalDate.of(2025, 12, 31));
    private static final Interval FEB_TO_MAR_2024 = new Interval(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 3, 31));

    public static Stream<Arguments> splitFactory() {
        return Stream.of(
                Arguments.of("Empty", Collections.emptyList(), Collections.emptyList()),
                Arguments.of("Single", Collections.singletonList(JAN_2024), Collections.singletonList(JAN_2024)),
                Arguments.of("Single null", Collections.singletonList(new Interval(null, null)), Collections.singletonList(new Interval(null, null))),
                Arguments.of("Single start", Collections.singletonList(new Interval(JAN_2024.getStart(), null)), Collections.singletonList(new Interval(JAN_2024.getStart(), null))),
                Arguments.of("Single end", Collections.singletonList(new Interval(null, JAN_2024.getEnd())), Collections.singletonList(new Interval(null, JAN_2024.getEnd()))),
                Arguments.of("2 touching", List.of(JAN_2024, FEB_2024), List.of(JAN_2024, FEB_2024)),
                Arguments.of("2 overlapping", List.of(JAN_TO_MAR_2024, FEB_TO_APR_2024), List.of(JAN_2024, FEB_TO_MAR_2024, APR_2024)),
                Arguments.of("4 overlapping", List.of(JAN_TO_MAR_2024, FEB_TO_APR_2024, MAR_TO_JUL_2024, JUL_15_TO_DEC_2025), List.of(JAN_2024, FEB_2024, MAR_2024, APR_2024, MAY_TO_JUL_14_2024, JUL_15_TO_31_2025, AUG_TO_DEC_2025)),
                Arguments.of("2 overlapping & 1 gap", List.of(JAN_TO_MAR_2024, FEB_TO_APR_2024, JUN_2024), List.of(JAN_2024, FEB_TO_MAR_2024, APR_2024, JUN_2024))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("splitFactory")
    void split(String description, Collection<Interval> input, Collection<Interval> expected) {
        Collection<Interval> actual = Interval.split(input);
        Assertions.assertEquals(expected, actual);
    }
}