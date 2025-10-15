package com.thanlinardos.spring_enterprise_library.time.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.concurrent.TimeUnit;

/**
 * Configuration properties for the TimeProvider.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "thanlinardos.springenterpriselibrary.time")
public class TimeProviderProperties {

    /**
     * Used to override the time zone of the application.
     *
     */
    private static final String DEFAULT_TIME_ZONE = ZoneId.systemDefault().toString();

    /**
     * Used to override the min and max date / datetime of the application.
     * Do not override this unless strictly necessary.
     *
     */
    private static final LocalDate MAX_DATE = LocalDate.MAX;
    private static final LocalDate MIN_DATE = LocalDate.MIN;
    private static final LocalDateTime MAX_DATE_TIME = LocalDateTime.MAX;
    private static final LocalDateTime MIN_DATE_TIME = LocalDateTime.MIN;
    private static final String DEFAULT_TIME_ACCURACY = "MILLISECONDS";

    private String timeZone = DEFAULT_TIME_ZONE;
    private String accuracy = DEFAULT_TIME_ACCURACY;
    private LocalDate maxDate = MAX_DATE;
    private LocalDate minDate = MIN_DATE;
    private LocalDateTime maxDateTime = MAX_DATE_TIME;
    private LocalDateTime minDateTime = MIN_DATE_TIME;

    /**
     * Default constructor.
     */
    public TimeProviderProperties() {
        // Default constructor
    }

    /**
     * Returns the time zone as a ZoneId object.
     *
     * @return the time zone as a ZoneId object.
     * @throws DateTimeException  if the zone ID has an invalid format.
     * @throws ZoneRulesException if the zone ID is a region ID that cannot be found.
     */
    public ZoneId getTimeZoneId() {
        return ZoneId.of(timeZone);
    }

    /**
     * Returns the accuracy as a TimeUnit enum.
     *
     * @return the accuracy as a TimeUnit enum.
     * @throws IllegalArgumentException if the accuracy is not a valid TimeUnit.
     */
    public TimeUnit getAccuracy() {
        return TimeUnit.valueOf(accuracy);
    }
}
