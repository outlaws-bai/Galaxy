# Http Hook

实现在HTTP报文二次加密场景下自动解密的功能。

## 简介

目前外部工具的做法是在用户选择繁琐的页面配置后，用已加密的数据调用对应的解密函数完成解密，这样只能满足既定情况，如加密&加签同时存在、加密算法组合等不适用。

而本项目的思路是将请求/响应对象交给你，通过少量代码修改请求/响应对象，这样无论算法组合有多复杂都可以自动解密。

> 需要一些编程基础，不过项目已内置多种加密场景的脚本，对于常规算法及逻辑可以做到开箱即用。

## 设计思路

[设计思路](https://xz.aliyun.com/t/15051)。

## 流程图

![流程图](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621105543574.png)

`hookRequestToBurp`：函数/接口，HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

`hookRequestToServer`：函数/接口，HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

`hookResponseToBurp`：函数/接口，HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

`hookResponseToClient`：函数/接口，HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

## 界面

安装后，你会看到这样的页面：

![image-20240730215219927](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730215219927.png)

`Hooker`: [实现方式](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md#%E5%AE%9E%E7%8E%B0%E6%96%B9%E5%BC%8F)，可选js、python、java、grpc。

`Hook Request`: 开关，用于判断是否需要对请求Hook。

`Hook Response`: 开关，用于判断是否需要对响应Hook。

`Expression`: js bool [表达式](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Expression)，用于判断请求是否需要Hook。

## 实现方式

支持grpc、java、python、js这四种方式实现四个Hook。

这四种可分为两类，grpc（grpc），code(java、python、js)。

`grpc` ：你可以用任何语言实现grpc服务端，并在其中实现四个Hook，在这里它们是四个接口，你需要自行通过三方库实现它们应有的功能。

`code` ：你可以用对应的语言实现脚本文件，并在其中实现四个Hook，在这里它们是四个函数，你需要在这些函数中 `找到请求/响应的加解密数据` -> `调用项目中的加解密代码` -> `修改请求/响应对象`，以实现它们应有的功能。

## 测试

将被加密的请求发送到Repeater，并启动Http Hook服务后，右键可找到 `Decrypt Request` ，点击可测试。

## 日志

运行中的日志会发送到两个地方：

1. `Burp -> Extensions -> Galaxy -> Output/Errors`
2. [WorkDir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#work-dir) / run.log

## 示例

**grpc**

[java](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/core/httphook/HttpHookGrpcServer.java)

[python](https://github.com/outlaws-bai/PyGRpcServer)

**code**

https://github.com/outlaws-bai/Galaxy/tree/main/src/main/resources/examples

