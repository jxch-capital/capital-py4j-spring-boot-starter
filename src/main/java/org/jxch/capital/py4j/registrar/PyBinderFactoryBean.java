package org.jxch.capital.py4j.registrar;

import org.jxch.capital.py4j.bind.PyBind;
import org.jxch.capital.py4j.bind.processor.PyBindRunnerParamProcessor;
import org.jxch.capital.py4j.config.Py4JAutoConfig;
import org.jxch.capital.py4j.executor.PyExecutor;
import org.jxch.capital.py4j.handler.PyBinderInvocationHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Proxy;

public class PyBinderFactoryBean implements FactoryBean<Object> {
    private final Class<?> pyBindRunnerClazz;
    @Autowired
    private PyExecutor pyExecutor;
    @Autowired
    private PyBindRunnerParamProcessor processor;
    @Autowired
    private Py4JAutoConfig py4JAutoConfig;
    @Autowired
    private ApplicationContext context;

    public PyBinderFactoryBean(Class<?> pyBindRunnerClazz) {
        this.pyBindRunnerClazz = pyBindRunnerClazz;
    }

    @Override
    public Object getObject() {
        return Proxy.newProxyInstance(pyBindRunnerClazz.getClassLoader(),
                new Class<?>[]{pyBindRunnerClazz},
                new PyBinderInvocationHandler(pyExecutor, processor, py4JAutoConfig,
                        context.getBean(pyBindRunnerClazz.getAnnotation(PyBind.class).converter())));
    }

    @Override
    public Class<?> getObjectType() {
        return pyBindRunnerClazz;
    }

}
