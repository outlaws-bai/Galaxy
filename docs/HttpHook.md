# Http Hook

需要一些编程基础

## 设计思路

> 必读

[设计思路及运行原理](https://xz.aliyun.com/t/15051)。

## 测试靶场

[GalaxyDemo](https://github.com/outlaws-bai/GalaxyDemo) ，项目中的示例均与该靶场的对应

## 界面简介

`Hooker`:  hook 的方式，可选js、graalpy、jython、java、grpc、http（因不同 jar 包及客户端条件有差异，详情见 [Releases](https://github.com/outlaws-bai/Galaxy/releases) 中的注意事项）

`Hook Response`: 开关，是否需要对响应Hook

`Auto Forward Request`: 开关，是否自动将解密后的请求转发到被动代理扫描器。注意联动被动代理扫描器时必须配置被动扫描器的上游代理为Burp

`Expression`: mvel bool [表达式](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Expression)，用请求判断经过 Proxy 模块的流量是否需要Hook

## hook 方式解释

支持grpc、http、java、graalpy、jython、js等方式

> graalpy -> python3.11
> 
> jython -> python2.7

这四种可分为两类，服务类（grpc、http），跨语言类(java、graalpy、jython、js)

**服务类** ：你可以用任何语言实现 grpc/http 服务端，并在其中实现四个Hook接口，你需要在这些接口通过三方库 修改/还原 请求/响应 对象

[grpc-java](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/core/httphook/HttpHookGrpcServer.java)：grpc 的 java 版本
[grpc-python](https://github.com/outlaws-bai/GalaxyServerHooker)：grpc 的 python 版本
[http-python](https://github.com/outlaws-bai/GalaxyServerHooker)：http 的 python 版本

**跨语言类** ：你可以用对应的跨语言方案实现 hook 脚本，并在其中实现四个Hook函数，你需要在这些函数中 修改/还原 请求/响应对象

[examples](https://github.com/outlaws-bai/Galaxy/tree/main/src/main/resources/examples)

### 优缺点对比

**服务类**：优点是跨语言能力、运行兼容性强；缺点是学习成本稍高、依赖IO -> 可能存在性能问题、不同语言算法间可能存在兼容性问题，在动态密钥的情况下很难实现需求。

**跨语言类**：优点是可以与JVM交互 -> 可调用项目内置的加解密工具类 + 对Java来说没有算法兼容性的问题、可以将客户端代码 copy 进去运行、在动态密钥的情况也能实现需求；缺点是需要熟悉项目自带的对象和工具类，并且存在跨语言兼容性的问题。

## 使用思路
```
可逆向出算法及加密逻辑 -> 实现加解密算法
算法较为复杂 -> 游览器 -> 抠出游览器中加解密代码 or 通过jsrpc/cdp调用游览器中的加解密代码
           -> 客户端 -> 通过 frida 调用客户端中的加解密代码
```

## 测试

在启动Http Hook服务后，在任意的HTTP请求/响应编辑器中右键可找到对应的按钮，点击即可测试

## 日志

运行中的日志会发送到两个地方：

1. `Burp -> Extensions -> Galaxy -> Output/Errors` （仅显示部分）
2. [WorkDir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#work-dir) / run.log

## 工具联动

- 联动jsrpc调用 js 代码：[linkage-jsrpc](xz.aliyun.com/t/15252)
- 联动frida调用客户端代码：**服务类** hook 方式自行调用即可；**跨语言类** 需要通过 [Grida](https://github.com/outlaws-bai/Grida) 暴露 frida rpc 接口为 http 接口，再在插件编辑器中用 HttpClient 调用
- 联动sqlmap扫描明文请求：在已解密请求右键找到 `Send Decrypted Request To Sqlmap` 点击后粘贴命令到终端中执行
- 联动xray扫描明文请求：配置xray的上游代理为 burp，开启 `Auto Forward Request` 或在已解密请求右键找到 `Send Decrypted Request To Scanner` 点击

> 当在联动sqlmap、xray或与它们相似的工具时，由于流量会再次经过Burp，Burp的Proxy中势必会多出扫描流量，可以添加下方代码片段到bambda不显示这些流量
>
> !requestResponse.annotations().notes().contains("HookedByGalaxy")

