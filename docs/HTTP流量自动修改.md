# HTTP Traffic Auto Modification

主要做HTTP流量自动修改

## HTTP Traffic Hook

**场景**

很多网站都有HTTP请求&响应加解密/加签，这导致想要修改请求或响应的原始报文变得不便，该功旨在通过写少量代码的方式，在Burp展示已解密后的请求&响应，并在用户修改后自动加密给到server/client。

> 目前大多数插件通过用户选择繁琐的页面配置后，调用对应加解密的函数，对流量进行处理，这样只能满足既定情况。
>
> 再复杂一点的情况，比如加密&加签同时存在、自定义算法的情况下并不适用，而本功能会将请求&响应流量的对象给到用户，用户可以通过简单的代码自行完成对请求&响应流量的处理。
>
> 门槛相对提高一些，但更灵活、使用的场景更多

**实现**

本插件使用Burp的API抽离出来请求从client -> burp -> server，响应从server->burp->client流程中的四个生命周期，用户可以利用不同的方式实现这四个生命周期，完成该功能。

**hookRequestToBurp**：HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

**hookRequestToServer**：HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

**hookResponseToBurp**：HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

**hookResponseToClient**：HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

### 方式一 RPC

用户可以通过其他任何语言编写一个GRpc的服务端，该插件会在上述四个生命周期调用GRpc服务端的同名函数，来达到目的。

如：[Python GRPC Server](https://github.com/outlaws-bai/PyGRpcServer)，这里简单实现了一个对AES加解密的demo， 用户可以修改`server_rpc.py`文件中的代码来修改对应的请求&响应对象。

### 方式二 JAVA

用户可以编写一个Java的`.class`文件，分别实现与上述生命周期同名的静态函数，该插件会自动编译指定的`.class`文件，再在四个生命周期调用其对应的函数，来达到目的。

> 这里并非要完整编写加解密的逻辑，该插件已经内置了一些加解密、加签算法，用户只需要在对应的函数调用，将流程串起来即可

如：[Hook.java](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/resources/Hook.java)，这里同样简单实现了一个对AES加解密的demo

## HTTP Traffic Decorate

该功能实际是HTTP Traffic Hook的青春版

**场景**

有些网站在header等位置中添加一个随机数，来做放重放，用户想要通过简单的表达式生成随机的uuid，替换该header

**实现**

本插件会在HTTP流量的特定生命周期调用一句话表达式修改HTTP流量，比如可以写如下表达，该插件就可以自动修改每个HTTP请求

```java
request.getHeaders().put("X-Request-Id", java.util.UUID.randomUUID().toString())
```

## HTTP Traffic Special Rule Match

类似HaE，当匹配到特定的参数名称、响应头、响应内容对流量进行高亮显示。

## 