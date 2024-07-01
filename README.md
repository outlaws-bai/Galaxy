# Galaxy

**Burp Suite Extension**

功能比较杂，多是出于便捷考虑，提高手动渗透测试效率。

下载：[Download](https://github.com/outlaws-bai/Galaxy/releases)

导入：Extensions -> Add -> Select File(Type: java)

自行打包：build.gradle -> shadowJar

# 核心功能

## HTTP Traffic Hook

**需求**：很多网站都有HTTP请求&响应加解密/加签，这导致想要修改请求或响应的原始报文变得不便。

**实现**：通过写少量代码，在Burp展示已解密后的请求&响应，并在用户修改后自动加密给到server/client。并且同时支持Intruder、Repeater模块。

> 目前大多同功能插件通过用户选择繁琐的页面配置后，调用对应加解密的函数，对流量进行处理，这样只能满足既定情况。再复杂一点的情况，比如加密&加签同时存在、自定义算法的情况下并不适用，而本功能会将请求&响应流量的对象给到用户，用户可以通过简单的代码自行完成对请求&响应流量的处理。
>
> 门槛相对提高一些，但更灵活、适用的场景更多，因此如果想要使用该功能，需要一些Java或其它编程语言的基础。
>
> 熟悉该功能后，即便密钥是动态的(重新加载页面/每次请求更新密钥)，也可以做到对流量进行解密显示。

如果你对该功能有兴趣，可以点击 [这里](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HTTP%E6%B5%81%E9%87%8F%E8%87%AA%E5%8A%A8%E4%BF%AE%E6%94%B9.md#http-traffic-hook) 查看实现方式和使用方法

## Bypass Url

**需求**：当你发现了一个可能存在SSRF或URL跳转的点，但是服务端对host做了校验，可能是startsWith/endsWith等等。

**实现**：程序会先获取当前请求的URL和期望攻击的URL对象，然后通过内置模板(可自定义)进行渲染，之后将结果作用于测试点

例如, 内置模板中有如下一条，程序会经过如下处理后将其作用于测试点
```java
String template = "${originUrl.getProtocol()}://${originUrl.getHost()}@${evilUrl.getHost()}";
URL originUrl = new URL("https://baidu.com");
URL evilUrl = new URL("https://evil.com");
Map<String, Object> env = new HashMap<>();
env.put("originUrl", originUrl);
env.put("evilUrl", evilUrl);
String res = Render.renderTemplate(template, env); // https://baidu.com@evil.com
```

如果你对该功能有兴趣，可以点击 [这里](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Bypass.md#bypass-url) 查看使用方式

## Bypass Path

**需求**：当你通过接口文档或JS等方式发现了一个新的接口，但因为它存在漏洞，WAF或服务端的鉴权规则禁止对该接口的访问，想要自动化对其进行Bypass，而不是手动修改

**原理**：利用不同的Web容器对URL Path的处理不同来Bypass，原理可查看[浅谈Path解析与鉴权中的陷阱](https://tttang.com/archive/1899/)

例如，当path为 `/api/user/admin/listUsers`，会自动生成不同的绕过path
```java
String path = "/api/user/admin/listUsers";
BypassTools.generateBypassPathPayloads(path).forEach(System.out::println);
/*
 * /api/user/admin/listUsers;
 * /api/user/admin/listUsers/
 * /;/api/;/user/;/admin/;/listUsers
 * /%61%70%69/%75%73%65%72/%61%64%6D%69%6E/%6C%69%73%74%55%73%65%72%73
 * ...
 * */
```

如果你对该功能有兴趣，可以点击 [这里](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Bypass.md#bypass-path) 查看使用方式

## Fuzz Sensitive Path

**需求**: 当你发现某个Path `/api/user/getUserInfo`，想要分别对`/`，`/api/`，`/api/user
/`，进行目录扫描，如果你使用常规的目录扫描器，需要运行三次。或者当你获取到了某个目标几百个请求，想要对每个请求都进行这样的扫描且考虑去重。

**实现**：获取Path中每一层的目录，并且追加上内置的字典(可自定义)，作为最终的测试路径。

**该功能可以配合上述Bypass Path同时使用**

如果你对该功能有兴趣，可以点击 [这里](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Fuzz.md#fuzz-sensitive-path) 查看使用方式

## Fuzz Swagger Docs

**需求**：当你发现了swagger或openapi的接口文档泄露，由于接口众多，需要一个插件解析接口文档，代入参数，发送请求

如果你对该功能有兴趣，可以点击 [这里](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Fuzz.md#fuzz-swagger-docs) 查看使用方式

## 其他

该项目还包括了很多的小功能，如果你有兴趣，可以阅读如下按照模块划分的功能详情

[HTTP流量自动修改](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HTTP%E6%B5%81%E9%87%8F%E8%87%AA%E5%8A%A8%E4%BF%AE%E6%94%B9.md)

[Bypass](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Bypass.md)

[Fuzz](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Fuzz.md)

[Payload](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Payload.md)

[Cloud](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Cloud.md)

[Mixed](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Mixed.md)

# Next

各大云平台对象存储匿名操作检测

# 交流

> 期待你的star，如果该项目对你有帮助的话 ~

如果你发现BUG、有建议或有好的点子，可以提交Issue或扫码添加下方wx交流群沟通

![交流](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/Snipaste_2024-06-28_09-59-03.png)

链接失效请添加wx号outlaws_bai，并备注Galaxy交流。

# 环境

Burp运行及插件编译JDK版本：21

Burp版本：V2023.10.3.7

Python版本：3.11

# 参考文档

[burp javadoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[burp ext examples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[jdk 21 download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
