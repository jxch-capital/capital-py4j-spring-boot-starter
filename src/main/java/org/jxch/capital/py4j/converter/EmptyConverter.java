package org.jxch.capital.py4j.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EmptyConverter implements Converter<String, String> {

    @Override
    public String convert(String source) {
        return source;
    }

}
