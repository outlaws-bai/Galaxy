# Galaxy

<p align="center">
  <h3 align="center">Galaxy</h3>
  <p align="center">
    Automatically decrypt in the scenario of double encryption in HTTP messages.
    <br />
          <br />
<a href="https://github.com/outlaws-bai/Galaxy/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/outlaws-bai/Galaxy"/></a>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="GitHub releases" src="https://img.shields.io/github/release/outlaws-bai/Galaxy"/></a>
<a href="https://github.com/outlaws-bai/Galaxy/releases"><img alt="Downloads" src="https://img.shields.io/github/downloads/outlaws-bai/Galaxy/total?color=brightgreen"/></a>
<br>
<br>
<a href="https://github.com/outlaws-bai/Galaxy/blob/main/README.md">中文</a> | 
    <a href="https://github.com/outlaws-bai/Galaxy/releases">Download</a> | 
    <a href="https://github.com/outlaws-bai/Galaxy/blob/main/docs/FAQ-EN.md">FAQ</a> | 
    <a href="https://github.com/outlaws-bai/Galaxy/issues">Issue</a>
  </p>

## Function Introduction

### Http Hook

After successful activation, the following effects will occur:

1. All subsequent requests and responses from the agent are automatically decrypted.
2. Once decrypted, the request should be forwarded to the Repeater and the response received will also be decrypted.。
3. Intruder, Scanner, and other modules also support:。

> Multiple encryption and decryption scenario examples are already included, making it ready to use for common algorithms and logic.

Further understanding：[Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook_EN.md)

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/hook.gif)

### Other functions:

1. [Parse Swagger Api Doc](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other_EN.md#Parse-Swagger-Api-Doc):  Analyze the swagger document, generate requests for all URLs, including parameters, paths, and descriptions. Optionally, automatically send:。
2. [Bypass Host Check](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other_EN.md#Bypass-Host-Check):  Bypass the server's validation of the host in csrf/ssrf test points.。
3. [Bypass Auth Of Path](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other_EN.md#Bypass-Auth-Of-Path):  Bypass certain authentication/authorization/interception by modifying the Path.。

## Installation guide:

Plugin Download:：[Download](https://github.com/outlaws-bai/Galaxy/releases)

Plugin installation:：`Extender -> Extensions -> Add - Select File -> Next`

Build your own:：`build.gradle -> shadowJar`

**Note:**:

1. The project is developed using Burp `Montoya API`, with a minimum required version of Burp.`v2023.10.3.7`。 [Update](https://github.com/outlaws-bai/Galaxy/blob/main/docs/README_EN.md#commonly-used-addresses)
2. Develop and compile the project using JDK 17, please ensure that the JDK used to start Burp is not lower than 17.。 [Update](https://github.com/outlaws-bai/Galaxy/blob/main/docs/README_EN.md#commonly-used-addresses)
3. The project uses dynamic compilation, please make sure to start Burp with JDK instead of JRE.[Modify](https://github.com/outlaws-bai/Galaxy/blob/main/docs/ToJDK_EN.md)

## Advantages and characteristics:

1. Simple and efficient: Users do not need to start unnecessary local services. After successful configuration, message encryption and decryption can be done automatically.。
2. Easy to get started: Universal algorithms have examples available, making it easy to use out of the box.。
3. Flexible: can be implemented in multiple ways such as Python, JS, Java, and Grpc to meet the requirements.
4. Support a wide range: such as encryption algorithm combination, custom algorithms, dynamic keys, etc. can all be supported.。

## Development Plan:

> At this stage, only about half of the project development has been completed. Make sure to **star** the code to avoid getting lost.

1. Supports use with desktop scanners, allowing the scanner to scan plaintext requests and receive plaintext responses.。
2. Propose the method of using in the case of asymmetric encryption (unknown private key)。

## Communication

If you find bugs or have good suggestions, please feel free to raise an issue on GitHub or scan the QR code to join the WeChat group below for discussion.

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730211916457.png" width="300" height="300"/>

## Stars

[![Stargazers over time](https://starchart.cc/outlaws-bai/Galaxy.svg?variant=adaptive)](https://starchart.cc/outlaws-bai/Galaxy)

## Commonly used addresses:

[BurpDownload](https://portswigger.net/burp/releases#professional)

[BurpJavaDoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[BurpExtExamples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[JDK17Download](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)

[JDK21Download](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
