from org.m2sec.core.utils import (
    ByteUtil,
    CodeUtil,
    CryptoUtil,
    HashUtil,
    HttpUtil,
    JsonUtil,
    MacUtil,
    YamlUtil,
)
from org.m2sec.core.models import Request, Response
from java.lang import String


"""
You can complete the requirements in Hooker by calling encryption and decryption functions in Java.

The available classes are as follows...
utils：可能用到的工具类
https://github.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/utils
"""

ALGORITHM = "AES/CBC/PKCS5Padding"
secret = b"32byteslongsecretkeyforaes256!aa"
iv = b"16byteslongiv456"
paramMap = {"iv": iv}
jsonKey = "data"
log = None


def set_log(log1):
    global log
    log = log1


def hook_request_to_burp(request):
    """HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

    Args:
        request (_type_): 请求对象, https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java

    Returns:
        _type_: 经过处理后的request对象，返回null代表不需要处理
    """
    # 获取需要解密的数据
    encryptedData = get_data(request.getContent())
    # 调用函数解密
    data = decrypt(encryptedData)
    # 更新body为已加密的数据
    request.content = data
    return request


def hook_request_to_server(request):
    """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

    Args:
        request (Request): 请求对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java

    Returns:
        _type_: 经过处理后的request对象，返回null代表不需要处理
    """
    # 获取被解密的数据
    data = request.content
    # 调用函数加密回去
    encryptedData = encrypt(data)
    # 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData)
    # 更新body
    request.content = body
    return request


def hook_response_to_burp(response):
    """HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

    Args:
        response (Request): 响应对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java

    Returns:
        _type_: 经过处理后的response对象，返回null代表不需要处理
    """
    # 获取需要解密的数据
    encryptedData = get_data(response.getContent())
    # 调用函数解密
    data = decrypt(encryptedData)
    # 更新body
    response.content = data
    return response


def hook_response_to_client(response):
    """HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

    Args:
        response (Response): 响应对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java

    Returns:
        _type_: 经过处理后的response对象，返回null代表不需要处理
    """
    # 获取被解密的数据
    data = response.getContent()
    # 调用函数加密回去
    encryptedData = encrypt(data)
    # 更新body
    # 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData)
    # 更新body
    response.content = body
    return response


def decrypt(content):
    """解密函数

    Args:
        content (Response): 要解密的数据

    Returns:
        _type_: 解密结果
    """
    return CryptoUtil.aesDecrypt(ALGORITHM, content, secret, paramMap)


def encrypt(content):
    """加密函数

    Args:
        content (byte[]): 要加密的数据

    Returns:
        _type_: 加密结果
    """
    return CryptoUtil.aesEncrypt(ALGORITHM, content, secret, paramMap)


def get_data(content):
    return CodeUtil.b64decode(JsonUtil.jsonStrToMap(String(content)).get(jsonKey))


def to_data(content):
    jsonBody = {}
    jsonBody[jsonKey] = CodeUtil.b64encodeToString(content)
    return JsonUtil.toJsonStr(jsonBody).encode()
