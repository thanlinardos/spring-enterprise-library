package com.thanlinardos.spring_enterprise_library.utils;

import java.util.Arrays;
import java.util.Objects;

public abstract class ObjectUtils {

    private ObjectUtils() {
    }

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
