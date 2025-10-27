package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.error.utils.ExceptionUtils;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Very useful utility methods for working with Functions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionUtils {

    /**
     * Negates the result of input function.
     *
     * @param func the function to negate.
     * @param <T>  type of the input element.
     * @return the negated {@link Function}.
     */
    public static <T> Function<T, Boolean> negateBoolean(Function<T, Boolean> func) {
        return func.andThen(b -> !b);
    }

    /**
     * Method that can be used to apply with a single method argument to a bi-function.
     *
     * @param function the function to call with the input
     * @param object   the input
     * @param <T>      type of the starting element
     * @param <O>      type of the object to be applied
     * @param <R>      type of result
     * @return a function that can be used to apply the input to the  bi-function
     */
    public static <T, O, R> Function<T, R> apply(BiFunction<T, O, R> function, O object) {
        return t -> function.apply(t, object);
    }

    /**
     * Method that chains two methods and return the combined result.
     *
     * @param func1 the first function of the chain
     * @param func2 the final function of the chain
     * @param <T1>  type of the first method
     * @param <T2>  type of the final method
     * @param <R>   type the final method returns
     * @return the method returns the result of the final function
     */
    public static <T1, T2, R> Function<T1, R> compose(Function<T1, T2> func1, Function<T2, R> func2) {
        return func1.andThen(func2);
    }

    /**
     * Method that chains two methods in a null safe wrapper and return the combined result.
     *
     * @param firstGetter the first function of the chain
     * @param finalGetter the final function of the chain
     * @param <T1>        type of the first method
     * @param <T2>        type of the final method
     * @param <R>         type the final method returns
     * @return the method returns the result of the final function
     */
    public static <T1, T2, R> Function<T1, R> composeNullSafe(Function<T1, T2> firstGetter, Function<T2, R> finalGetter) {
        return wrapWithNullHandling(firstGetter).andThen(wrapWithNullHandling(finalGetter));
    }

    /**
     * Method that chains two methods in a null safe wrapper and return the combined result.
     *
     * @param firstGetter the first function of the chain
     * @param finalGetter the final function of the chain
     * @param <T1>        type set of the first method
     * @param <T2>        type of the final method
     * @param <R>         type the final method returns
     * @return the method returns the result of the final function
     */
    public static <T1, T2, R> Function<T1, Set<R>> composeWithSetNullSafe(Function<T1, Set<T2>> firstGetter, Function<T2, R> finalGetter) {
        return wrapWithNullHandling(firstGetter).andThen(extractWithNullHandling(finalGetter));
    }

    /**
     * Method that chains two methods in a null safe wrapper and return the combined result.
     *
     * @param firstGetter the first function of the chain
     * @param finalGetter the final function of the chain
     * @param <T1>        type set of the first method
     * @param <T2>        type set of the final method
     * @param <R>         type the final method returns
     * @return the method returns the result of the final function
     */
    public static <T1, T2, R> Function<T1, Set<R>> composeAllNullSafe(Function<T1, Set<T2>> firstGetter, Function<T2, Set<R>> finalGetter) {
        return wrapWithNullHandling(firstGetter).andThen(extractSetWithNullHandling(finalGetter));
    }

    /**
     * Method that chains three methods in a null safe wrapper and return the combined result.
     *
     * @param firstGetter  the first function of the chain
     * @param secondGetter the second function of the chain
     * @param finalGetter  the final function of the chain
     * @param <T1>         type of the first method
     * @param <T2>         type of the second method
     * @param <T3>         type of the final method
     * @param <R>          type the final method returns
     * @return the method returns the result of the final function
     */
    public static <T1, T2, T3, R> Function<T1, R> composeNullSafe(Function<T1, T2> firstGetter, Function<T2, T3> secondGetter, Function<T3, R> finalGetter) {
        return wrapWithNullHandling(firstGetter).andThen(wrapWithNullHandling(secondGetter)).andThen(wrapWithNullHandling(finalGetter));
    }

    private static <T, R> Function<T, R> wrapWithNullHandling(Function<T, R> function) {
        return t -> Optional.ofNullable(t)
                .map(function)
                .orElse(null);
    }

    private static <T, R> Function<Set<T>, Set<R>> extractWithNullHandling(Function<T, R> function) {
        return t -> StreamUtils.ofNullable(t)
                .map(function)
                .collect(Collectors.toSet());
    }

    private static <T, R> Function<Set<T>, Set<R>> extractSetWithNullHandling(Function<T, Set<R>> function) {
        return t -> StreamUtils.ofNullable(t)
                .map(function)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Creates a null-safe consumer that applies a function to another object, then a consumer to the result.
     * This is useful, e.g., when needing to consume elements of a (possible composed) getter.
     *
     * @param other    object to start chain from
     * @param func     function to apply to {@code other}
     * @param consumer bi-consumer that takes the result of {@code func} and the applied element
     * @param <T>      type of the consumed element
     * @param <O>      type of the "starting" object
     * @param <S>      type of intermediate element used by {@code consumer}
     * @return single-element consumer for an object of type {@code T}
     */
    public static <T, O, S> Consumer<T> chainFromOther(@Nullable O other, Function<O, S> func, BiConsumer<S, T> consumer) {
        return t -> Optional.ofNullable(other)
                .map(func)
                .ifPresent(s -> consumer.accept(s, t));
    }

    /**
     * Creates a null-safe consumer that applies a function to a set of another objects, then a consumer to the result.
     * This is useful, e.g., when needing to consume elements of a (possible composed) getter.
     *
     * @param others   objects to start chain from
     * @param func     function to apply to {@code other}
     * @param consumer bi-consumer that takes the result of {@code func} and the applied element
     * @param <T>      type of the consumed element
     * @param <O>      type of the "starting" object
     * @param <S>      type of intermediate element used by {@code consumer}
     * @return single-element consumer for an object of type {@code T}
     */
    public static <T, O, S> Consumer<T> chainAllFromOther(@Nullable Collection<O> others, Function<O, S> func, BiConsumer<S, T> consumer) {
        return t -> StreamUtils.ofNullable(others)
                .map(func)
                .filter(Objects::nonNull)
                .forEach(s -> consumer.accept(s, t));
    }

    /**
     * Creates a null-safe consumer that applies a function to the element, then a consumer to input object.
     * This is useful, e.g., when needing to consume elements of a (possible composed) getter.
     *
     * @param other    object to consume in the end
     * @param func     function to apply to the element
     * @param consumer bi-consumer that takes the result of {@code func} and {@code other}
     * @param <T>      type of the consumed element
     * @param <O>      type of the other object
     * @param <S>      type of intermediate element used by {@code consumer}
     * @return single-element consumer for an object of type {@code T}
     */
    public static <T, O, S> Consumer<T> chainFromSelf(@Nullable O other, Function<T, S> func, BiConsumer<S, O> consumer) {
        return t -> Optional.ofNullable(other)
                .ifPresent(c -> consumer.accept(func.apply(t), c));
    }

    /**
     * Creates a null-safe consumer that applies a function to the element, then a consumer to input object.
     * This is useful, e.g., when needing to consume elements of a (possible composed) getter.
     *
     * @param others   objects to consume in the end
     * @param func     function to apply to the element
     * @param consumer bi-consumer that takes the result of {@code func} and an {@code other}
     * @param <T>      type of the consumed element
     * @param <O>      type of the other object
     * @param <S>      type of intermediate element used by {@code consumer}
     * @return single-element consumer for an object of type {@code T}
     */
    public static <T, O, S> Consumer<T> chainAllFromSelf(@Nullable Collection<O> others, Function<T, S> func, BiConsumer<S, O> consumer) {
        return t -> StreamUtils.ofNullable(others)
                .filter(Objects::nonNull)
                .forEach(c -> consumer.accept(func.apply(t), c));
    }

    /**
     * Returns a {@link Function} that applies the {@link Collection#stream()} method on the result of the input {@link Function}.
     * This can be used to shorten stream chains that first does a map and then a flatMap with {@link Collection#stream()}.
     *
     * @param function the input {@link Function}
     * @param <T>      type of the input element
     * @param <R>      type of the elements in the collection returned by the input {@link Function}
     * @return a {@link Function} that applies the {@link Collection#stream()} method on the result of the input {@link Function}
     */
    public static <T, R> Function<T, Stream<R>> stream(Function<T, Collection<R>> function) {
        return function.andThen(Collection::stream);
    }

    /**
     * Returns a {@link Function} that applies the {@link Optional#stream()} method on the result of the input {@link Function}.
     * This can be used to shorten stream chains that first does a map and then a flatMap with {@link Optional#stream()}.
     *
     * @param function the input {@link Function}
     * @param <T>      type of the input element
     * @param <R>      type of the element in the optional returned by the input {@link Function}
     * @return a {@link Function} that applies the {@link Optional#stream()} method on the result of the input {@link Function}
     */
    public static <T, R> Function<T, Stream<R>> streamOptional(Function<T, Optional<R>> function) {
        return function.andThen(Optional::stream);
    }

    /**
     * Helper method to provide ids of entity input parameters in the resulting exception if the given function throws an exception
     *
     * @param entity   a given entity extending {@link BasicIdJpa}
     * @param function a function taking the given {@code entity}
     * @param <U>      class of the given entity
     * @param <R>      class of returned by {@code function}
     * @return the result of applying the function to the given entity, rethrowing any exception with an exception mentioning the entity id
     */
    public static <U extends BasicIdJpa, R> R rethrowWithEntityId(U entity, Function<U, R> function) {
        try {
            return function.apply(entity);
        } catch (Exception e) {
            ErrorCode errorCode = getCoreExceptionErrorCodeOrUnexpectedSystemError(e);
            throw new CoreException(errorCode, String.format("Exception occurred applying function to entity (class=%s, id=%s): %s",
                    entity.getClass().getName(), entity.getId(), ExceptionUtils.getStackTrace(e)));
        }
    }

    /**
     * Gets the {@link ErrorCode} f it is a {@link CoreException} otherwise {@link ErrorCode#UNEXPECTED_ERROR}.
     *
     * @param e a given exception
     * @return the {@link ErrorCode} f it is a {@link CoreException} otherwise {@link ErrorCode#UNEXPECTED_ERROR}.
     */
    public static ErrorCode getCoreExceptionErrorCodeOrUnexpectedSystemError(Exception e) {
        return Optional.of(e)
                .filter(CoreException.class::isInstance)
                .map(CoreException.class::cast)
                .map(CoreException::getErrorCode)
                .orElse(ErrorCode.UNEXPECTED_ERROR);
    }
}
