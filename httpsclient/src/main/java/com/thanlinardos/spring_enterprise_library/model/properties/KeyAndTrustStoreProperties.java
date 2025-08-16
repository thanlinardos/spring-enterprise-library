package com.thanlinardos.spring_enterprise_library.model.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class KeyAndTrustStoreProperties {

    private Resource path;
    private String password;

    public boolean isEnabled() {
        return path != null && password != null;
    }
}
