package org.jxch.capital.py4j.executor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxch.capital.py4j.config.Py4JAutoConfig;
import org.jxch.capital.py4j.event.PyExecutorInitializationEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Component
@Accessors(chain = true)
@RequiredArgsConstructor
public class LocalPyExecutor implements PyExecutor , InitializingBean {
    private final static Log log = LogFactory.getLog(Py4JAutoConfig.class);
    private final Py4JAutoConfig config;
    private final ApplicationContext context;
    private String pythonExecutorDir;

    @Override
    public void initEnv() {
        TimeInterval timer = DateUtil.timer();
        downloadEnv();
        importSite();
        installPip();
        upgradePip();
        log.info("初始化环境耗时: " + timer.intervalPretty());
        context.publishEvent(new PyExecutorInitializationEvent(this));
    }

    @Override
    public File getPythonExecutorFile() {
        return new File(pythonExecutorDir).toPath().resolve("python.exe").toFile().getAbsoluteFile();
    }

    @Override
    public String run(List<String> command) {
        List<String> commandArr = new ArrayList<>(command);
        commandArr.add(0, getPythonExecutorFile().getAbsolutePath());
        log.info(String.join(" ", commandArr));
        return runProcess(new ProcessBuilder(commandArr));
    }

    @NonNull
    @SneakyThrows
    private String runProcess(@NonNull ProcessBuilder pb) {
        pb.redirectErrorStream(true);
        Process process = pb.start();
        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner in = new Scanner(new BufferedReader(new InputStreamReader(process.getInputStream())))) {
            while (in.hasNextLine()) {
                String line = in.nextLine();
                log.info(line);
                stringBuilder.append(line).append("\n");
            }
            process.waitFor();
        }
        return stringBuilder.toString();
    }

    private String getPythonVersion() {
        String[] paths = config.getPyExecutorUrl().split("/");
        String targetFileName = paths[paths.length - 1];
        return StrUtil.removeSuffix(targetFileName, ".zip");
    }

    private void setPythonExecutorDir() {
        pythonExecutorDir = new File(config.getPyExecutorPath()).toPath().resolve(getPythonVersion()).toString();
    }

    @SneakyThrows
    private void downloadEnv() {
        log.info("开始下载python执行器：" + config.getPyExecutorUrl());
        URL website = new URL(config.getPyExecutorUrl());
        String[] paths = config.getPyExecutorUrl().split("/");
        String targetFileName = paths[paths.length - 1];
        Path targetPath = new File(config.getPyExecutorPath()).toPath().resolve(targetFileName);
        Files.createDirectories(targetPath.getParent());
        try (InputStream in = website.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        ZipUtil.unzip(targetPath.toFile(), Path.of(pythonExecutorDir).toFile());
        Files.deleteIfExists(targetPath);
    }

    private void importSite() {
        String importSite = "import site";
        File pth = Arrays.stream(Objects.requireNonNull(getPythonExecutorFile().toPath().getParent().toFile().listFiles()))
                .filter(file -> file.getName().startsWith("python") && file.getName().endsWith("._pth")).findFirst().orElseThrow();
        if (FileUtil.readUtf8Lines(pth).stream().noneMatch(line -> Objects.equals(line.trim(), importSite))) {
            FileAppender fileAppender = new FileAppender(pth, 1, true);
            fileAppender.append("\n" + importSite);
            fileAppender.flush();
        }
    }

    @SneakyThrows
    private void installPip() {
        String python = getPythonExecutorFile().getAbsolutePath();
        File pipFile = getPythonExecutorFile().toPath().getParent().resolve("Scripts").resolve("pip3.exe").toFile();
        if (!pipFile.exists()) {
            Path getPipPy = getPythonExecutorFile().toPath().getParent().resolve("get-pip.py");
            URL pipUrl = new URL("https://bootstrap.pypa.io/get-pip.py");
            try (InputStream in = pipUrl.openStream()) {
                Files.copy(in, getPipPy, StandardCopyOption.REPLACE_EXISTING);
            }
            runProcess(new ProcessBuilder(python, getPipPy.toFile().getAbsolutePath()));
        }
    }

    private void upgradePip() {
        pip(List.of("install", "--upgrade", "pip"));
    }

    @Override
    public void afterPropertiesSet() {
        setPythonExecutorDir();
        if (!getPythonExecutorFile().exists()) {
            initEnv();
        }
    }

}
