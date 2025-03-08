package com.culturespot.culturespotdomain.core.auth.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // 파라미터에서 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
@Documented
public @interface MemberOnly {
}
