package org.jxch.capital.py4j.bind.processor;

import java.util.List;

public interface PyBindRunnerParamProcessor {

    <T> List<String> encode(T param);

}
