# 自定义 hook 脚本

> 需要一定的编程能力，java/python/js。
>
> 不了解Http Hook的原理，请先阅读 [Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md) 。

这个[靶场](https://github.com/outlaws-bai/GalaxyDemo)实现了常见的一些加解密逻辑，对应的hook脚本在示例中都有，可以体验一下，也可以尝试写对应的hook脚本，与内置示例对照增加熟练度。

在hook脚本中你需要实现/修改其中的四个Hook函数，每个函数应该完成的是：

1. 从请求/响应找到加密后的数据。
2. 解密该数据（用项目内置加解密工具类 or jsrpc/frida调用游览器/客户端中的加解密函数）。
3. 修改请求/响应对象。

## 示例

我们以靶场中的aes+rsa为例。启用方式见 https://github.com/outlaws-bai/GalaxyDemo

首先，我们看一下这一项的加解密代码：

![j](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730222750886.png)

逻辑如下：

1. 生成一个随机的32位密钥。
2. 使用该随机密钥，通过 `aes-ecb` 加密原始请求的json。
3. 使用公钥1通过 `rsa` 加密随机密钥。
4. 生成新的json并发送请求。
5. 获取响应后，先用私钥2，通过 `rsa` 解密响应json中的key。
6. 使用解密出的key，通过 `aes-ecb` 解密出原始的json。

![image-20240730223200709](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730223200709.png)

很明显，我们想要hook这个加解密逻辑，hook脚本的逻辑应该如下。

**hookRequestToBurp**：

1. 获取被加密的数据、被加密的`aes-ecb`密钥。
2. 使用私钥1，通过内置的  `rsa`  解密函数，解密出被加密的 `aes-ecb` 密钥。
3. 通过内置的 `aes`  解密函数，解密出被加密的原始数据。
4. 更新请求对象。

![image-20240730224247534](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730224247534.png)

**hookRequestToServer**：

1. 获取被 `hookRequestToBurp` 解密的数据。
2. 用写死的32位密钥，将1中的数据进行 `aes-ecb` 加密。（这里服务端没做随机密钥检查，所以可以写死）
3. 使用公钥1，通过内置的 `rsa` 加密函数，加密这个32位的密钥。
4. 更新请求对象。

![image-20240730224718564](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730224718564.png)

**hookResponseToBurp**：同  `hookRequestToBurp` ， 只是步骤2中的私钥1替换位私钥2。

![image-20240730230929689](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730230929689.png)

**hookResponseToClient**：同 `hookRequestToServer`，只是步骤3中的公钥1替换位公钥2。

![image-20240730231316189](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730231316189.png)

最终效果如图，该示例为内置的 `AesRsa`

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/hook.gif)

## 测试

当启动服务后，会在任意请求/响应编辑器中绑定Encrypt/Decrypt按钮，可以用于测试。

同时在脚本中你可以使用log对象打印日志，来判断代码逻辑是否正确。

```java
log.info("request: {}", request)
```

运行中的日志会发送到两个地方：

1. `Burp -> Extensions -> Galaxy -> Output/Errors` （仅显示部分）
2. [WorkDir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#work-dir) / run.log

## 常用函数

> 推荐点击链接阅读代码，也可以在代码编辑器中输入指定按键查看可用函数。

### DataObjects

#### Request

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

#### Response

> HTTP响应。 [Response.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java)

获取/修改状态码

```java
response.getStatusCode() -> int
```

```java
response.setStatusCode(int statusCode) -> void
```

获取/修改响应头

```java
response.getHeaders() -> Headers extends Map<String, List<String>>
```

```java
response.setHeaders(Headers headers) -> void
```

获取/修改响应体

```java
response.getContent() -> byte[]
response.getBody() -> String
```

```java
response.setContent(byte[] content) -> content
```

#### Headers/Query

> 请求头或者Query对象，均继承自Map\<String, List\<String\>\>。

获取value

```java
headers.get("Host") -> List<String>
headers.getFirst("Host") -> String
```

修改value

```java
// {"Host": ["www.baidu.com"]}
headers.add("Host", "192.168.1.4") => {"Host": ["www.baidu.com", "192.168.1.4"]}
headers.put("Host", "192.168.1.4") => {"Host": ["192.168.1.4"]}
headers.remove("Host") => {}
```

### Utils

#### CodeUtil

> hex、base64编码工具类。[CodeUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java)
>
> 在python中可以使用自带库。

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

#### FactorUtil

> 因子工具类。[FactorUtil](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/FactorUtil.java)
>
> 在python中可以使用自带库。

生成uuid

```java
FactorUtil.uuid()
```

生成随机字符串

```java
FactorUtil.randomString(int length)
```

获取当前时间

```java
FactorUtil.currentDate()
```

#### JsonUtil

> json解析工具类。[JsonUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java)
>
> 在python中可以使用自带库

json字符串转Map或者说dict

```java
JsonUtil.jsonStrToMap(String jsonStr) -> Map
```

json字符串转List

```java
JsonUtil.jsonStrToList(String jsonStr) -> List
```

对象转json字符串

```java
JsonUtil.toJsonStr(Object obj) -> String
```

#### CryptoUtil

> 加解密工具类。[CryptoUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CryptoUtil.java)
>
> 不建议利用脚本中的语言去引入本地的加解密依赖，这样可能会导致兼容问题。
>
> 项目中加解密使用java中的bouncycastle，具体的transformation可查询官方文档

AES加密/解密

```java
CryptoUtil.aesEncrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

```java
CryptoUtil.aesDecrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

RSA加密/解密

```java
CryptoUtil.rsaEncrypt(byte[] data, byte[] publicKey) -> byte[]
```

```java
CryptoUtil.rsaDecrypt(byte[] data, byte[] privateKey) -> byte[]
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

#### HashUtil

> hash计算工具类。[HashUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HashUtil.java)
>
> 项目中hash计算使用java中的bouncycastle，具体的algorithm可查询官方文档

```java
HashUtil.calc(String algorithm, byte[] data) -> byte[]
HashUtil.calcToHex(String algorithm, byte[] data) -> String
HashUtil.calcToBase64(String algorithm, byte[] data) -> String
```

#### MacUtil

> mac计算工具类。[MacUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/MacUtil.java)
>
> 项目中mac计算使用java中的bouncycastle，具体的algorithm可查询官方文档

```java
MacUtil.calc(String algorithm, byte[] data, byte[] secret) -> byte[]
MacUtil.calcToHex(String algorithm, byte[] data, byte[] secret) -> String
MacUtil.calcToBase64(String algorithm, byte[] data, byte[] secret) -> String
```
