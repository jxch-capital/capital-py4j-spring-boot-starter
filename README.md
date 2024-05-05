# capital-py4j-spring-boot-starter
py4j本地执行引擎与springboot的无缝集成

## 为什么开发这个项目
1. 很多python执行器的库无法提供原生的python体验
2. Graalvm等jvm虽然提供了python解释器的环境但是无法实现无缝的自动集成
3. 灵活性，用户可以自由指定python版本，默认使用嵌入式python环境，大小仅21M，初始化环境速度很快

## 代码示例
1. 在/py/test.py中准备示例python代码
```python
print("test py")
```
2. 定义Binder接口（默认路径：/py/；指定test.py脚本；输入Void代表无参；输出String）
```java
@PyBind(pyResource = "test")
public interface TestBinder extends PyBindRunner<Void, String> {
}
```
3. 指定`PyBinderScan`扫描的Binder包路径
```java
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
```

---
## 自定义
* `Py4JAutoConfig` 类中可以通过配置相关环境变量来自定义要下载的python解释器链接和本地安装目录（默认使用嵌入式python版本，大小只有21M，速度很快）
* `PyBindRunner<Void, String>`中第一个泛型代表入参类型，此类型会被`PyBindRunnerParamProcessor`组件自动编码为`argparse`形式的入参；第二个类型为输出类型
  * 可以通过`PyParam`注解来控制对python脚本的入参格式
  * 可以通过`Converter<String, T>`的Spring组件来自定义输出的类型（由于进程间通信只能通过字符串，所以需要自定义转换类型）
* 可以直接`PyExecutor`组件阻塞执行python命令，也可以执行pip命令安装包

