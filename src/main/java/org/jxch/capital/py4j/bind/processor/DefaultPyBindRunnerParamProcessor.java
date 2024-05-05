package org.jxch.capital.py4j.bind.processor;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.jxch.capital.py4j.bind.PyParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DefaultPyBindRunnerParamProcessor implements PyBindRunnerParamProcessor {

    @Override
    @SneakyThrows
    public <T> List<String> encode(T param) {
        if (Objects.isNull(param)) {
            return new ArrayList<>();
        }

        List<String> commands = new ArrayList<>();
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(param);
            if (Objects.nonNull(value)) {
                String fieldName = StrUtil.toUnderlineCase(field.getName());
                if (AnnotationUtil.hasAnnotation(field, PyParam.class)) {
                    PyParam pyParam = AnnotationUtil.getAnnotation(field, PyParam.class);
                    if (pyParam.ignored()) {
                        continue;
                    }

                    if (!StrUtil.isBlank(pyParam.alias())) {
                        fieldName = pyParam.alias();
                    }
                }

                commands.add("--" + fieldName);
                String fieldValue = StrUtil.toString(value);
                if (AnnotationUtil.hasAnnotation(field, DateTimeFormat.class)) {
                    String pattern = AnnotationUtil.getAnnotation(field, DateTimeFormat.class).pattern();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    fieldValue = simpleDateFormat.format(value);
                }
                commands.add(fieldValue);
            }
        }

        return commands;
    }

}
