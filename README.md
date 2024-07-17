# Galaxy

Burp插件，主要实现在 `HTTP请求&响应全加密加签 `场景下，高效的对明文报文查看、编辑和扫描的需求。

### 功能介绍

### Http Hook

使用Burp新版  `Montoya API`  开发，你可以使用Python、JS、Java语言或Grpc来完成需求。

> 相比于其它通过配置的方案，该插件是将请求&响应对象交给你实现的代码/服务处理，适配性更广，甚至即便系统是动态密钥，也能满足需求。
>
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

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
