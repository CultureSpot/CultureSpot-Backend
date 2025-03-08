package com.culturespot.culturespotdomain.core.auth.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // 파라미터에서 사용 가능
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
}
