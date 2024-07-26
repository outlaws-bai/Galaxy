# Galaxy

Burp插件，主要实现在 `HTTP报文二次加密 `场景下，自动解密以使得Burp中展示明文报文的功能。

## 功能介绍

### Http Hook

使用Burp新版  `Montoya API`  开发，从中提取出四个阶段，你可以使用Python、JS、Java语言或Grpc来完成四个阶段的处理逻辑以实现需求。

> 项目已内置多种加解密场景，对于常规算法可以做到开箱即用。

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

## 内置场景

> 带*为待实现加入

AesCbc: 通过AES CBC模式使用指定的密钥和IV对用户输入的数据进行加密请求，并对服务器响应的数据进行解密。

AesEcb: 通过AES ECB模式使用指定的密钥对用户输入的数据进行加密请求，并对服务器响应的数据进行解密。

AesGcm: 通过AES GCM模式使用指定的密钥和IV对用户输入的数据进行加密请求，并对服务器响应的数据进行解密。

Sm4Cbc: 使用SM4算法在CBC模式下对数据进行加密请求，并展示解密后的结果。

Rsa: 使用RSA公钥加密用户输入的数据，并将加密后的数据发送到服务器；服务器响应的加密数据使用另外一组RSA私钥解密后展示。

Sm2: 使用SM2算法对用户输入的数据进行加密，并将加密后的数据发送到服务器；服务器响应的加密数据使用另外一组SM2私钥进行解密。

DesCbc*: 使用DES算法在CBC模式下对数据进行加密请求，并展示解密后的结果。

3DesCbc*:使用3DES算法在CBC模式下对数据进行加密请求，并展示解密后的结果。

DynamicKey*: 通过从服务器获取动态生成的密钥和IV，对用户输入的数据进行AES CBC加密发送请求，并对服务器响应的数据进行解密。

AesRsa*: 使用随机生成的AES密钥加密用户数据，通过RSA加密AES密钥后发送请求，并在收到响应后解密AES密钥和数据。

Sm2Sm4*: 使用随机生成的SM4密钥加密用户数据，通过SM2加密SM4密钥后发送请求，并在收到响应后解密SM4密钥和数据。


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
