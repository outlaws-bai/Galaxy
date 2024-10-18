<h3 align="center">Galaxy</h3>
<p align="center">
让你测试加密报文时像明文一样简单高效
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
<a href="https://github.com/outlaws-bai/Galaxy/wiki/FAQ">FAQ</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/issues">Issue</a>
</p>

## 🔥 主要功能

- **自动化解密加密请求和响应**：写好自定义 hook 后，插件会自动化解密后续代理的流量。
- **全面支持 Burp 模块**：适用于 Burp 的所有模块，如 Intruder、Proxy、Repeater 和 Scanner。
- **与安全工具联动**：支持与 sqlmap、xray 的联动，让你更高效地发现潜在的安全漏洞。
- **其他小功能**：[Bypass Host Check](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Host-Check)、[Bypass Auth Of Path](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Auth-Of-Path)、[Parse Swagger Api Doc](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Parse-Swagger-Api-Doc)...

## 🔒 适用场景

- 渗透测试中发现网站的 HTTP 报文做了加密。
- 加密逻辑较为复杂，如加密算法组合、自定义算法和动态密钥等。
- 想要使用扫描器，支持其对明文请求扫描，扫描请求发出后获得明文响应。
- ...

## 🎥 效果演示

**自动解密**

> 启动后代理的请求/响应自动解密，并且将解密后的请求转发到 Repeater 后发送，得到的是明文响应。

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/hook.gif)

**联动sqlmap**

> 右键获取 sqlmap 扫描的命令，执行 后 sqlmap 就可以扫描明文请求，并得到解密后的响应。

![linkage-sqlmap](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-sqlmap.gif)

**联动xray**

> 右键将解密后的明文请求发送给 xray，它就可以扫描明文请求，并得到解密后的响应。

![linkage-xray](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-xray.gif)

## 🚀 安装指引

插件下载：[Download](https://github.com/outlaws-bai/Galaxy/releases)

插件安装：`Extensions -> Add -> Select File -> Next`

自行构建：修改 `build.gradle` 中 的 `optionalHooker` 再使用 `shadowJar` 打包（gradlew shadowJar）

**注意事项**:

- [首次使用必看](https://github.com/outlaws-bai/Galaxy/wiki/%E5%8A%9F%E8%83%BD%E8%AF%A6%E8%A7%A3)
- [Release](https://github.com/outlaws-bai/Galaxy/releases) 中对应版本的注意事项
- Burp版本不低于 `v2023.10.3.7`

## 🐛 遇到问题

请先阅读 [FAQ](https://github.com/outlaws-bai/Galaxy/blob/main/docs/FAQ.md) 和 [历史Issue](https://github.com/outlaws-bai/Galaxy/issues?q=is%3Aissue)，无法解决可以提交 [Issue](https://github.com/outlaws-bai/Galaxy/issues) 或加群。

## 📢 联系我们

> 如果项目对你有帮助，请 star。

扫码添加微信并备注 `Galaxy` 。

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240731000104866.png" width="300" height="300"/>

## 🔯 Stars

[![Stargazers over time](https://starchart.cc/outlaws-bai/Galaxy.svg?variant=adaptive)](https://starchart.cc/outlaws-bai/Galaxy)

## 🔗 常用地址

[设计思路及运行原理](https://xz.aliyun.com/t/15051)

[项目对应靶场](https://github.com/outlaws-bai/GalaxyDemo)

[BurpDownload](https://portswigger.net/burp/releases#professional)

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
