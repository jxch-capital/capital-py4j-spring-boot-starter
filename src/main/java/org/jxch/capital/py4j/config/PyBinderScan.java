package org.jxch.capital.py4j.config;

import org.jxch.capital.py4j.registrar.PyBinderRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PyBinderRegistrar.class)
public @interface PyBinderScan {

    String basePackage() default "";

}

