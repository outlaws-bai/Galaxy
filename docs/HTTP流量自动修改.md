# HTTP Traffic Auto Modification

HTTP流量自动修改

## HTTP Traffic Hook

### 场景

很多网站都有HTTP请求&响应加解密/加签，这导致想要修改请求或响应的原始报文变得不便，该功旨在通过写少量代码的方式，在Burp展示已解密后的请求&响应，并在用户修改后自动加密给到server/client。
**并且同时支持Intruder、Repeater等模块**。

>
目前大多同功能插件通过用户选择繁琐的页面配置后，调用对应加解密的函数，对流量进行处理，这样只能满足既定情况。再复杂一点的情况，比如加密&加签同时存在、自定义算法的情况下并不适用，而本功能会将请求&响应流量的对象给到用户，用户可以通过简单的代码自行完成对请求&响应流量的处理。
>
> 门槛相对提高一些，但更灵活、适用的场景更多，因此如果想要使用该功能，需要一些Java或其它编程语言的基础。

### 实现

本插件在Client/Burp/Server间流量交互的过程中抽出四个作用点。

**hookRequestToBurp**：HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

**hookRequestToServer**：HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

**hookResponseToBurp**：HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

**hookResponseToClient**：HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

![image-20240621105543574](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621105543574.png)

### 前置

该功能需要编写表达式、修改配置文件，相应的简介见

[项目配置简介](https://github.com/outlaws-bai/Galaxy/blob/main/docs/使用须知.md#配置简介) [表达式简介](https://github.com/outlaws-bai/Galaxy/blob/main/docs/使用须知.md#表达式渲染)

### 方式一 RPC

用户可以通过其他任何语言编写一个GRpc的服务端，该插件会在上述四个生命周期调用GRpc服务端的同名函数，来达到目的。

如：[Python GRPC Server](https://github.com/outlaws-bai/PyGRpcServer)，这里简单实现了一个对AES加解密的demo，
用户可以修改`server_rpc.py`文件中的代码来修改对应的请求&响应对象。

还有:  [Java GRPC Server](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/modules/traffic/hook/HttpHookRpcServer.java)
有一个Java实现的GRPC服务端，同样实现了一个对AES加解密的demo。

使用时，需要将部分配置修改如下，并在 `rpcConn` 配置的地址上启动RPC服务

```yaml
httpTrafficAutoModificationConfig: # HTTP Traffic Auto Modification 模块的功能配置
  hookConfig: # HTTP Traffic Hook 功能的配置
    hookService: RPC # enum. 指定以哪种方式启用hook功能，可选：RPC、JAVA
    requestIsNeedHook: false # bool. 请求是否需要hook，开启时hookRequestToBurp、hookRequestToServer才会被执行
    responseIsNeedHook: false # bool. 响应是否需要hook，开启时hookResponseToBurp、hookResponseToClient才会被执行
    requestMatchExpression: 'request.getHost().equals("192.168.1.4")' # expression. 匹配请求的表达式；用来判断哪些请求需要被hook，例如这里判断请求的host必须是192.168.1.4
    rpcConn: 127.0.0.1:8443 # String. 当hookService为RPC时，RPC服务端的连接串
    javaFilePath: C:\Users\outlaws\.galaxy\Hook.java # String. 当hookService为JAVA时，Java的文件路径
```

### 方式二 JAVA

用户可以编写一个Java的`.class`文件，分别实现与上述生命周期同名的静态函数，该插件会自动编译指定的`.class`
文件，再在四个生命周期调用其对应的函数，来达到目的。

> 这里并非要完整编写加解密的逻辑，该插件已经内置了一些加解密、加签算法，用户只需要在对应的函数调用，将流程串起来即可

如：[Hook.java](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/resources/Hook.java)，这里同样简单实现了一个对AES加解密的demo，
还有更多示例可以参考 [examples](https://github.com/outlaws-bai/Galaxy/tree/main/examples/HTTPTrafficHook)，不过我更建议你可以clone代码本地构建调试好再使用。

使用时，需要将部分配置修改如下，并在 `javaFilePath` 文件中编写四个阶段的处理代码

```yaml
httpTrafficAutoModificationConfig: # HTTP Traffic Auto Modification 模块的功能配置
  hookConfig: # HTTP Traffic Hook 功能的配置
    hookService: JAVA # enum. 指定以哪种方式启用hook功能，可选：RPC、JAVA
    requestIsNeedHook: false # bool. 请求是否需要hook，开启时hookRequestToBurp、hookRequestToServer才会被执行
    responseIsNeedHook: false # bool. 响应是否需要hook，开启时hookResponseToBurp、hookResponseToClient才会被执行
    requestMatchExpression: 'request.getHost().equals("192.168.1.4")' # expression. 匹配请求的表达式；用来判断哪些请求需要被hook，例如这里判断请求的host必须是192.168.1.4
    rpcConn: 127.0.0.1:8443 # String. 当hookService为RPC时，RPC服务端的连接串
    javaFilePath: C:\Users\outlaws\.galaxy\Hook.java # String. 当hookService为JAVA时，Java的文件路径
```

### 效果

正常情况下流量被加密

![未启用](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240620232601252.png)

启用该插件后

![启用后](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240620232621547.png)

## HTTP Traffic Decorate

该功能算是HTTP Traffic Hook的简化版，只在hookRequestToBurp、hookResponseToClient阶段起作用，用于通过表达式对请求和响应进行修改。

### 场景

有些网站在header等位置中添加一个随机数，来做防重放，你想要通过简单的表达式生成随机的uuid，替换该header

### 实现

本插件会在HTTP流量的特定生命周期调用一句话表达式修改HTTP流量

### 前置

该功能需要编写表达式、修改配置文件，相应的简介见

[项目配置简介](https://github.com/outlaws-bai/Galaxy/blob/main/docs/使用须知.md#配置简介) [表达式简介](https://github.com/outlaws-bai/Galaxy/blob/main/docs/使用须知.md#表达式渲染)

### 使用

例如，想要在请求和响应头各添加一个随机的UUID

修改配置如下

```yaml
httpTrafficAutoModificationConfig: # HTTP Traffic Auto Modification 模块的功能配置
  decorateConfig: # HTTP Traffic Decorate 功能的配置
    requestModifyExpression: 'request.getHeaders().put("X-Request-Id", java.util.UUID.randomUUID().toString())' # 请求修改的表达式
    responseModifyExpression: 'response.getHeaders().put("X-Response-Id", java.util.UUID.randomUUID().toString())' # 响应修改的表达式
```

### 效果

![image-20240621154307899](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621154307899.png)

## HTTP Traffic Special Rule Match

类似HaE，当匹配到特定的参数名称、响应头、响应内容对流量进行高亮显示。

### 实现

共包括三种匹配配置，当匹配到会对该流量进行分数累计，分数越多在Burp中的颜色显示会更显眼

**requestParamMatches**：对请求参数进行匹配，包括query、form、json(json会逐层解析)

**responseHeaderMatches**：对响应头进行匹配

**responseContentMatches**：对响应内容进行匹配，暂不支持正则等方式，后续考虑优化

### 前置

该功能需要修改配置文件，相应的简介见

[项目配置简介](https://github.com/outlaws-bai/Galaxy/blob/main/docs/使用须知.md#配置简介)

### 使用

例如配置如下的话，当请求中有参数为url、fileUrl；响应头中有x-powered-by；response body中有password，都会累计分数

```yaml
httpTrafficAutoModificationConfig: # HTTP Traffic Auto Modification 模块的功能配置
  ruleMatchConfig: # HTTP Traffic Special Rule Match 功能的配置, value为权重或者说等级，可选择1-5
    requestParamMatches: # 对请求参数进行匹配的配置
      url: 3
      fileUrl: 3
    responseHeaderMatches: # 对响应头进行匹配的配置, 
      x-powered-by: 3
    responseContentMatches: # 对响应内容进行匹配的配置, 
      password: 3
```

### 效果

![image-20240621154447369](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621154447369.png)
