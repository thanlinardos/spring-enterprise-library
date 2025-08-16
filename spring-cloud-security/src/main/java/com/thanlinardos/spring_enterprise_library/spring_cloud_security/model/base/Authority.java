package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.AccessType;
import jakarta.annotation.Nullable;

public interface Authority {

    String getName();

    AccessType getAccess();

    String getUri();

    @Nullable
    String getExpression();
}
