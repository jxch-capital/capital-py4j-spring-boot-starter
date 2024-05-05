package org.jxch.capital.py4j.converter;

import org.springframework.stereotype.Component;

@Component
public class EmptyConverter implements PyOutConverter<String> {

    @Override
    public String convert(String source) {
        return source;
    }

}
