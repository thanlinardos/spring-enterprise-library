package com.thanlinardos.spring_enterprise_library.model;

import com.thanlinardos.spring_enterprise_library.utils.DateUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.thanlinardos.spring_enterprise_library.utils.DateUtils.addDay;
import static com.thanlinardos.spring_enterprise_library.utils.DateUtils.subtractDay;
import static com.thanlinardos.spring_enterprise_library.utils.ObjectUtils.isAllObjectsNotNullAndEquals;

/**
 * Represents a time interval with a start and end date.
 * Provides utility methods for creating, manipulating, and analyzing intervals.
 */
@Data
public class Interval implements Comparable<Interval> {

    public static final LocalDate MIN_DATE = LocalDate.MIN;
    public static final LocalDate MAX_DATE = LocalDate.MAX;

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

    public Interval(YearMonth yearMonth) {
        this(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public Interval(Year year) {
        this(year.atDay(1), DateUtils.getLastDayOfYear(year));
    }

    /**
     * Constructs an {@link Interval} using ISO 8601 formatted dates ("YYYY-MM-DD")
     *
     * @param startDate start of the interval (inclusive)
     * @param endDate   end of the interval (inclusive)
     * @return an {@link Interval} based on the given dates
     */
    public static Interval forIsoDates(@Nullable String startDate, @Nullable String endDate) {
        return new Interval(parseDate(startDate, LocalDate::parse), parseDate(endDate, LocalDate::parse));
    }

    private static LocalDate parseDate(String date, Function<String, LocalDate> parser) {
        return StringUtils.hasLength(date) ? parser.apply(date) : null;
    }

    /**
     * Constructs an {@link Interval} using a ISO 8601 formatted month ("YYYY-MM").
     *
     * @param month a string representing the month (e.g. "2025-07")
     * @return an {@link Interval} based on the given month
     */
    public static Interval forIsoMonth(@Nonnull String month) {
        return new Interval(YearMonth.parse(month));
    }

    /**
     * Constructs an {@link Interval} using a year.
     *
     * @param year an int representing the year (e.g. 2025)
     * @return an {@link Interval} based on the given year
     */
    public static Interval forIsoYear(int year) {
        return new Interval(Year.of(year));
    }

    public LocalDate getStartNullAsMin() {
        return start == null ? MIN_DATE : start;
    }

    public LocalDate getEndNullAsMax() {
        return end == null ? MAX_DATE : end;
    }

    /**
     * Returns a sorted list of intervals, covering the same days as the input, but without any intervals overlapping.
     * <pre>
     * Input:
     * [-----------]    [-------------][------][----------------]    [---]
     *        [---------------]                    [---][---]
     * Output:
     * [-----][----][--][-----][------][------][--][---][---][--]    [---]
     * </pre>
     *
     * @param intervals collection of intervals to normalize
     * @return split list of intervals
     */
    public static List<Interval> split(Collection<Interval> intervals) {
        if (intervals.size() <= 1) {
            return new ArrayList<>(intervals);
        }

        // generate start dates from end dates, and vice versa, selecting only those that overlap with the given intervals, with start/end-specific sorting
        List<LocalDate> startDates = getSortedDatesWithFunctionsPredicateAndComparator(intervals, Interval::getStart, i -> addDay(i.getEnd()),
                DateUtils.NULL_AS_MIN_COMPARATOR, Interval::containsNullAsMin);
        List<LocalDate> endDates = getSortedDatesWithFunctionsPredicateAndComparator(intervals, Interval::getEnd, i -> subtractDay(i.getStart()),
                DateUtils.NULL_AS_MAX_COMPARATOR, Interval::containsNullAsMax);

        if (startDates.size() != endDates.size()) {
            throw new IllegalStateException(String.format("Unable to split collection of intervals: %s", intervals));
        }

        List<Interval> splittedIntervals = new ArrayList<>();
        for (int i = 0; i < startDates.size(); i++) {
            splittedIntervals.add(new Interval(startDates.get(i), endDates.get(i)));
        }
        return splittedIntervals;
    }

    private static List<LocalDate> getSortedDatesWithFunctionsPredicateAndComparator(Collection<Interval> intervals,
                                                                                     Function<Interval, LocalDate> keepNull,
                                                                                     Function<Interval, LocalDate> discardNull,
                                                                                     Comparator<LocalDate> comparator,
                                                                                     BiPredicate<Interval, LocalDate> predicate) {
        return intervals.stream()
                .flatMap(interval -> Stream.concat(
                        Stream.of(keepNull.apply(interval)),
                        Stream.ofNullable(discardNull.apply(interval))))
                .filter(date -> isAnyMatchForPredicateOnDate(intervals, predicate, date))
                .distinct()
                .sorted(comparator)
                .toList();
    }

    private static boolean isAnyMatchForPredicateOnDate(Collection<Interval> intervals, BiPredicate<Interval, LocalDate> predicate, LocalDate date) {
        return intervals.stream()
                .anyMatch(interval -> predicate.test(interval, date));
    }

    private boolean hasNullStart() {
        return start == null;
    }

    private boolean hasNullEnd() {
        return end == null;
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

    public boolean contains(@Nonnull YearMonth yearMonth) {
        return contains(new Interval(yearMonth));
    }

    public boolean contains(@Nonnull Interval interval) {
        return (hasNullStart() || (interval.hasNullStart() || !DateUtils.isBeforeNullAsMin(interval.getStart(), getStart())))
                && (hasNullEnd() || (interval.hasNullEnd() || !DateUtils.isAfterNullAsMax(interval.getEnd(), getEnd())));
    }

    /**
     * @param interval a given {@link Interval}
     * @return true if this {@link Interval} overlaps the given Interval, otherwise false
     */
    public boolean overlaps(@Nonnull Interval interval) {
        return (hasNullStart() || !DateUtils.isAfterNullAsMax(getStart(), interval.getEnd()))
                && (hasNullEnd() || !DateUtils.isBeforeNullAsMin(getEnd(), interval.getStart()));
    }

    /**
     * @param intervals a collection of {@link Interval}
     * @return true if this {@link Interval} overlaps with any of the given intervals, otherwise false
     */
    public boolean overlaps(Collection<Interval> intervals) {
        return intervals.stream()
                .anyMatch(this::overlaps);
    }

    /**
     * Returns true if this {@link Interval} overlaps with the given yearMonth, otherwise false.
     *
     * @param yearMonth a {@link YearMonth}
     * @return true if this {@link Interval} overlaps with the given yearMonth, otherwise false
     */
    public boolean overlaps(@Nonnull YearMonth yearMonth) {
        return overlaps(new Interval(yearMonth));
    }

    /**
     * Returns true if any of the given intervals overlap with each other, otherwise false
     *
     * @param intervals a list of intervals
     * @return true if any of the given intervals overlap with each other
     */
    public static boolean anyOverlaps(Collection<Interval> intervals) {
        return intervals.stream()
                .anyMatch(interval -> interval.overlaps(interval.relativeComplement(intervals)));
    }

    /**
     * Returns the list of the given intervals, excluding any elements equal to this interval
     *
     * @param intervals a list of intervals
     * @return the result of removing any intervals equal to this interval
     */
    public List<Interval> relativeComplement(Collection<Interval> intervals) {
        return intervals.stream()
                .filter(Predicate.not(this::equals))
                .toList();
    }

    /**
     * @param interval a given {@link Interval}
     * @return true if this {@link Interval} overlaps the given Interval and neither Interval contains the other, otherwise false
     */
    public boolean partiallyOverlaps(@Nonnull Interval interval) {
        return this.overlaps(interval) && !this.contains(interval) && !interval.contains(this);
    }

    /**
     * Returns the overlap between this Interval and the given interval, otherwise an empty Optional if there is no overlap
     * <pre>
     * This interval:
     * |--------------------------------------|
     * Input:
     *        |---------------|
     * Output:
     *        |---------------|
     * </pre>
     *
     * @param interval a given {@link Interval}
     * @return the overlap between this Interval and the given interval, otherwise an empty Optional if there is no overlap
     */
    public Optional<Interval> getOverlap(@Nonnull Interval interval) {
        if (overlaps(interval)) {
            return Optional.of(new Interval(
                    DateUtils.maxNullAsMin(this.getStart(), interval.getStart()),
                    DateUtils.minNullAsMax(this.getEnd(), interval.getEnd())
            ));
        }
        return Optional.empty();
    }

    /**
     * Returns a new version of this interval, where the end date is bounded according to the given {@code upperBound}.
     * Equivalent to calling {@link Interval#getOverlap(Interval)} with an interval starting from null and ending in {@code upperBound}.
     *
     * @param upperBound an interval defining the bounds to apply to this interval
     * @return a new version of this interval, bounded according to given {@code upperBound}, or an empty Optional if the bounding results in an invalid interval
     */
    public Optional<Interval> boundEndDate(@Nullable LocalDate upperBound) {
        return getOverlap(new Interval(null, upperBound));
    }

    /**
     * Returns a new version of this interval, where the start date is bounded according to the given {@code lowerBound}
     * Equivalent to calling {@link Interval#getOverlap(Interval)} with an interval starting from {@code lowerBound} and ending in null.
     *
     * @param lowerBound a date defining the lower bound to apply to this interval
     * @return a new version of this interval, bounded according to given {@code lowerBound}, or an empty Optional if the bounding results in an invalid interval
     */
    public Optional<Interval> boundStartDate(@Nullable LocalDate lowerBound) {
        return getOverlap(new Interval(lowerBound, null));
    }

    /**
     * Set the start date of this interval to the input {@code lowerBound} according to {@link Interval#boundStartDate(LocalDate)}
     * If the bounding results in an invalid interval, it doesn't change the original start date of this interval.
     *
     * @param lowerBound a date defining the lower bound to apply to this interval.
     */
    public void boundAndSetStartDateIfValid(@Nullable LocalDate lowerBound) {
        boundStartDate(lowerBound)
                .map(Interval::getStart)
                .ifPresent(this::setStart);
    }

    /**
     * Returns a normalized (see {@link Interval#normalize(Collection)}) list of overlaps between the given intervals and this interval
     * <pre>
     * This interval:
     * |--------------------------------|
     * Input:
     *        |---------|           |------|
     *             |-------||--|
     * Output:
     *        |----------------|    |---|
     * </pre>
     *
     * @param intervals intervals to determine overlaps for
     * @return a normalized list of overlaps between the given intervals and this interval
     */
    public List<Interval> getOverlaps(Collection<Interval> intervals) {
        return getOverlaps(intervals, true);
    }

    /**
     * Vararg variant of {@link Interval#getOverlaps(Collection)}
     *
     * @param intervals intervals to determine overlaps for
     * @return a normalized list of overlaps between the given intervals and this interval
     */
    public List<Interval> getOverlaps(Interval... intervals) {
        return getOverlaps(List.of(intervals));
    }

    /**
     * Returns a normalized (see {@link Interval#normalize(Collection)}) list of overlaps between the given intervals and this interval
     * Whether to merge adjacent overlaps is subject to the value of {@code mergeAdjacentIntervals}
     * <pre>
     * This interval:
     * |--------------------------------|
     * Input:
     *        |---------|           |------|
     *             |-------||--|
     * Output:
     *        |----------------|    |---|      (if mergeAdjacentIntervals = true)
     * or
     *        |------------||--|    |---|      (if mergeAdjacentIntervals = false)
     * </pre>
     *
     * @param intervals              intervals to determine overlaps for
     * @param mergeAdjacentIntervals whether to merge adjacent overlaps
     * @return a normalized list of overlaps between the given intervals and this interval
     */
    public List<Interval> getOverlaps(Collection<Interval> intervals, boolean mergeAdjacentIntervals) {
        List<Interval> overlaps = intervals.stream()
                .map(this::getOverlap)
                .flatMap(Optional::stream)
                .toList();
        return normalize(overlaps, mergeAdjacentIntervals);
    }

    /**
     * Returns a normalized list of the portions of {@code intervals} that do not overlap with this interval
     * <pre>
     * This interval:
     *                |---------------|
     * Input:
     *        |---------|         |------|
     *             |---------|
     * Output:
     *        |------|                 |-|
     * </pre>
     *
     * @param intervals list of intervals
     * @return the portions of {@code intervals} that do not overlap with this interval
     */
    public List<Interval> getNotOverlaps(Collection<Interval> intervals) {
        List<Interval> overlaps = this.getOverlaps(intervals);
        List<Interval> notOverlaps = intervals.stream()
                .map(interval -> interval.subtract(overlaps))
                .flatMap(Collection::stream)
                .toList();
        return normalize(notOverlaps);
    }

    /**
     * Vararg variant of {@link Interval#getNotOverlaps(Collection)}
     *
     * @param intervals given intervals
     * @return the portions of {@code intervals} that do not overlap with this interval
     */
    public List<Interval> getNotOverlaps(Interval... intervals) {
        return getNotOverlaps(List.of(intervals));
    }

    /**
     * Returns a sorted list of intervals, covering the same days as the input, but without any intervals overlapping.
     * Intervals that start just after the previous one ends will also be merged.
     * <pre>
     * Input:
     * [-----------]    [-------------][------][----------------]    [---]
     *        [---------------]                    [---][---]
     * Output:
     * [--------------------------------------------------------]    [---]
     * </pre>
     *
     * @param intervals collection of intervals to normalize
     * @return normalized list of intervals
     */
    public static List<Interval> normalize(Collection<Interval> intervals) {
        return normalize(intervals, true);
    }

    /**
     * Returns a sorted list of intervals, covering the same days as the input, but without any intervals overlapping.
     * <pre>
     * Input:
     * [-----------]    [-------------][------][----------------]    [---]
     *        [---------------]                    [---][---]
     * Output:
     * [--------------------------------------------------------]    [---]   (if mergeAdjacentIntervals = true)
     * or
     * [------------------------------][------][----------------]    [---]   (if mergeAdjacentIntervals = false)
     * </pre>
     *
     * @param intervals              collection of intervals to normalize
     * @param mergeAdjacentIntervals whether to merge adjacent intervals
     * @return normalized list of intervals
     */
    public static List<Interval> normalize(Collection<Interval> intervals, boolean mergeAdjacentIntervals) {
        if (intervals.size() <= 1) {
            return new ArrayList<>(intervals);
        }

        List<Interval> sortedIntervals = sort(intervals);
        List<Interval> result = new ArrayList<>();
        Interval current = sortedIntervals.getFirst();

        for (Interval interval : sortedIntervals) {
            if (shouldMergeCurrentIntervalWithNextInterval(current, interval, mergeAdjacentIntervals)) {
                current = new Interval(current.getStart(), DateUtils.maxNullAsMax(current.getEnd(), interval.getEnd()));
            } else {
                result.add(current);
                current = interval;
            }
        }
        result.add(current);

        return result.stream()
                .distinct()
                .toList();
    }

    private static boolean shouldMergeCurrentIntervalWithNextInterval(Interval current, Interval next, boolean mergeAdjacentIntervals) {
        LocalDate adjustedEndDate = mergeAdjacentIntervals ? addDay(current.getEnd()) : current.getEnd();
        return !DateUtils.isAfterNullAsMax(next.getStart(), adjustedEndDate);
    }

    private static List<Interval> sort(Collection<Interval> intervals) {
        return intervals.stream()
                .sorted()
                .toList();
    }

    /**
     * Checks if the given {@link Interval} is touching this Interval.
     *
     * @param interval a given {@link Interval}
     * @return true if this Interval ends on the day before the given Interval starts or starts on the day after the given Interval ends, otherwise false.
     */
    public boolean adjacent(@Nonnull Interval interval) {
        return isAllObjectsNotNullAndEquals(subtractDay(getStart()), interval.getEnd())
                || isAllObjectsNotNullAndEquals(getEnd(), subtractDay(interval.getStart()));
    }

    /**
     * Returns the remainder of this interval, after subtracting the overlap with the given interval
     * <pre>
     * This:
     * |---------------------------|
     * Input:
     *        |----|
     * Output:
     * |-----|      |--------------|
     * </pre>
     *
     * @param interval a given interval to subtract with
     * @return list of intervals after subtracting the given {@code interval} from this {@link Interval}
     */
    public List<Interval> subtract(Interval interval) {
        return this.subtract(List.of(interval));
    }

    /**
     * Returns the remainder of this interval, after subtracting the overlap with the given intervals
     * <pre>
     * This:
     * |---------------------------|
     * Input:
     *        |----|    |---|
     * Output:
     * |-----|      |--|     |-----|
     * </pre>
     *
     * @param intervals intervals to subtract with
     * @return list of intervals after subtracting the given {@code intervals} from this {@link Interval}
     */
    public List<Interval> subtract(Collection<Interval> intervals) {
        @Nullable LocalDate nextPossibleStartDate = this.getStart();
        boolean endCrossed = false;
        List<Interval> resultIntervals = new ArrayList<>();
        for (Interval overlap : this.getOverlaps(intervals)) {
            if (DateUtils.isBeforeNullAsMin(nextPossibleStartDate, overlap.getStart())) {
                resultIntervals.add(new Interval(
                        nextPossibleStartDate,
                        DateUtils.minNullAsMax(this.getEnd(), subtractDay(overlap.getStart())))
                );
            }
            if (DateUtils.isAfterOrEqual(overlap.getEnd(), this.getEnd())) {
                endCrossed = true;
                break;
            }
            if (DateUtils.isBeforeOrEqual(nextPossibleStartDate, overlap.getEnd())) {
                nextPossibleStartDate = addDay(overlap.getEnd());
            }
        }
        if (!endCrossed) {
            resultIntervals.add(new Interval(nextPossibleStartDate, getEnd()));
        }

        return resultIntervals;
    }

    @Override
    public int compareTo(@Nonnull Interval o) {
        int startComparisonResult = DateUtils.NULL_AS_MIN_COMPARATOR.compare(getStart(), o.getStart());
        if (startComparisonResult == 0) {
            return DateUtils.NULL_AS_MAX_COMPARATOR.compare(getEnd(), o.getEnd());
        } else {
            return startComparisonResult;
        }
    }

}
