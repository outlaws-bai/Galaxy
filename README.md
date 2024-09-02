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

## :fire: 功能介绍

### Http Hook

**面临的问题**

- 越来越多的网站对HTTP请求&响应做了加密/加签，这导致想要修改明文请求/响应非常不方便
- 已有项目在面对加密&加签同时存在、加密算法组合等情况时不够用，自行编写hook脚本调通算法难度大、效率低

**本项目的解决方案**

目前其它插件的做法是在用户选择好页面配置后，用被加密的数据调用对应的解密函数完成解密，这样只能满足既定请求。而我们的思路是将请求/响应对象交给你，通过少量代码解密并修改请求/响应对象，这样无论算法组合有多复杂都可以自动解密

> 已包含多种加解密场景的hook脚本，对于常规算法及逻辑可以做到开箱即用

**启用后的效果**

- 启用成功后，后续代理的所有请求和响应自动解密
- 已解密请求转到Repeater后Send，得到的响应也会被解密
- Intruder、Scanner等模块同样支持
- 支持联动sqlmap、被动代理扫描器，使得它们可以扫描明文请求，得到明文响应

进一步了解：[Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md)

### 其他功能

1. [Parse Swagger Api Doc](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Parse-Swagger-Api-Doc):  解析swagger文档，生成所有URL的请求，并带入参数、路径、描述。
2. [Bypass Host Check](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Host-Check):  绕过服务端在url重定向/ssrf的测试点对host做了验证。
3. [Bypass Auth Of Path](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Auth-Of-Path):  通过修改Path的方式绕过某些认证/鉴权/拦截。
4. ...

## :clapper: 效果展示

### 常规情况
![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/hook.gif)

### 联动sqlmap

![linkage-sqlmap](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-sqlmap.gif)

### 联动xray

![linkage-xray](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-xray.gif)

## :rocket: 安装指引

插件下载：[Download](https://github.com/outlaws-bai/Galaxy/releases)

插件安装：`Extensions -> Add -> Select File -> Next`

自行构建：`build.gradle -> shadowJar`

**注意事项**:

- 项目采用Burp `Montoya API` 开发，Burp版本不低于 `v2023.10.3.7` 。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 项目使用JDK 17进行开发及编译，请确保启动Burp的Java版本不低于17。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 如果你下载或打包后的jar包含 `without-jython` 字样，请在Burp的Java environment(`Settings -> Extensions`)配置一个文件夹，并将 `jython-standalone-xxx.jar` 放在该文件夹。[Download](https://www.jython.org/download)

## 🎡 优势特点

- 简单高效：不需要启动多余的本地服务。
- 上手容易：通用算法及常见加密逻辑已内置，基本能做到开箱即用。
- 支持面广：如加密算法组合、自定义算法、动态密钥等均可以支持。
- 强灵活性：可以使用python、js、Java、grpc多种方式实现hook脚本以满足需求。

## :bug: 遇到问题

请先阅读 [FAQ](https://github.com/outlaws-bai/Galaxy/blob/main/docs/FAQ.md) 和 [历史Issue](https://github.com/outlaws-bai/Galaxy/issues?q=is%3Aissue+is%3Aclosed)，无法解决可以提交 [Issue](https://github.com/outlaws-bai/Galaxy/issues) 或加群。

## ⛪ 交流

> 如果项目对你有帮助，请 star

扫码添加微信并备注 `Galaxy` 进群。

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240731000104866.png" width="300" height="300"/>

## :star: Stars

[![Stargazers over time](https://starchart.cc/outlaws-bai/Galaxy.svg?variant=adaptive)](https://starchart.cc/outlaws-bai/Galaxy)

## :book: 常用地址

[BurpDownload](https://portswigger.net/burp/releases#professional)

[BurpJavaDoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[BurpExtExamples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[JDK17Download](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
