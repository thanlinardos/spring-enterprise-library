package com.thanlinardos.spring_enterprise_library.utils;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.*;
import java.util.Objects;

public abstract class DateUtils {

    public static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
    public static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);

    private DateUtils() {
    }

    /**
     * Converts epoch milliseconds to {@link LocalDateTime} in the system default time zone {@link DateUtils#getDefaultZoneId()}.
     *
     * @param epochMilli the epoch milliseconds to convert.
     * @return the corresponding {@link LocalDateTime}.
     */
    public static LocalDateTime getLocalDateTimeFromEpochMilli(Long epochMilli) {
        return epochMilliToZonedDateTime(epochMilli).toLocalDateTime();
    }

    /**
     * Converts epoch milliseconds to {@link LocalDate} in the system default time zone {@link DateUtils#getDefaultZoneId()}.
     *
     * @param epochMilli the epoch milliseconds to convert.
     * @return the corresponding {@link LocalDate}.
     */
    public static LocalDate getLocalDateFromEpochMilli(Long epochMilli) {
        return epochMilliToZonedDateTime(epochMilli).toLocalDate();
    }

    /**
     * Converts epoch milliseconds to {@link ZonedDateTime} in the system default time zone {@link DateUtils#getDefaultZoneId()}.
     *
     * @param epochMilli the epoch milliseconds to convert.
     * @return the corresponding {@link ZonedDateTime}.
     */
    public static ZonedDateTime epochMilliToZonedDateTime(Long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(getDefaultZoneId());
    }

    /**
     * Converts {@link LocalDateTime} to epoch milliseconds in the system default time zone {@link DateUtils#getDefaultZoneId()}.
     *
     * @param localDateTime the {@link LocalDateTime} to convert.
     * @return the corresponding epoch milliseconds.
     */
    public static long getEpochMilliFromLocalDateTime(@NonNull LocalDateTime localDateTime) {
        return localDateTime.atZone(getDefaultZoneId())
                .toInstant()
                .toEpochMilli();
    }

    /**
     * Gets the system default time zone.
     *
     * @return the system default {@link ZoneId}.
     */
    @NonNull
    private static ZoneId getDefaultZoneId() {
        return ZoneId.systemDefault();
    }

    /**
     * Checks if date1 is before date2, treating null as {@link DateUtils#MIN_DATE}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is before date2, false otherwise.
     */
    public static boolean isBeforeNullAsMin(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return date1 == null || (date2 != null && date1.isBefore(date2));
    }

    /**
     * Checks if date1 is after date2, treating null as {@link DateUtils#MAX_DATE}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is after date2, false otherwise.
     */
    public static boolean isAfterNullAsMax(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return date1 == null || (date2 != null && date1.isAfter(date2));
    }

    /**
     * Checks if date1 is before or equal to date2, treating null as {@link DateUtils#MIN_DATE}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is before or equal to date2, false otherwise.
     */
    public static boolean isBeforeOrEqual(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return Objects.equals(date1, date2) || isBeforeNullAsMin(date1, date2);
    }

    /**
     * Checks if date1 is after or equal to date2, treating null as {@link DateUtils#MAX_DATE}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is after or equal to date2, false otherwise.
     */
    public static boolean isAfterOrEqual(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return Objects.equals(date1, date2) || isAfterNullAsMax(date1, date2);
    }
}
