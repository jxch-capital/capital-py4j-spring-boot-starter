package org.jxch.capital.py4j.binder;

import org.jxch.capital.py4j.bind.PyBind;
import org.jxch.capital.py4j.bind.PyBindRunner;

@PyBind(pyResource = "test")
public interface TestBinder extends PyBindRunner<Void, String> {
}
