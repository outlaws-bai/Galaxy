# Galaxy

**Burp Suite Extension**

功能比较杂，多是出于便捷考虑，将需要多次或无法手动在Burp上完成的操作自动化。

工具适用于：熟悉yaml结构，有一些JAVA或其他语言基础的人群

# 功能梳理

## 1. HTTP Traffic Auto Modification

主要做HTTP流量自动修改，下方有一个简单的介绍，详情查看 [HTTP流量自动修改](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HTTP%E6%B5%81%E9%87%8F%E8%87%AA%E5%8A%A8%E4%BF%AE%E6%94%B9.md)

### 1.1. HTTP Traffic Hook

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

#### 1.1.1. 方式一 RPC

用户可以通过其他任何语言编写一个GRpc的服务端，该插件会在上述四个生命周期调用GRpc服务端的同名函数，来达到目的。

如：[Python GRPC Server](https://github.com/outlaws-bai/PyGRpcServer)，这里简单实现了一个对AES加解密的demo， 用户可以修改`server_rpc.py`文件中的代码来修改对应的请求&响应对象。

#### 1.1.2. 方式二 JAVA

用户可以编写一个Java的`.class`文件，分别实现与上述生命周期同名的静态函数，该插件会自动编译指定的`.class`文件，再在四个生命周期调用其对应的函数，来达到目的。

> 这里并非要完整编写加解密的逻辑，该插件已经内置了一些加解密、加签算法，用户只需要在对应的函数调用，将流程串起来即可

如：[Hook.java](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/resources/Hook.java)，这里同样简单实现了一个对AES加解密的demo

### 1.2. HTTP Traffic Decorate

该功能实际是HTTP Traffic Hook的青春版

**场景**

有些网站在header等位置中添加一个随机数，来做放重放，用户想要通过简单的表达式生成随机的uuid，替换该header

**实现**

本插件会在HTTP流量的特定生命周期调用一句话表达式修改HTTP流量，比如可以写如下表达，该插件就可以自动修改每个HTTP请求

```java
request.getHeaders().put("X-Request-Id", java.util.UUID.randomUUID().toString())
```

### 1.3. HTTP Traffic Special Rule Match

类似HaE，当匹配到特定的参数名称、响应头、响应内容对流量进行高亮显示。

## 2. Bypass

主要做Bypass相关的功能，下方有一个简单的介绍，详情查看 [Bypass](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Bypass.md)

### 2.1 Bypass Url

**场景**

发现了可能存在SSRF或URL重定向的测试点，存在host校验，想要进行绕过。

**实现**

不同的校验方式有不同的绕过方式，本功能总结了一些绕过的模板。用当前流量的url和用户想要攻击的url对象作为模板的入参，生成payload并发送请求

```
${originUrl.getProtocol()}://${evilUrl.getHost()}?${originUrl.getHost()}
${originUrl.getProtocol()}://${originUrl.getHost()}@${evilUrl.getHost()}
${originUrl.getProtocol()}://${evilUrl.getHost()}/${originUrl.getHost()}
...
```

### 2.2 Bypass Path

**场景**

通过接口文档或其它方式发现了某个存在漏洞的接口，但WAF或程序的鉴权规则禁止对该接口的访问，想要Bypass

**实现**

> 利用WAF或程序鉴权规则与程序路由匹配规则的逻辑不统一，原理参考自 [浅谈 URL 解析与鉴权中的陷阱](https://tttang.com/archive/1899/)
>

获取到请求的path后，通过调用 [该函数](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/modules/bypass/BypassTools.java#L14) 生成一系列绕过的payload

### 2.3 Bypass IP

增加常见Hop By Hop传输IP的请求头, 值默认为127.0.0.1

## 3. Fuzz

主要做Fuzz相关的功能，下方有一个简单的介绍，详情查看 [Fuzz](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Fuzz.md)

### 3.1. Fuzz Sensitive Path

**场景**

在微服务盛行的当下，很多公司对外提供服务的某个域名，都会通过一定的转发规则(反向代理)，将请求代理到不同的服务，
例如某个接口为`/api/user/getUserInfo`会转发到user服务的`/getUserInfo`，如果只对该域名的根目录进行路径扫描，会错失一些漏洞

**实现**

以`/api/user/getUserInfo`接口为例，假设我们想要扫描`/actuator`，通过本插件处理后会生成如下接口

```
/actuator
/api/actuator
/api/user/actuator
```

### 3.2. Fuzz Swagger Docs

**场景**

在已获取到一份Swagger接口文档的情况下，接口很多，逐个处理较为费时

**实现**

解析Swagger接口文档，生成每个接口的测试请求、自动化解析参数代入、发送请求

### 3.3. Extract FuzzDict

**场景**

当发现某个可能存在问题的接口，但无法获取入参，需要通过fuzz的方式测试有哪些入参，但由于不同公司不同业务可能有不同的命名习惯，需要手动总结fuzz的参数列表

**实现**

分析多个流量，获取请求&响应中的header名称、cookie键值、参数名称(包括query、form、json的每一级)、路径、action(path的最后一段)
创建`FuzzDict`对象并输出至`.galaxy/fuzzDicts`文件夹下

## 4. Payload

存储常用的payload，便于在repeater中利用，详情查看 [Bypass](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Bypass.md)

> 支持payload模板渲染，例如在fastjson的测试中，可以通过模板配置表达式，模板渲染时通过表达式调用Burp的Collaborator生成dns domain，再组装成payload

## 5. Cloud(*)

想做云安全相关的功能，待开发及完善，详情查看 [Cloud](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Cloud.md)

### 5.1. Cloud Singer

云资源一般使用签名作为认证, 该功能可以利用配置中的对应AK、SK对当前请求进行签名

## 6. Mixed

一些混杂的功能配置，详情查看 [Mixed](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Mixed.md)

### 6.1. Json To Query

将json参数转为query参数

### 6.2. Query To Json

将query参数转为json

### 6.3. Message To SqlMap 

创建临时文件，写入当前的请求内容，并调用sqlmap进行扫描

### 6.4. Url To Repeater

在任意Request or Response编辑器选中Url,  会将Url转为请求报文并发送至Repeater

# 环境版本

Burp运行及插件编译JAVA版本：19

Burp版本：V2023.10.3.7

Python版本：3.11

# 参考文档

[burp javadoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[burp ext examples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[express](http://mvel.documentnode.com/)