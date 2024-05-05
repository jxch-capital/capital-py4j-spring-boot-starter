package org.jxch.capital.py4j.executor;

import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface PyExecutor {

    void initEnv();

    File getPythonExecutorFile();

    String run(List<String> command);

    default String run(@NonNull File pythonFile, List<String> command) {
        List<String> commandList = new ArrayList<>(command);
        commandList.add(0, pythonFile.getAbsolutePath());
        return run(commandList);
    }

    default String run(@NonNull File pythonFile) {
        return run(List.of(pythonFile.getAbsolutePath()));
    }

    default void pip(List<String> command) {
        List<String> commandList = new ArrayList<>(command);
        commandList.add(0, "pip");
        commandList.add(0, "-m");
        run(commandList);
    }

}
