# 自定义代码文件

> 适用于想要通过Python、JavaScript、Java代码完成HTTP HOOK功能，并且项目示例不满足需求。
>
> 需要一定的编程能力。

如不了解Http Hook功能的基本信息，请先阅读 [Http Hook](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md)

在Burp中打开插件Tab，选择你要使用的语言后，点击下方的New按钮，我们以Java为例，代码文件名称为Example

![image-20240717225425967](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240717225425967.png)

你需要实现图中的四个函数，实现逻辑是 **使用项目提供的工具类按照你的需求修改函数提供给你的请求/响应，以达到你的需求**。

所以你必须熟悉项目中的DataObjects和Utils。

## DataObjects

> 推荐点击链接阅读代码

Request：请求。 [Request.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java)

1. 获取请求头：Headers getHeaders()
2. 获取Body：String getBody()

Response：响应。 [Response.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java)

1. 获取请求头：Headers getHeaders()
2. 获取Body：String getBody()

Headers：请求/响应头。[Headers.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Headers.java)

1. 获取：List\<String\> get(String key)
2. 覆盖：Headers put(String key, String value)
3. 追加：Headers add(String key, String value)

Cookies：请求Cookie。[Cookies.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Cookies.java)

1. 获取：List\<String\> get(String key)
2. 覆盖：Cookies put(String key, String value)
3. 追加：Cookies add(String key, String value)

Query：请求query参数。[Query.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Query.java)

1. 获取：List\<String\> get(String key)
2. 覆盖：Query put(String key, String value)
3. 追加：Query add(String key, String value)

Form：请求Body中通过`application/x-www-form-urlencoded`传递的数据。[Form.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Form.java)

1. 获取：List\<String\> get(String key)
2. 覆盖：Form put(String key, String value)
3. 追加：Form add(String key, String value)

FormData：请求Body中通过`multipart/form-data`传递的数据。[FormData.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/FormData.java)

1. 获取：List\<T\> get(String key)
2. 覆盖：Form put(String key, T value)
3. 追加：Form add(String key, T value)

UploadFile：请求Body中通过`multipart/form-data`传递的文件。[UploadFile.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/UploadFile.java)

1. 获取文件名：String getFilename()
2. 获取文件内容：byte[] getContent()

## Utils

> 推荐点击链接阅读代码

ByteUtil：byte数组处理的工具类。[ByteUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/ByteUtil.java)

CodeUtil：hex、base64编码工具类。[CodeUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java)

HttpUtil：http相关工具类。[HttpUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HttpUtil.java)

JsonUtil：json解析工具类。[JsonUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java)

YamlUtil：yaml解析工具类。[YamlUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/YamlUtil.java)

CryptoUtil：加解密工具类。[CryptoUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CryptoUtil.java)

HashUtil：hash计算工具类。[HashUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HashUtil.java)

## 示例

说起来还是比较抽象，我们假设有一个场景（对称加密+非堆成加密），也是现在比较主流的。

服务端生成了两对公私钥pri1，pub1，pri2，pub2，客户端持有pub1，pri2，服务端持有pri2，pub2。当然有一个大前提，在这种非加密场景下想进行中间人攻击，我们必须已知pri2。

1. 客户端生成一个长度为32的随机密钥
2. 通过`AES` 将请求body加密，再使用pub1将1中的随机密钥进行`RSA`加密
3. 服务端收到body后使用pri1解密被`RSA`加密的随机密钥
4. 使用解密得到的随机密钥，再解密被`AES`加密的原始body
5. 服务端进行正常的业务逻辑处理，处理完成后
6. 服务端生成一个长度为32的随机密钥
7. 通过`AES` 将响应body加密，再使用pub2将6中的随机密钥进行`RSA`加密
8. 客户端收到body后使用pri2解密被`RSA`加密的随机密钥
9. 使用解密得到的随机密钥，再解密被`AES`加密的原始body
10. 客户端进行正常的业务逻辑

coding...

