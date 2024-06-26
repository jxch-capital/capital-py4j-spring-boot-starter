package org.jxch.capital.py4j.handler;

import org.jxch.capital.py4j.bind.PyBind;
import org.jxch.capital.py4j.bind.PyBindRunner;
import org.jxch.capital.py4j.bind.processor.PyBindRunnerParamProcessor;
import org.jxch.capital.py4j.config.Py4JAutoConfig;
import org.jxch.capital.py4j.converter.PyOutConverter;
import org.jxch.capital.py4j.executor.PyExecutor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class PyBinderInvocationHandler implements InvocationHandler {
    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final PyBindRunnerParamProcessor processor;
    private final PyExecutor pyExecutor;
    private final Py4JAutoConfig py4JAutoConfig;
    private final PyOutConverter<String> converter;

    public PyBinderInvocationHandler(PyExecutor pyExecutor, PyBindRunnerParamProcessor processor, Py4JAutoConfig py4JAutoConfig,
                                     PyOutConverter<String> converter) {
        this.pyExecutor = pyExecutor;
        this.processor = processor;
        this.py4JAutoConfig = py4JAutoConfig;
        this.converter = converter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);

            // 对于非public的类，其模式应该是MethodHandles.Lookup.PRIVATE
            Class<?> declaringClass = method.getDeclaringClass();
            return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                    .unreflectSpecial(method, declaringClass)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        }

        Class<?> clazz = Arrays.stream(proxy.getClass().getInterfaces())
                .filter(PyBindRunner.class::isAssignableFrom).findFirst().orElseThrow();
        PyBind pyBind = clazz.getAnnotation(PyBind.class);

        Resource resource = resolver.getResource(Path.of(py4JAutoConfig.getPyResourcePath())
                .resolve(pyBind.pyResource() + pyBind.suffix()).toString());

        File pyFile = resource.getFile();
        File appScriptDir = new File(py4JAutoConfig.getPyExecutorTmpPath());
        File copyFile = appScriptDir.toPath().resolve(pyFile.getName()).toFile();
        Files.copy(pyFile.toPath(), copyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return converter.convert(pyExecutor.run(copyFile, processor.encode(args[0])));
    }

}
