package org.jxch.capital.py4j.bind;


import org.jxch.capital.py4j.converter.EmptyConverter;
import org.jxch.capital.py4j.converter.PyOutConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PyBind {

    String pyResource();

    String suffix() default ".py";

    Class<? extends PyOutConverter> converter() default EmptyConverter.class;

}
