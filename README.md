<h3 align="center">Galaxy</h3>
<p align="center">
自动解密被加密的报文，让你像测试明文一样简单
<br>
<br>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="releases" src="https://img.shields.io/github/release/outlaws-bai/Galaxy"/></a>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="downloads" src="https://img.shields.io/github/downloads/outlaws-bai/Galaxy/total?color=orange"/></a>
<img alt="begin" src="https://img.shields.io/badge/begin-202406-green"/>
<img alt="last commit" src="https://img.shields.io/github/last-commit/outlaws-bai/Galaxy"/>
<a href="https://github.com/outlaws-bai/Galaxy/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/outlaws-bai/Galaxy"/></a>
<br>
<br>
<a href="https://github.com/outlaws-bai/Galaxy/releases">Download</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/blob/main/docs/FAQ.md">FAQ</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/issues">Issue</a>
</p>

## 功能介绍

### Http Hook

**场景**

- 越来越多的网站对HTTP请求&响应做了加密/加签，这导致想要修改明文请求/响应非常不方便
- 已有项目在面对加密&加签同时存在、加密算法组合等情况时不够用，自行编写hook脚本难度大、效率低

**效果**

- 启用成功后，后续代理的所有请求和响应自动解密
- 已解密请求转到Repeater后Send，得到的响应也会被解密
- Intruder、Scanner等模块同样支持

> 已包含多种加解密场景的hook脚本，对于常规算法及逻辑可以做到开箱即用。

进一步了解：[Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md)

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/hook.gif)

### 其他功能

1. [Parse Swagger Api Doc](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Parse-Swagger-Api-Doc):  解析swagger文档，生成所有URL的请求，并带入参数、路径、描述。
2. [Bypass Host Check](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Host-Check):  绕过服务端在csrf/ssrf的测试点对host做了验证。
3. [Bypass Auth Of Path](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Auth-Of-Path):  通过修改Path的方式绕过某些认证/鉴权/拦截。
4. ...

## 安装指引

插件下载：[Download](https://github.com/outlaws-bai/Galaxy/releases)

插件安装：`Extensions -> Add -> Select File -> Next`

自行构建：`build.gradle -> shadowJar`

**注意事项**:

- 项目采用Burp `Montoya API` 开发，Burp版本不低于 `v2023.10.3.7` 。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 项目使用JDK 17进行开发及编译，请确保启动Burp的JDK不低于17。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 项目使用了动态编译，请确保启动Burp的是JDK，而不是JRE。[Modify](https://github.com/outlaws-bai/Galaxy/blob/main/docs/ToJDK.md)
- 如果你下载或打包后的jar包含 `without-jython` 字样，请在Burp的Java environment(`Settings -> Extensions`)配置一个文件夹，并将 `jython-standalone-xxx.jar` 放在该文件夹。[Download](https://www.jython.org/download)

## 优势特点

- 简单高效：不需要启动多余的本地服务。
- 上手容易：通用算法及常见加密逻辑已内置，基本能做到开箱即用。
- 支持面广：如加密算法组合、自定义算法、动态密钥等均可以支持。
- 强灵活性：可以使用python、js、Java、grpc多种方式实现hook脚本以满足需求。

## 开发计划

> star越多，更新越快。

- 支持配合桌面扫描器一起使用，使得扫描器可以扫描明文请求并得到明文响应。
- 提出在涉及非对称加密（不已知私钥）下的使用方法。
- 联动jsrpc、frida等

## 交流

如果你发现BUG或有好的建议，欢迎在GitHub上提Issue或扫描群二维码进群交流。

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730211916457.png" width="300" height="300"/>

群二维码失效扫描添加微信并备注 `Galaxy` 。

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240731000104866.png" width="300" height="300"/>

## Stars

[![Stargazers over time](https://starchart.cc/outlaws-bai/Galaxy.svg?variant=adaptive)](https://starchart.cc/outlaws-bai/Galaxy)

## 常用地址

[BurpDownload](https://portswigger.net/burp/releases#professional)

[BurpJavaDoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[BurpExtExamples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[JDK17Download](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
