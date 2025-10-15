package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.entity.base.BasicIdJpa;
import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utility class for handling entities extending BasicIdJpa.
 */
public abstract class EntityUtils {

    private EntityUtils() {
    }

    /**
     * Builds an entity with the given ID or returns null if the ID is null.
     *
     * @param entityId the ID of the entity to be built, can be null.
     * @param <T>      the type of the entity extending BasicIdJpa.
     * @return an instance of the entity with the specified ID, or null if the ID is null.
     */
    @Nullable
    public static <T extends BasicIdJpa> T buildEntityWithIdOrNull(@Nullable Long entityId) {
        return (T) Optional.ofNullable(entityId)
                .map(id -> buildEntityWithId(entityId))
                .orElse(null);
    }

    /**
     * Builds an entity with the given ID.
     *
     * @param entityId the ID of the entity to be built.
     * @param <T>      the type of the entity extending BasicIdJpa.
     * @return an instance of the entity with the specified ID.
     */
    public static <T extends BasicIdJpa> T buildEntityWithId(Long entityId) {
        return (T) BasicIdJpa.builder()
                .id(entityId)
                .build();
    }

    /**
     * Adds a member to a collection and sets the member on the entity using the provided setter.
     *
     * @param entity       the entity to which the member is being added.
     * @param member       the member to be added to the collection.
     * @param memberSetter a Consumer that sets the member on the entity.
     * @param memberList   the collection to which the member will be added.
     * @param <T>          the type of the entity extending BasicIdJpa.
     * @param <R>          the type of the member extending BasicIdJpa.
     */
    public static <T extends BasicIdJpa, R extends BasicIdJpa> void addMemberWithLink(T entity, R member, Consumer<T> memberSetter, Collection<R> memberList) {
        memberSetter.accept(entity);
        memberList.add(member);
    }
}
