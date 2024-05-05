package org.jxch.capital.py4j.binder;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.jxch.capital.py4j.config.Py4JAutoConfig;
import org.jxch.capital.py4j.config.PyBinderScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@PyBinderScan(basePackage = "org.jxch.capital.py4j.binder")
@SpringBootTest(classes = Py4JAutoConfig.class)
public class TestBinderTest {
    @Autowired
    private TestBinder testBinder;

    @Test
    public void test() {
        String res = testBinder.run(null);
        log.info(res);
    }

}
