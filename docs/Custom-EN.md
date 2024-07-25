# Custom code file:

> Requires a certain level of programming ability.。

If you do not understand the basic information of the Http Hook function, please read: [Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md)

Open the plugin tab in Burp, select the language you want to use, click the "New" button below, and enter the file name. Then, a template file in the corresponding language will be generated in the editor, including four functions (in camel case for Java, and in snake case for JavaScript/Python).：

`hookRequestToBurp`，`hookRequestToServer`， `hookResponseToBurp`， `hookResponseToClient`

You need to implement four of the functions, and the implementation logic is to **use the provided project tools to modify requests/responses according to your needs**. So you must be familiar with the DataObjects and Utils in the project.

## DataObjects

> Recommended to click the link to read the code:

### Request

> HTTP request. [Request.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java)

Get/Modify request method:

```java
request.getMethod() -> String
```

```java
request.setMethod(String method) -> void
```

Get/Modify Request Path:

```java
request.getPath() -> String
```

```java
request.setPath(String path) -> void
```

Get/Modify Query Parameters:

```java
request.getQuery() -> Query extends Map<String, List<String>>
```

```java
request.setQuery(Query query) -> void
```

Get/Modify Request Headers:

```java
request.getHeaders() -> Headers extends Map<String, List<String>>
```

```java
request.setHeaders(Headers headers) -> void
```

Retrieve/Modify Response Body:

```java
request.getContent() -> byte[]
request.getBody() -> String
```

```java
request.setContent(byte[] content) -> content
```

### Response

> HTTP Response:。 [Response.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java)

Get/Modify Status Code:

```java
response.getStatusCode() -> int
```

```java
response.setStatusCode(int statusCode) -> void
```

Get/modify response headers:

```java
response.getHeaders() -> Headers extends Map<String, List<String>>
```

```java
response.setHeaders(Headers headers) -> void
```

Get/Modify Response Body:

```java
response.getContent() -> byte[]
response.getBody() -> String
```

```java
response.setContent(byte[] content) -> content
```

### Headers Query

> Request headers or Query objects both inherit from Map<String, List<String>>.

Get value:

```java
headers.get("Host") -> List<String>
headers.getFirst("Host") -> String
```

Modify value:

```java
// {"Host": ["www.baidu.com"]}
headers.add("Host", "192.168.1.4") => {"Host": ["www.baidu.com", "192.168.1.4"]}
headers.put("Host", "192.168.1.4") => {"Host": ["192.168.1.4"]}
headers.remove("Host") => {}
```

## Utils

> Recommended to click the link to read the code:

### CodeUtil

> hex, base64 encoding tool class:[CodeUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java)
>
> In Python, you can import base64 and binascii to use:

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

### FactorUtil

> Factor Tool Class:[FactorUtil](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/FactorUtil.java)

Generate uuid

```java
FactorUtil.uuid()
```

Generate random string:

```java
FactorUtil.randomString(int length)
```

Get current time:

```java
FactorUtil.currentDate()
```

### JsonUtil

> JSON parsing tool class:。[JsonUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java)
>
> In Python, you can import and use JSON.

Convert JSON string to Map or dictionary.

```java
JsonUtil.jsonStrToMap(String jsonStr) -> Map
```

Convert JSON string to List:

```java
JsonUtil.jsonStrToList(String jsonStr) -> List
```

Convert object to JSON string:

```java
JsonUtil.toJsonStr(Object obj) -> String
```

### CryptoUtil

> Encryption and decryption tool class:[CryptoUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CryptoUtil.java)
>
> It is not recommended to introduce local encryption and decryption dependencies using the language in the script, as this may lead to compatibility issues.
>
> Use the Bouncycastle library in Java for encryption and decryption in the project. Refer to the official documentation for specific transformations.

AES encryption/decryption:

```java
CryptoUtil.aesEncrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

```java
CryptoUtil.aesDecrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

RSA encryption/decryption:

```java
CryptoUtil.rsaEncrypt(String transformation, byte[] data, byte[] publicKey) -> byte[]
```

```java
CryptoUtil.rsaDecrypt(String transformation, byte[] data, byte[] privateKey) -> byte[]
```

SM2 encryption/decryption:

```java
CryptoUtil.sm2Encrypt(byte[] data, byte[] publicKey) -> byte[]
```

```java
CryptoUtil.sm2Decrypt(byte[] data, byte[] privateKey) -> byte[]
```

SM4 encryption/decryption:

```java
CryptoUtil.sm4Encrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

```java
CryptoUtil.sm4Decrypt(String transformation, byte[] data, byte[] secret, Map<String, Object> params) -> byte[]
```

### HashUtil

> Hash calculation tool class:[HashUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HashUtil.java)
>
> In the project, hash calculation uses bouncycastle in Java, and the specific algorithm can be found in the official documentation.

```java
HashUtil.calc(byte[] data, String algorithm) -> byte[]
HashUtil.calcToHex(byte[] data, String algorithm) -> String
HashUtil.calcToBase64(byte[] data, String algorithm) -> String
```

### MacUtil

> Mac calculator class:[MacUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/MacUtil.java)
>
> In the project, the Mac calculation uses the Bouncy Castle in Java. Specific algorithms can be found in the official documentation.

```java
MacUtil.calc(byte[] data, byte[] secret, String algorithm) -> byte[]
MacUtil.calcToHex(byte[] data, byte[] secret, String algorithm) -> String
MacUtil.calcToBase64(byte[] data, byte[] secret, String algorithm) -> String
```

## Test

When the service is started, the Encrypt/Decrypt button will be bound on the Repeater page, which can be used for testing:。

At the same time, you can use the log object in the script to print logs to determine if the code logic is correct.。

```java
log.info("request: {}", request)
```

## Log

All logs will be displayed in two places:：

1. Burp Extensions，When you select the plug-in, you can see that Burp limits the number of displayed lines here.
2. There is a run.log file in the work directory, which contains all the logs.

