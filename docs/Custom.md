# 自定义代码文件

> 需要一定的编程能力。

如不了解Http Hook功能的基本信息，请先阅读 [Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md)

在Burp中打开插件Tab，选择你要使用的语言，点击下方的New按钮并输入文件名，之后在编辑器会生成对应语言的模版文件，并且其中有四个函数(java为驼峰，js/python为蛇形)：

`hookRequestToBurp`，`hookRequestToServer`， `hookResponseToBurp`， `hookResponseToClient`

你需要实现图中的四个函数，实现逻辑是 **使用项目提供的工具类按照你的需求修改函数提供给你的请求/响应，以达到你的需求**。所以你必须熟悉项目中的DataObjects和Utils。

## DataObjects

> 推荐点击链接阅读代码

### Request

> HTTP请求。 [Request.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java)

获取/修改请求方法

```java
request.getMethod() -> String
```

```java
request.setMethod(String method) -> void
```

获取/修改请求路径

```java
request.getPath() -> String
```

```java
request.setPath(String path) -> void
```

获取/修改Query参数

```java
request.getQuery() -> Query extends Map<String, List<String>>
```

```java
request.setQuery(Query query) -> void
```

获取/修改请求头

```java
request.getHeaders() -> Headers extends Map<String, List<String>>
```

```java
request.setHeaders(Headers headers) -> void
```

获取/修改响应体

```java
request.getContent() -> byte[]
request.getBody() -> String
```

```java
request.setContent(byte[] content) -> content
```

### Response

> HTTP响应。 [Response.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java)

获取/修改状态码

```java
request.getStatusCode() -> int
```

```java
request.setStatusCode(int statusCode) -> void
```

获取/修改响应头

```java
request.getHeaders() -> Headers extends Map<String, List<String>>
```

```java
request.setHeaders(Headers headers) -> void
```

获取/修改响应体

```java
request.getContent() -> byte[]
request.getBody() -> String
```

```java
request.setContent(byte[] content) -> content
```

## Utils

> 推荐点击链接阅读代码

### CodeUtil

hex、base64编码工具类。[CodeUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java)

> 在python中可以导入base64和binascii使用

base64

```java
CodeUtil.b64decode(String data) -> byte[]
CodeUtil.b64encode(byte[] data) -> byte[]
CodeUtil.b64encodeToString(byte[] data) -> String
```

hex

```java
CodeUtil.hexDecode(String data) -> byte[]
CodeUtil.hexEncode(byte[] data) -> byte[]
CodeUtil.hexEncodeToString(byte[] data) -> String
```

### JsonUtil

json解析工具类。[JsonUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java)

> 在python中可以导入json使用

json字符串转Map或者说dict

```java
JsonUtil.jsonStrToMap(String jsonStr) -> Map
```

json字符串转List

```java
JsonUtil.jsonStrToList(String jsonStr) -> Map
```

对象转json字符串

```java
JsonUtil.toJsonStr(Object obj) -> String
```

### CryptoUtil

> 加解密工具类。[CryptoUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CryptoUtil.java)
>
> 项目中加解密使用java中的bouncycastle，具体的transformation可查询官方文档
>
> 不建议利用脚本中的语言去引入本地的加解密依赖，这样可能会导致兼容问题。

AES加密/解密

```java
CryptoUtil.aesEncrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

```java
CryptoUtil.aesDecrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

RSA加密/解密

```java
CryptoUtil.rsaEncrypt(String transformation, byte[] data, byte[] publicKey) -> byte[]
```

```java
CryptoUtil.rsaDecrypt(String transformation, byte[] data, byte[] privateKey) -> byte[]
```

SM2加密/解密

```java
CryptoUtil.sm2Encrypt(byte[] data, byte[] publicKey) -> byte[]
```

```java
CryptoUtil.sm2Decrypt(byte[] data, byte[] privateKey) -> byte[]
```

SM4加密/解密

```java
CryptoUtil.sm4Encrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

```java
CryptoUtil.sm4Decrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

### HashUtil

> hash计算工具类。[HashUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HashUtil.java)
>
> 项目中hash计算使用java中的bouncycastle，具体的algorithm可查询官方文档

```java
HashUtil.calc(byte[] data, String algorithm) -> byte[]
HashUtil.calcToHex(byte[] data, String algorithm) -> String
HashUtil.calcToBase64(byte[] data, String algorithm) -> String
```

### MacUtil

> mac计算工具类。[MacUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/MacUtil.java)
>
> 项目中mac计算使用java中的bouncycastle，具体的algorithm可查询官方文档

```java
MacUtil.calc(byte[] data, byte[] secret, String algorithm) -> byte[]
MacUtil.calcToHex(byte[] data, byte[] secret, String algorithm) -> String
MacUtil.calcToBase64(byte[] data, byte[] secret, String algorithm) -> String
```

## Debug

在脚本中你可以使用log对象打印日志进行调试，比如

```
log.info("request: {}", request)
```

## Log

所有日志会显示在两个地方：

1. Burp Extensions，选中插件可以看到，不过这里Burp限制了显示行数
2. work dir下有run.log文件，包含了所有日志

