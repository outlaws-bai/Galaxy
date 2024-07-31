# Custom 

The premise is that the encryption and decryption logic of the website has been reverse engineered. If you can't reverse engineer it, you can ask for help in the group, or just go to bed ( ͡° ͜ʖ ͡°).

> Requires a certain level of programming ability,java/python/js.。
>
> If you do not understand the basic information of the Http Hook function, please read: [Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook_EN.md)
>

This [target field](https://github.com/outlaws-bai/GalaxyDemo) implements some common encryption and decryption logic, and the corresponding hook scripts are available in the examples. You can try it out and also try writing corresponding hook scripts to increase proficiency by comparing them with the built-in examples.


If you need to customize the hook script, you can modify the code in the example or create a new one.



In the hook script, you need to implement/modify four Hook functions, and each function should accomplish the following:

1. Find the encrypted data from the request/response.
2. Call the project encryption/decryption function.
3. Modify request/response object.

## Example

We take the aes+rsa in the target field as an example. The method of enabling can be found at https://github.com/outlaws-bai/GalaxyDemo

First, let's take a look at the encryption and decryption code for this item:

![j](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730222750886.png)

The logic is as follows:

1. Generate a random 32-bit key.
2. Use this random key to encrypt the original request's JSON through `aes-ecb`.
3. Encrypt the random key through `rsa` using public key 1.
4. Generate a new JSON and send the request.
5. After receiving the response, decrypt the key in the response JSON using private key 2 through `rsa`.
6. Use the decrypted key to decrypt the original JSON through `aes-ecb`.

![image-20240730223200709](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730223200709.png)

Obviously, we want to hook this encryption and decryption logic. The logic of the hook script should be as follows.

**hookRequestToBurp**：

1. Get the encrypted data and the encrypted `aes-ecb` key.
2. Use private key 1 to decrypt the encrypted `aes-ecb` key through the built-in `rsa` decryption function.
3. Decrypt the original data using the built-in `aes` decryption function.
4. Update the request object.

![image-20240730224247534](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730224247534.png)

**hookRequestToServer**：

1. Get the data decrypted by `hookRequestToBurp`.
2. Encrypt the data from step 1 using a hardcoded 32-bit key through `aes-ecb`. (The server does not check for random key, so it can be hardcoded)
3. Encrypt this 32-bit key using public key 1 through the built-in `rsa` encryption function.
4. Update the request object.

![image-20240730224718564](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730224718564.png)

**hookResponseToBurp**: Same as `hookRequestToBurp`, but replace private key 1 with private key 2 in step 2.

![image-20240730230929689](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730230929689.png)

**hookResponseToClient**: Same as `hookRequestToServer`, but replace public key 1 with public key 2 in step 3.

![image-20240730231316189](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730231316189.png)

The final effect is as shown in the figure, this example is the built-in `AesRsa`.

![hook](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/hook.gif)

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

## Commonly used functions:

> Recommend clicking on the link to read the code, or you can press TAB in the code editor to view available functions.

### DataObjects

#### Request

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

#### Response

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

#### Headers Query

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

### Utils

> Recommended to click the link to read the code:

#### CodeUtil

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

#### FactorUtil

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

#### JsonUtil

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

#### CryptoUtil

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

#### HashUtil

> Hash calculation tool class:[HashUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HashUtil.java)
>
> In the project, hash calculation uses bouncycastle in Java, and the specific algorithm can be found in the official documentation.

```java
HashUtil.calc(byte[] data, String algorithm) -> byte[]
HashUtil.calcToHex(byte[] data, String algorithm) -> String
HashUtil.calcToBase64(byte[] data, String algorithm) -> String
```

#### MacUtil

> Mac calculator class:[MacUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/MacUtil.java)
>
> In the project, the Mac calculation uses the Bouncy Castle in Java. Specific algorithms can be found in the official documentation.

```java
MacUtil.calc(byte[] data, byte[] secret, String algorithm) -> byte[]
MacUtil.calcToHex(byte[] data, byte[] secret, String algorithm) -> String
MacUtil.calcToBase64(byte[] data, byte[] secret, String algorithm) -> String
```

