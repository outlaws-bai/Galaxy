from org.m2sec.core.utils import (
    CodeUtil,
    CryptoUtil,
    HashUtil,
    JsonUtil,
    MacUtil,
    FactorUtil,
)
from org.m2sec.core.models import Request, Response
from java.lang import String


"""
跨语言能力来自于jython (对应python2.7)
按 Ctrl（command） + ` 可查看内置函数
"""

ALGORITHM = "DES/CBC/PKCS5Padding"
secret = b"12345678"
iv = b"12345678"
paramMap = {"iv": iv}
jsonKey = "data"
log = None


def hook_request_to_burp(request):
    """HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

    Args:
        request (Request): 请求对象

    Returns:
        Request: 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取需要解密的数据
    encryptedData = get_data(request.getContent())
    # 调用函数解密
    data = decrypt(encryptedData)
    # 更新body为已解密的数据
    request.content = data
    return request


def hook_request_to_server(request):
    """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

    Args:
        request (Request): 请求对象

    Returns:
        Request: 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
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
    """HTTP响应从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

    Args:
        response (Response): 响应对象

    Returns:
        Response: 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取需要解密的数据
    encryptedData = get_data(response.getContent())
    # 调用函数解密
    data = decrypt(encryptedData)
    # 更新body
    response.content = data
    return response


def hook_response_to_client(response):
    """HTTP响应从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

    Args:
        response (Response): 响应对象

    Returns:
        Response: 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
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
    return CryptoUtil.desDecrypt(ALGORITHM, content, secret, paramMap)


def encrypt(content):
    return CryptoUtil.desEncrypt(ALGORITHM, content, secret, paramMap)


def get_data(content):
    return CodeUtil.b64decode(JsonUtil.jsonStrToMap(String(content)).get(jsonKey))


def to_data(content):
    jsonBody = {}
    jsonBody[jsonKey] = CodeUtil.b64encodeToString(content)
    return JsonUtil.toJsonStr(jsonBody).encode()


def set_log(log1):
    """程序在最开始会自动调用该函数，在上方函数可以放心使用log对象"""
    global log
    log = log1
