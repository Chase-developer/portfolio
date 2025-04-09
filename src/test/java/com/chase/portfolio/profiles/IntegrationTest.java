package com.chase.portfolio.profiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.test.context.junit.jupiter.DisabledIf;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DisabledIf(expression = "#{systemProperties['spring.profiles.active'] != 'test' && systemProperties['spring.profiles.active'] != 'int-test'}", reason = "Test disabled for non-unit-test and non-integration-test profiles")
public @interface IntegrationTest {
}
