# Basic

## Work Dir

Linux/Mac: ~/.galaxy

Windows: %USERPROFILE%/.galaxy

## Expression

使用 `nashorn` 实现表达式的执行，可以理解就是一行JavaScript代码。

## Template

${xxx} 中的内容会被当作 `Expression` 执行，执行结果通过 `common-text` 替换模板。

> 在Template要使用$字符时，需要双写转义。

## DataObjects

Request：请求。 [Request.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java)

Response：响应。 [Response.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java)

Headers：请求/响应头。[Headers.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Headers.java)

Cookies：请求Cookie。[Cookies.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Cookies.java)

Query：请求query参数。[Query.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Query.java)

Form：请求Body中通过`application/x-www-form-urlencoded`传递的数据。[Form.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Form.java)

FormData：请求Body中通过`multipart/form-data`传递的数据。[FormData.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/FormData.java)

UploadFile：请求Body中通过`multipart/form-data`传递的文件。[UploadFile.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/UploadFile.java)

## Utils

ByteUtil：byte数组处理的工具类。[ByteUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/ByteUtil.java)

CodeUtil：hex、base64编码工具类。[CodeUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CodeUtil.java)

HttpUtil：http相关工具类。[HttpUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HttpUtil.java)

JsonUtil：json解析工具类。[JsonUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/JsonUtil.java)

YamlUtil：yaml解析工具类。[YamlUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/YamlUtil.java)

CryptoUtil：加解密工具类。[CryptoUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/CryptoUtil.java)

HashUtil：hash计算工具类。[HashUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/HashUtil.java)

MacUtil：mac计算工具类。[MacUtil.java](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/utils/MacUtil.java)
