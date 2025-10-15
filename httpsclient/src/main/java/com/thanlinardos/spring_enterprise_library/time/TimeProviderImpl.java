package com.thanlinardos.spring_enterprise_library.time;

import com.thanlinardos.spring_enterprise_library.time.api.TimeProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link TimeProvider} interface.
 *
 * @param zoneId      the time zone to use for date and time operations.
 * @param accuracy    the default accuracy to use for date and time operations.
 * @param maxDate     the maximum date that can be used in the system.
 * @param minDate     the minimum date that can be used in the system.
 * @param maxDateTime the maximum date time that can be used in the system.
 * @param minDateTime the minimum date time that can be used in the system.
 */
public record TimeProviderImpl(ZoneId zoneId, TimeUnit accuracy, LocalDate maxDate, LocalDate minDate,
                               LocalDateTime maxDateTime, LocalDateTime minDateTime) implements TimeProvider {

    @Override
    public ChronoUnit getChronoAccuracy() {
        return accuracy.toChronoUnit();
    }

    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(zoneId);
    }

    @Override
    public LocalDate getCurrentDate() {
        return getCurrentDateTime().toLocalDate();
    }

    @Override
    public long getCurrentTimeMillis() {
        return toMillis(getCurrentDateTime());
    }

    @Override
    public LocalDateTime fromMillis(long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), zoneId);
    }

    @Override
    public long toMillis(LocalDateTime ldt) {
        return ldt.atZone(zoneId).toInstant().toEpochMilli();
    }

    @Override
    public LocalDateTime getEndOfDay(LocalDate date) {
        var start = getStartOfDay(date);
        return start.isAfter(LocalDateTime.MIN) ? start.minusSeconds(1).plusDays(1) : start.plusDays(1).minusSeconds(1);
    }

    @Override
    public LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    @Override
    public LocalDate getFirstDayOfQuarter(LocalDate dateInQuarter) {
        var result = getCurrentDate();
        return result
                .withYear(dateInQuarter.getYear())
                .with(dateInQuarter.getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());
    }

    @Override
    public LocalDate getLastDayOfQuarter(LocalDate dateInQuarter) {
        var result = getFirstDayOfQuarter(dateInQuarter);
        return result.plusMonths(2)
                .with(TemporalAdjusters.lastDayOfMonth());
    }

    @Override
    public LocalDate getFirstDayOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    @Override
    public LocalDate getLastDayOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

}
