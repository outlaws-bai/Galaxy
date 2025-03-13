<h3 align="center">Galaxy</h3>
<p align="center">
一个想让你测试加密流量像明文一样简单高效的 Burp 插件
<br>
<br>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="releases" src="https://img.shields.io/github/release/outlaws-bai/Galaxy"/></a>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="downloads" src="https://img.shields.io/github/downloads/outlaws-bai/Galaxy/total?color=orange"/></a>
<img alt="begin" src="https://img.shields.io/badge/begin-202406-green"/>
<img alt="last commit" src="https://img.shields.io/github/last-commit/outlaws-bai/Galaxy"/>
<a href="https://github.com/outlaws-bai/Galaxy/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/outlaws-bai/Galaxy"/></a>
<br>
<br>
<a href="https://github.com/outlaws-bai/Galaxy/blob/main/README_EN.md">English</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/wiki">Wiki</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/releases">Download</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/wiki/FAQ">FAQ</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/issues">Issue</a>
</p>

## 🔥 主要功能

- **自动化解密流量**：写好自定义 hook 后，插件会自动化解密后续代理的流量。
- **与安全工具联动**：支持与 sqlmap、xray 的联动，让你更高效地发现潜在的安全漏洞。
- **支持Burp多模块**：适用于 Burp 的多个模块，如 Intruder、Proxy、Repeater 和 Scanner。
- **其他小功能**：[Bypass Host Check](https://github.com/outlaws-bai/Galaxy/wiki/%E5%8A%9F%E8%83%BD%E8%AF%A6%E8%A7%A3#bypass-host-check)、[Bypass Auth Of Path](https://github.com/outlaws-bai/Galaxy/wiki/%E5%8A%9F%E8%83%BD%E8%AF%A6%E8%A7%A3#bypass-auth-of-path)、[Parse Swagger Api Doc](https://github.com/outlaws-bai/Galaxy/wiki/%E5%8A%9F%E8%83%BD%E8%AF%A6%E8%A7%A3#parse-swagger-api-doc)...

## 🔒 适用场景

- 渗透测试中发现网站的 HTTP 报文做了加密。
- 加密逻辑较为复杂，如加密算法组合、自定义算法和动态密钥等。
- 想要使用扫描器，支持其对明文请求扫描，扫描请求发出后获得明文响应。
- 你可以逆向出网站的加解密逻辑（包括通过 hook 方式调用客户端代码），并具备一定的代码能力。
- ...

## 🎥 效果演示

**自动解密**

> 启动后代理的请求/响应自动解密，并且将解密后的请求转发到 Repeater 后发送，得到的是明文响应。

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/hook.gif)

**联动sqlmap**

> 右键将解密后的明文请求发送给 sqlmap，sqlmap就可以扫描明文请求，并得到解密后的响应。

![linkage-sqlmap](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-sqlmap.gif)

**联动xray**

> 右键将解密后的明文请求发送给 xray，xray就可以扫描明文请求，并得到解密后的响应。

![linkage-xray](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-xray.gif)

## 🚀 安装指引

> 如果该项目对你有帮助，请 star

插件下载：[Download](https://github.com/outlaws-bai/Galaxy/releases)

插件安装：`Extensions -> Add -> Select File -> Next`

自行构建： `build.gradle -> shadowJar`（gradlew shadowJar）

**注意事项**:

- [首次使用必读](https://github.com/outlaws-bai/Galaxy/wiki)
- [Release](https://github.com/outlaws-bai/Galaxy/releases) 中对应版本的注意事项
- Burp版本不低于 `v2023.10.3.7`

## 🐛 遇到问题

请先阅读 [FAQ](https://github.com/outlaws-bai/Galaxy/wiki/FAQ) 和 [历史Issue](https://github.com/outlaws-bai/Galaxy/issues?q=is%3Aissue)，无法解决可以提交 [Issue](https://github.com/outlaws-bai/Galaxy/issues)。

## 🛰️ 支持项目

如果你觉得 Galaxy 好用，欢迎支持咖啡，让项目走得更远，功能更强！

<div align=center>
<img src="https://raw.githubusercontent.com/outlaws-bai/picture/refs/heads/main/621741786094_.pic.jpg" style="width: 30%" />
</div>

## 🔯 Stars

[![Stargazers over time](https://starchart.cc/outlaws-bai/Galaxy.svg?variant=adaptive)](https://starchart.cc/outlaws-bai/Galaxy)

## 🔗 常用地址

[项目对应靶场](https://github.com/outlaws-bai/GalaxyDemo)

[BurpDownload](https://portswigger.net/burp/releases#professional)

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)

## ☕ 参考项目

https://github.com/gh0stkey/HaE

https://github.com/c0ny1/sqlmap4burp-plus-plus
