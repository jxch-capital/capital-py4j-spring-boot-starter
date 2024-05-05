package org.jxch.capital.py4j.registrar;

import org.jxch.capital.py4j.bind.PyBind;
import org.jxch.capital.py4j.bind.PyBindRunner;
import org.jxch.capital.py4j.config.PyBinderScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class PyBinderRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(PyBinderScan.class.getName());
        String basePackage = Objects.requireNonNull(attributes).get("basePackage").toString();
        PyBinderBeanScan binderBeanScan = new PyBinderBeanScan(registry);
        binderBeanScan.addIncludeFilter((metadataReader, metadataReaderFactory) ->
                metadataReader.getAnnotationMetadata().hasAnnotation(PyBind.class.getName())
                        && Arrays.stream(metadataReader.getClassMetadata().getInterfaceNames())
                        .anyMatch(interfaceName -> Objects.equals(interfaceName, PyBindRunner.class.getName())));
        binderBeanScan.scan(basePackage);
    }

}
