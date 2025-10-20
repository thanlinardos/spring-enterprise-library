package com.thanlinardos.spring_enterprise_library.math;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@CoreTest
class UIntTest {

    public static Stream<Arguments> rangeUIntFactory() {
        return LongStream.range(2, 100)
                .mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, 1, -1, -100, -4294967295L, UInt.MIN_VALUE, UInt.MAX_VALUE, 131435L})
    @MethodSource("rangeUIntFactory")
    void testUInt_zero(Long value) {
        if (value == null) {
            assertThrows(NullPointerException.class, () -> getUInt(null));
        } else if (value < UInt.MIN_VALUE || value > UInt.MAX_VALUE) {
            assertThrows(IllegalArgumentException.class, () -> getUInt(value));
        } else {
            UInt uInt = getUInt(value);
            assertEquals(value, uInt.longValue());
        }
    }

    private UInt getUInt(Long value) {
        return new UInt(value);
    }
}