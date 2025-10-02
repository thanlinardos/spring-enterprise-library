package com.thanlinardos.spring_enterprise_library.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Data
public class Interval implements Comparable<Interval> {

    public static final LocalDate MIN_DATE = LocalDate.MIN;
    public static final LocalDate MAX_DATE = LocalDate.MAX;
    public static final Comparator<Interval> INTERVAL_COMPARATOR = Comparator.comparing(Interval::getStartNullAsMin).thenComparing(Interval::getEndNullAsMax);

    @Nullable
    private LocalDate start;
    @Nullable
    private LocalDate end;

    public Interval(@Nullable LocalDate start, @Nullable LocalDate end) {
        if (isNotValid(start, end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        this.start = start;
        this.end = end;
    }

    private boolean isNotValid(@Nullable LocalDate start, @Nullable LocalDate end) {
        return start != null && end != null && start.isAfter(end);
    }

    private Interval(DateOfInterval start, DateOfInterval end) {
        this(start.date(), end.date());
    }


    public LocalDate getStartNullAsMin() {
        return start == null ? MIN_DATE : start;
    }

    public LocalDate getEndNullAsMax() {
        return end == null ? MAX_DATE : end;
    }

    public Interval getStartEndAsNull() {
        start = start == MIN_DATE ? null : start;
        end = end == MAX_DATE ? null : end;
        return this;
    }

    private static Collection<Pair<LocalDate, Integer>> getAllDates(Collection<Interval> intervals) {
        return intervals.stream()
                .map(interval -> List.of(Pair.of(interval.getStartNullAsMin(), 1), Pair.of(interval.getEndNullAsMax(), -1)))
                .flatMap(Collection::stream)
                .distinct()
                .sorted(Comparator.comparing(Pair::first))
                .toList();
    }

    public static Collection<Interval> split(Collection<Interval> intervals) {
        if (intervals.size() < 2) {
            return intervals;
        }
        List<Interval> sortedIntervals = intervals.stream()
                .distinct()
                .sorted()
                .toList();
        Stack<DateOfInterval> stack = Stream.concat(getStartDateStream(sortedIntervals), getEndDateStream(sortedIntervals))
                .sorted(Comparator.reverseOrder())
                .collect(Stack::new, Stack::push, Stack::addAll);

        List<Interval> result = new ArrayList<>();
        while (stack.size() > 1) {
            DateOfInterval startDate = stack.pop();
            DateOfInterval endDate;
            DateOfInterval dateOfInterval = stack.pop();
            if (startDate.isEnd() && dateOfInterval.isStart() && !getStackSpan(stack).contains(startDate.date())) {
                stack.push(dateOfInterval);
                continue;
            }

            LocalDate date = dateOfInterval.date();
            if (date.isAfter(startDate.date()) && !dateOfInterval.equalsInterval(startDate) && !stack.isEmpty()) {
                if (dateOfInterval.isStart()) {
                    endDate = new DateOfInterval(date.minusDays(1), dateOfInterval.interval().copyWithStartPlusDays(-1));
                    stack.push(dateOfInterval);
                }
                else {
                    endDate = new DateOfInterval(date, dateOfInterval.interval());
                    stack.push(new DateOfInterval(date.plusDays(1), dateOfInterval.interval().copyWithEndPlusDays(1)));
                }
            } else {
                endDate = dateOfInterval;
                stack.push(new DateOfInterval(date.plusDays(1), dateOfInterval.isStart() ? dateOfInterval.interval().copyWithStartPlusDays(1) : dateOfInterval.interval().copyWithEndPlusDays(1)));
            }
            result.add(new Interval(startDate, endDate));
        }
        return result;
    }

    private static Interval getStackSpan(Stack<DateOfInterval> stack) {
        LocalDate minDate = stack.stream()
                .map(DateOfInterval::interval)
                .map(Interval::getStartNullAsMin)
                .min(LocalDate::compareTo)
                .orElse(MIN_DATE);
        LocalDate maxDate = stack.stream()
                .map(DateOfInterval::interval)
                .map(Interval::getEndNullAsMax)
                .max(LocalDate::compareTo)
                .orElse(MAX_DATE);
        return new Interval(minDate, maxDate);
    }

    private static Stream<DateOfInterval> getEndDateStream(List<Interval> sortedIntervals) {
        return sortedIntervals.stream()
                .map(interval -> new DateOfInterval(interval.getEndNullAsMax(), interval));
    }

    private static Stream<DateOfInterval> getStartDateStream(List<Interval> sortedIntervals) {
        return sortedIntervals.stream()
                .map(interval -> new DateOfInterval(interval.getStartNullAsMin(), interval));
    }

    public Interval copyWithStartPlusDays(int days) {
        return new Interval(start == null ? null : start.plusDays(days), end);
    }

    public Interval copyWithEndPlusDays(int days) {
        return new Interval(start, end == null ? null : end.plusDays(days));
    }

    private record DateOfInterval(@Nonnull LocalDate date, @Nonnull Interval interval) implements Comparable<DateOfInterval> {

        @Override
        public int compareTo(@Nonnull Interval.DateOfInterval o) {
            return date().compareTo(o.date());
        }

        private boolean equalsInterval(@Nonnull Interval.DateOfInterval o) {
            return interval().equals(o.interval());
        }

        private boolean isStart() {
            return interval().getStartNullAsMin().isEqual(date());
        }

        private boolean isEnd() {
            return interval().getEndNullAsMax().isEqual(date());
        }
    }

    public boolean containsNullAsMin(@Nullable LocalDate date) {
        LocalDate nonNullDate = date == null ? MIN_DATE : date;
        return (start == null || !nonNullDate.isBefore(start)) && (end == null || !nonNullDate.isAfter(end));
    }

    public boolean containsNullAsMax(@Nullable LocalDate date) {
        LocalDate nonNullDate = date == null ? MAX_DATE : date;
        return (start == null || !nonNullDate.isBefore(start)) && (end == null || !nonNullDate.isAfter(end));
    }

    public boolean contains(@Nonnull LocalDate date) {
        return (start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end));
    }

    @Override
    public int compareTo(@Nonnull Interval interval) {
        return INTERVAL_COMPARATOR.compare(this, interval);
    }
}
