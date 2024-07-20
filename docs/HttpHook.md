# Http Hook

## 基本信息

**选项解释**：

`Hook Request`: 是否需要对请求Hook。

`Hook Response`: 是否需要对响应Hook。

`Check Expression`: 判断请求是否需要Hook的JavaScript bool表达式。[Expression](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Expression)

**Hook 阶段**：

`hookRequestToBurp`：HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。
`hookRequestToServer`：HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
`hookResponseToBurp`：HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
`hookResponseToClient`：HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

**流程图**：

![流程图](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621105543574.png)

**测试**：在`Repeater`右键找到`Http Hook`，点击其选的按钮即可测试。前提是你已经开启了Http Hook服务

## 实现方式

支持Grpc、Java、Python、Js这四种方式实现四个阶段。

可分为两类：

`Grpc` ：在HTTP报文的对应生命周期调用对应的 `Hook 接口`。你需要用其他语言实现Grpc Server，并自行通过三方库实现对应 `Hook 接口` 应有的功能。

`Code` ：在HTTP报文的特定生命周期调用对应的 `Hook 函数`。你需要用支持的方式编写对应语言的脚本，在脚本中组合、调用项目中的DataObjects和Utils，实现对应 `Hook 函数` 应有的功能。

**对比**

`Grpc`：你需要用其他语言实现Grpc Server。优点是跨语言能力强，运行兼容性强，缺点是学习成本稍高、依赖IO -> 可能存在性能问题、不同语言算法间可能存在兼容性问题，在动态密钥的情况下无法实现需求。

`Code`：优点是与JVM交互调用Java原生的加解密库 -> 对Java来说没有算法兼容性的问题、且项目已包含多种示例 -> 成本低，缺点是需要熟悉项目自带的DataObjects和Utils，并且可能存在运行兼容性的问题。

### Grpc

你需要自行编写并启动一个 GRPC 服务端。实现四个Hook函数。

GRPC proto 见 [HttpHook.proto](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/proto/HttpHook.proto)

这里有几个不同语言的Grpc Server：
1. [java](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/core/httphook/HttpHookGrpcServer.java)
2. [python](https://github.com/outlaws-bai/PyGRpcServer)。

### Java

> 必须使用JDK启动Burp，因为要动态编译.java文件，JRE不满足条件。

你需要选择一个JAVA文件，并在必要的时候修改它直到满足你的需求，程序会动态编译并调用其中的Hook函数。

示例：
1. [AesCbc](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/AesCbc.java)
2. [AesEcb](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/AesEcb.java)
3. [AesGcm](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/AesGcm.java)
4. [Rsa](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/Rsa.java)
5. [Sm2](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/Sm2.java)

### Python

你需要选择一个Python文件，并在必要的时候修改它直到满足你的需求，程序会在不同的HTTP报文的对应生命周期调用对应的函数。

示例：
1. [aes_cbc](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/aes_cbc.py)

### Js

你需要选择一个Js文件，并在必要的时候修改它直到满足你的需求，程序会在不同的HTTP报文的对应生命周期调用对应的函数。

示例：
1. [aes_cbc](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/aes_cbc.js)

## 设计思路

https://xz.aliyun.com/t/15051
