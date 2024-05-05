package org.jxch.capital.py4j.config;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Data
@Configuration
@ComponentScan("org.jxch.capital.py4j")
public class Py4JAutoConfig implements InitializingBean, DisposableBean {
    private final static Log log = LogFactory.getLog(Py4JAutoConfig.class);
    public final static String LOCAL_PY_EXECUTOR = "LOCAL_PY_EXECUTOR";
    @Value("${capital.py4j.py.resource.path:/py/}")
    private String pyResourcePath;
    @Value("${capital.py4j.py.executor.url:https://www.python.org/ftp/python/3.12.3/python-3.12.3-embed-amd64.zip}")
    private String pyExecutorUrl;
    @Value("${capital.py4j.py.executor.path:/tmp/py_executor}")
    private String pyExecutorPath;
    private String pyExecutorTmpPath;

    @Override
    @SneakyThrows
    public void afterPropertiesSet() {
        File path = new File(pyExecutorPath);
        pyExecutorTmpPath = path.toPath().resolve("tmp").toString();
        File tmpPath = new File(pyExecutorTmpPath);

        if (path.mkdirs()) {
            log.info("创建python执行器文件夹：" + pyExecutorPath);
        }
        if (tmpPath.mkdirs()) {
            log.info("创建python执行器临时文件夹：" + pyExecutorTmpPath);
        }
    }

    @Override
    public void destroy() {
        File tmpPath = new File(pyExecutorTmpPath);
        tmpPath.deleteOnExit();
        log.info("程序退出后将删除python执行器临时文件夹：" + pyExecutorTmpPath);
    }

}
