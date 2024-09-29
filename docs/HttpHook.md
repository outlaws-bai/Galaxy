# Http Hook

支持用js/python/java实现hook脚本或任意语言实现grpc/http hook服务来自动解密报文，让你像测试明文一样简单

> 用于熟悉该插件的靶场 [GalaxyDemo](https://github.com/outlaws-bai/GalaxyDemo) ，下方实现方式中的示例均为该靶场的对应 hook 脚本/服务

## 简介

将请求/响应对象交给你，你可以通过少量代码随意修改请求/响应对象，这样无论有多复杂都可以达到目的

> 需要一些编程基础，项目已提供多项示例，可以作为参考

## 设计思路

[设计思路](https://xz.aliyun.com/t/15051)。

## 流程图

![流程图](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621105543574.png)

`hookRequestToBurp`：HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文

`hookRequestToServer`：HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server

`hookResponseToBurp`：HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文

`hookResponseToClient`：HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client

## 界面

`Hooker`: 可选js、graalpy、jython、java、grpc、http（因不同 jar 包及客户端条件有差异，详情见 [Releases](https://github.com/outlaws-bai/Galaxy/releases) 中的注意事项）

`Hook Response`: 开关，是否需要对响应Hook

`Auto Forward Request`: 开关，是否自动将解密后的请求转发到被动代理扫描器。注意联动被动代理扫描器时必须配置被动扫描器的上游代理为Burp

`Expression`: mvel bool [表达式](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Expression)，用请求判断经过 Proxy 模块的流量是否需要Hook

## 实现方式

支持grpc、http、java、graalpy、jython、js等方式实现四个Hook

> graalpy -> python3.11
> 
> jython -> python2.7

这四种可分为两类，服务类（grpc、http），跨语言类(java、graalpy、jython、js)

**服务类** ：你可以用任何语言实现 grpc/http 服务端，并在其中实现四个Hook接口，你需要在这些接口通过三方库实现修改请求/响应对象，它们应有的功能

[grpc-java](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/core/httphook/HttpHookGrpcServer.java)
[grpc-python](https://github.com/outlaws-bai/GalaxyServerHooker)
[http-python](https://github.com/outlaws-bai/GalaxyServerHooker)

**跨语言类** ：你可以用对应的跨语言方案实现hook脚本，并在其中实现四个Hook函数，你需要在这些函数中修改请求/响应对象，以实现它们应有的功能

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

在启动Http Hook服务后，在任意的HTTP请求/响应编辑器中右键可找到对应的按钮。

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

