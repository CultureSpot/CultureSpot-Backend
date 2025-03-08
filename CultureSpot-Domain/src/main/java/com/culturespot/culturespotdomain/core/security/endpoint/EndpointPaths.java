package com.culturespot.culturespotdomain.core.security.endpoint;

import java.util.List;

public class EndpointPaths {
    public final static String FORM_LOGIN = "/api/login";
    public final static String FORM_LOGOUT = "/api/logout";
    public final static String PREFIX_PUBLIC ="/api/public";
    public final static String PREFIX_USER_AUTHENTICATED ="/api/users";
    public final static String PREFIX_ADMIN ="/api/admins";


    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            FORM_LOGIN,
            FORM_LOGOUT,
            PREFIX_PUBLIC + "/**"
    );

    public static final List<String> USER_AUTHENTICATED_ENDPOINTS = List.of(
            PREFIX_USER_AUTHENTICATED + "/**"
    );

    public static final List<String> ADMIN_ENDPOINTS = List.of(
            PREFIX_ADMIN + "/**"
    );
}

