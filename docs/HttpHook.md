# HttpHook

## 基本信息

**选项解释**：

`Hook Request`: 是否需要对请求Hook。

`Hook Response`: 是否需要对响应Hook。

`Check Expression`: 判断请求是否需要Hook的JavaScript bool表达式。[Expression](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Expression)

**Hook函数**：

`hookRequestToBurp`：HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。
`hookRequestToServer`：HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
`hookResponseToBurp`：HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
`hookResponseToClient`：HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。


**测试**：在`Repeater`右键找到`Http Hook Test`，点击即可测试。

## 实现方式

暂时支持GRPC、JAVA。打算增加更多方式，欢迎加群共建~

### GRPC

你需要自行编写并启动一个 GRPC 服务端。实现四个Hook函数。

GRPC proto 见 [HttpHook.proto](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/proto/HttpHook.proto)

这里有几个不同语言的示例：[java](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/core/httphook/HttpHookGrpcServer.java), [python](https://github.com/outlaws-bai/PyGRpcServer)。

### JAVA

> JAVA代码中需要使用项目中的DataObject和Util。可参考 [DataObject](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#DataObject) [Util](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Util)

你需要选择一个包含Hook函数的JAVA文件，程序会动态编译并调用其中的Hook函数。

项目已经提供了几个常用的加解密文件，你可以选择/创建一个并修改直到满足你的需求。

### Jython *

待实现。

### JS *

待实现。

### ...

待增加。

## 场景及解决方案

**场景1**：在Burp中看到且可编辑解密后的报文。

**场景2**：使用桌面的扫描器扫描明文请求。sqlmap、xray...

### 解决方案 - 1

应对场景1，但必须满足条件：涉及非对称加密的情况下必须已知私钥。

**流程图**：

![流程图](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621105543574.png)

### 解决方案 - 2 *

应对场景2，使得可以用sqlmap，xray等桌面扫描器扫描明文请求。待实现。

### 解决方案 - 3 *

对场景1的补充，提出涉及非对称加密情况下的解决方案。待实现。
