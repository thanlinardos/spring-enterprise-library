package com.thanlinardos.spring_enterprise_library.objects.utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * A utility class for object-related operations.
 */
public abstract class ObjectUtils {

    private ObjectUtils() {
    }

    /**
     * Checks if the given object and all other objects are not null and equal to the given object.
     *
     * @param object the object to compare against.
     * @param others the other objects to check.
     * @return true if the given object and all other objects are not null and equal to the given object, false otherwise.
     */
    public static boolean isAllObjectsNotNullAndEquals(Object object, Object... others) {
        if (object == null) {
            return false;
        } else {
            return Arrays.stream(others)
                    .noneMatch(other -> isNullOrNotEqual(other, object));
        }
    }

    private static boolean isNullOrNotEqual(Object object, Object equalTo) {
        return object == null || !Objects.equals(object, equalTo);
    }
}
