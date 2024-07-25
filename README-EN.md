# Galaxy

Burp plugin, mainly to achieve automatic decryption in the `HTTP message secondary encryption` scenario, in order to display plaintext message in Burp.

## Function Introduction

### Http Hook

Use the new version of Burp `Montoya API` for development, extract four stages from it, and use Python, JS, Java, or Grpc to implement the processing logic for the four stages to meet the requirements.

> The project has built-in multiple encryption and decryption rules, which can be used out of the box for common algorithms.

Further understanding：[Detail](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md)

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/hook.gif)

### Other functions:

1. [Parse Swagger Api Doc](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Parse-Swagger-Api-Doc):  Analyze the swagger document, generate requests for all URLs, including parameters, paths, and descriptions. Optionally, automatically send:。
2. [Bypass Host Check](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Host-Check):  Bypass the server's validation of the host in csrf/ssrf test points.。
3. [Bypass Auth Of Path](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Other.md#Bypass-Auth-Of-Path):  Bypass certain authentication/authorization/interception by modifying the Path.。

## Installation guide:

Plugin Download:：[Download](https://github.com/outlaws-bai/Galaxy/releases)

Plugin installation:：`Extender -> Extensions -> Add - Select File -> Next`

Build your own:：`build.gradle -> shadowJar`

**Note:**:

1. The project is developed using Burp `Montoya API`, with a minimum required version of Burp.`v2023.10.3.7`。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
2. Develop and compile the project using JDK 17, please ensure that the JDK used to start Burp is not lower than 17.。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)

## Advantages and characteristics:

1. Simple and efficient: Users do not need to start unnecessary local services. After successful configuration, message encryption and decryption can be done automatically.。
2. Easy to get started: Universal algorithms have examples available, making it easy to use out of the box.。
3. Flexible: can be implemented in multiple ways such as Python, JS, Java, and Grpc to meet the requirements.
4. Support a wide range: such as encryption algorithm combination, custom algorithms, dynamic keys, etc. can all be supported.。

## Next

1. Supports use with desktop scanners, allowing the scanner to scan plaintext requests and receive plaintext responses.。
2. Propose the method of using in the case of asymmetric encryption (unknown private key)。

## Communication

> Looking forward to your **star** if this project is helpful to you. ~

If you find bugs or have good suggestions, please feel free to raise an issue on GitHub or scan the QR code to join the WeChat group below for discussion.

(If the QR code is invalid, please add the WeChat ID outlaws_bai and note "Galaxy交流". )

<img src="https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240714204644975.png" height="300px" width="240px" />

## Commonly used addresses:

[BurpDownload](https://portswigger.net/burp/releases#professional)

[BurpJavaDoc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)

[BurpExtExamples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)

[JDK17Download](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
