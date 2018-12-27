package com.tangseng.Annotation;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default  "";
}
