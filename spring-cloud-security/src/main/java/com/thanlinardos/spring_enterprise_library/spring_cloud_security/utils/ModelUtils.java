package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.mapped.base.BasicIdModel;
import jakarta.annotation.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class for handling models extending BasicIdModel.
 */
public abstract class ModelUtils {

    private ModelUtils() {
    }

    /**
     * Safely retrieves the ID from a BasicIdModel, returning null if the model is null.
     *
     * @param model the BasicIdModel instance, which may be null
     * @return the ID of the model, or null if the model is null
     */
    public static Long getIdFromModel(@Nullable BasicIdModel model) {
        return Optional.ofNullable(model)
                .map(BasicIdModel::getId)
                .orElse(null);
    }

    /**
     * Safely retrieves the ID from a nested BasicIdModel within another model, returning null if the outer model is null.
     *
     * @param model               the outer BasicIdModel instance, which may be null
     * @param nestedModelSupplier a function that extracts the nested BasicIdModel from the outer model
     * @param <T>                 the type of the outer model
     * @param <R>                 the type of the nested model
     * @return the ID of the nested model, or null if the outer model is null
     */
    public static <T extends BasicIdModel, R extends BasicIdModel> Long getIdFromNestedModel(@Nullable T model, Function<T, R> nestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(null);
    }

    /**
     * Safely retrieves the ID from a nested BasicIdModel within another model, returning the ID from an alternative nested model if the outer model is null.
     *
     * @param model                 the outer BasicIdModel instance, which may be null
     * @param nestedModelSupplier   a function that extracts the primary nested BasicIdModel from the outer model
     * @param orNestedModelSupplier a function that extracts the alternative nested BasicIdModel from the outer model
     * @param <T>                   the type of the outer model
     * @param <R>                   the type of the nested models
     * @return the ID of the primary nested model, or the ID of the alternative nested model if the outer model is null
     */
    public static <T extends BasicIdModel, R extends BasicIdModel> Long getIdFromNestedModelOr(@Nullable T model, Function<T, R> nestedModelSupplier, Function<T, R> orNestedModelSupplier) {
        return Optional.ofNullable(model)
                .map(t -> getIdFromModel(nestedModelSupplier.apply(t)))
                .orElse(getIdFromNestedModel(model, orNestedModelSupplier));
    }
}
