<h3 align="center">Galaxy</h3>
<p align="center">
A Burp plugin that wants you to test encrypted traffic as simple and efficient as plaintext
<br>
<br>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="releases" src="https://img.shields.io/github/release/outlaws-bai/Galaxy"/></a>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="downloads" src="https://img.shields.io/github/downloads/outlaws-bai/Galaxy/total?color=orange"/></a>
<img alt="begin" src="https://img.shields.io/badge/begin-202406-green"/>
<img alt="last commit" src="https://img.shields.io/github/last-commit/outlaws-bai/Galaxy"/>
<a href="https://github.com/outlaws-bai/Galaxy/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/outlaws-bai/Galaxy"/></a>
<br>
<br>
<a href="https://github.com/outlaws-bai/Galaxy">中文</a> |
<a href="https://github.com/outlaws-bai/Galaxy/wiki/Home%E2%80%90EN">Wiki</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/releases">Download</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/wiki/FAQ%E2%80%90EN">FAQ</a> | 
<a href="https://github.com/outlaws-bai/Galaxy/issues">Issue</a>
</p>

## 🔥 Key Features

- **Automated decryption of traffic**：After writing the custom hook, the plugin will automatically decrypt the traffic of subsequent agents.
- **Support Burp multi module**：Suitable for multiple modules of Burp, such as Intruder, Proxy, Repeater, and Scanner.
- **Linkage with security tools**：Support linkage with sqlmap and xray, allowing you to discover potential security vulnerabilities more efficiently.
- ...

## 🔒 Application Scenarios

- During penetration testing, it was discovered that the website's HTTP packets were encrypted.
- The encryption logic is relatively complex, such as encryption algorithm combinations, custom algorithms, and dynamic keys.
- I want to use a scanner that supports scanning plaintext requests and obtaining plaintext responses after the scanning request is sent.
- I can reverse engineer the encryption and decryption logic of the website (including calling client code through hooks) and have certain code capabilities.
- ...

## 🎥 Feature Showcase

**Auto decryption**

> After startup, the proxy's request/response is automatically decrypted, and the decrypted request is forwarded to the repeater for sending, resulting in a plaintext response.

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/hook.gif)

**Linkage sqlmap**

> Right click to send the decrypted plaintext request to sqlmap, which can scan the plaintext request and obtain the decrypted response.

![linkage-sqlmap](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-sqlmap.gif)

**Linkage xray**

> Right click to send the decrypted plaintext request to xray, and xray can scan the plaintext request and obtain the decrypted response.

![linkage-xray](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/linkage-xray.gif)

## 🚀 Installation Guide

> If this project is helpful to you, please star it.

Download：[Download](https://github.com/outlaws-bai/Galaxy/releases)

Install：`Extensions -> Add -> Select File -> Next`

Build： `build.gradle -> shadowJar`（gradlew shadowJar）

**Precautions**:

- [Onboarding Guide](https://github.com/outlaws-bai/Galaxy/wiki/Home%E2%80%90EN)
- [Release](https://github.com/outlaws-bai/Galaxy/releases) precautions for the corresponding version.
- Burp version >= `v2023.10.3.7`

## 🐛 Troubleshooting

Please read [FAQ](https://github.com/outlaws-bai/Galaxy/wiki/FAQ) and [Historical Issues](https://github.com/outlaws-bai/Galaxy/issues?q=is%3Aissue) first. If they cannot be resolved, you can submit an [issue](https://github.com/outlaws-bai/Galaxy/issues)

## 🔯 Stars

[![Stargazers over time](https://starchart.cc/outlaws-bai/Galaxy.svg?variant=adaptive)](https://starchart.cc/outlaws-bai/Galaxy)

## 🔗 Links

[Proving ground](https://github.com/outlaws-bai/GalaxyDemo)

[BurpDownload](https://portswigger.net/burp/releases#professional)

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)

## ☕ Reference project

https://github.com/gh0stkey/HaE

https://github.com/c0ny1/sqlmap4burp-plus-plus
