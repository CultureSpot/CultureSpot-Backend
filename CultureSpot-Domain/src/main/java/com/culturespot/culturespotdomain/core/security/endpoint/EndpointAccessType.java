package com.culturespot.culturespotdomain.core.security.endpoint;

import java.util.List;

public enum EndpointAccessType {
    PUBLIC(EndpointPaths.PUBLIC_ENDPOINTS),
    USER(EndpointPaths.USER_AUTHENTICATED_ENDPOINTS),
    ADMIN(EndpointPaths.ADMIN_ENDPOINTS);

    private final List<String> endpoints;

    EndpointAccessType(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }
}
