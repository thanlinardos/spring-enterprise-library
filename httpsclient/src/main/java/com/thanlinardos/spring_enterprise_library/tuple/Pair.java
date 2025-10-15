package com.thanlinardos.spring_enterprise_library.tuple;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tuple of things.
 *
 * @param first  The first thing.
 * @param second The second thing.
 * @param <S>    Type of the first thing.
 * @param <T>    Type of the second thing.
 */
public record Pair<S, T>(S first, T second) {

    /**
     * Creates a new {@link Pair} for the given elements.
     *
     * @param first  must not be {@literal null}.
     * @param second must not be {@literal null}.
     */
    public Pair {
        Assert.notNull(first, "First must not be null");
        Assert.notNull(second, "Second must not be null");
    }

    /**
     * Creates a new {@link Pair} for the given elements.
     *
     * @param first  must not be {@literal null}.
     * @param second must not be {@literal null}.
     * @param <S>    Type of the first thing.
     * @param <T>    Type of the second thing.
     * @return the {@link Pair}.
     */
    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair<>(first, second);
    }

    /**
     * A collector to create a {@link Map} from a {@link Stream} of {@link Pair}s.
     *
     * @param <S>  Type of the first thing.
     * @param <T>  Type of the second thing.
     * @return the {@link Collector} to create a {@link Map}.
     */
    public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap() {
        return Collectors.toMap(Pair::first, Pair::second);
    }

    @Override
    public boolean equals(@Nullable Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Pair<?, ?>(Object first1, Object second1))) {
            return false;
        }

        if (!Objects.equals(first, first1)) {
            return false;
        }

        return Objects.equals(second, second1);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(first);
        result = 31 * result + Objects.hashCode(second);
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return String.format("%s->%s", this.first, this.second);
    }
}
