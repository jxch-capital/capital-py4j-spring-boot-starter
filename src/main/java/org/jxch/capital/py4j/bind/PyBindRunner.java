package org.jxch.capital.py4j.bind;

@FunctionalInterface
public interface PyBindRunner<T, R> {

    R run(T command);

}
