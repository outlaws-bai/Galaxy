# Galaxy

Burp插件，主要实现在 `HTTP报文二次加密或加签 `场景下，自动解密以使得Burp中展示明文报文的功能。

## 功能介绍

### Http Hook

使用Burp新版  `Montoya API`  开发，从中提取出四个生命周期，你可以使用Python、JS、Java语言或Grpc来完成生命周期的逻辑以实现需求。

> 项目已内置多种加解密规则，对于常规算法可以做到开箱即用。

进一步了解：[Detail](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md)

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/hook.gif)

### 其他功能

1. [Parse Swagger Api Doc](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Parse-Swagger-Api-Doc):  解析swagger文档，生成所有URL的请求，带入参数、路径、描述。可选自动发送。
2. [Bypass Host Check](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Host-Check):  绕过服务端在csrf/ssrf的测试点对host做了验证。
3. [Bypass Auth Of Path](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Auth-Of-Path):  通过修改Path的方式绕过某些认证/鉴权/拦截。

## 安装指引

插件下载：[Download](https://github.com/outlaws-bai/Galaxy/releases)

插件安装：`Extender -> Extensions -> Add - Select File -> Next`

自行构建：`build.gradle -> shadowJar`

**注意事项**:

1. 项目采用Burp `Montoya API` 开发，Burp版本不低于`v2023.10.3.7`。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
2. 项目使用JDK 17进行开发及编译，请确保启动Burp的JDK不低于17。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)

## 优势特点

1. 简单高效：用户不需要启动多余的本地服务，配置成功后可以自动对报文进行加解密。
2. 上手容易：通用算法已有示例，能做到开箱即用。
3. 灵活：可以使用Python、JS、Java、Grpc多种方式实现以满足需求。
4. 支持面广：如加密算法组合、自定义算法、动态密钥等均可以支持。

## Next

1. 支持配合桌面扫描器一起使用，使得扫描器可以扫描明文请求并得到明文响应。
2. 提出在涉及非对称加密（不已知私钥）下的使用方法。

## 交流

> 期待你的star，如果该项目对你有帮助的话 ~
>

如果你发现BUG或有好的建议，，欢迎在GitHub上提Issue或扫码添加下方微信群一起交流讨论。

(二维码失效请添加wx号outlaws_bai，并备注 `Galaxy交流` 。)

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240714204644975.png" height="300px" width="240px" />

## 常用地址

[BurpDownload](https://portswigger.net/burp/releases#professional)

[BurpJavaDoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[BurpExtExamples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[JDK17Download](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
