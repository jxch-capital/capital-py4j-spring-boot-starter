package org.jxch.capital.py4j.bind;


import org.jxch.capital.py4j.converter.EmptyConverter;
import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PyBind {

    String pyResource();

    String suffix() default ".py";

    Class<? extends Converter> converter() default EmptyConverter.class;

}
