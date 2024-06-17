# Fuzz

主要做Fuzz相关的功能

## Fuzz Sensitive Path

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

## Fuzz Swagger Docs

**场景**

在已获取到一份Swagger接口文档的情况下，接口很多，逐个处理较为费时

**实现**

解析Swagger接口文档，生成每个接口的测试请求、自动化解析参数代入、发送请求

## Extract FuzzDict

**场景**

当发现某个可能存在问题的接口，但无法获取入参，需要通过fuzz的方式测试有哪些入参，但由于不同公司不同业务可能有不同的命名习惯，需要手动总结fuzz的参数列表

**实现**

分析多个流量，获取请求&响应中的header名称、cookie键值、参数名称(包括query、form、json的每一级)、路径、action(path的最后一段)
创建`FuzzDict`对象并输出至`.galaxy/fuzzDicts`文件夹下

## 