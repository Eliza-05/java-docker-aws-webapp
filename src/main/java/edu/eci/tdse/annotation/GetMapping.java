package edu.eci.tdse.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps HTTP GET requests to a specific URI path.
 * Methods annotated with @GetMapping inside a @RestController
 * are registered automatically as GET endpoints.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    String value();
}
