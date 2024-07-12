## 项目介绍

Burp插件，主要用于应对HTTP请求&响应全加密加签场景下对明文请求查看、编辑、扫描的需求，另外还有一些可以提高Burp使用效率的功能。

**应对场景1**：在Burp中看到且可编辑解密后的报文。

**应对场景2**：使用桌面的扫描器扫描明文请求。sqlmap、xray...

这些场景下的解决方案名为 [Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md) 。

除此之外还有一些小功能：

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
> 欢迎共建 ~

如果你发现BUG、有建议、新小功能的想法，欢迎在GitHub上提Issue或扫码添加下方微信群一起交流讨论。

(二维码失效请添加wx号outlaws_bai，并备注 `Galaxy交流` 。)

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240711201827643.png" height="300px" width="240px" />

## 常用地址

[BurpDownload](https://portswigger.net/burp/releases#professional)

[BurpJavaDoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[BurpExtExamples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[JDK17Download](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
