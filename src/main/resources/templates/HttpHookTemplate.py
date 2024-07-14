# Utils: https://github1s.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/utils
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
# DataObjects: https://github1s.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/models
from org.m2sec.core.models import Request, Response
from java.lang import String


"""
用法：使用JavaScript代码修改请求/响应对象的任何部分以满足需求。
内置：该项目内置了一些可能使用的DataObjects和Utils类，可以在代码中使用它们来满足加密、签名等要求。
警告(*)：你应该使用JavaScript代码调用项目中的内置Utils或DataObjects，不要尝试安装其他依赖项，这可能会导致兼容性问题。
"""

log = None

def hook_request_to_burp(request):
    """HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

    Args:
        request (_type_): 请求对象, https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java

    Returns:
        _type_: 经过处理后的request对象，返回null代表不需要处理
    """


def hook_request_to_server(request):
    """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

    Args:
        request (Request): 请求对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java

    Returns:
        _type_: 经过处理后的request对象，返回null代表不需要处理
    """


def hook_response_to_burp(response):
    """HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

    Args:
        response (Request): 响应对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java

    Returns:
        _type_: 经过处理后的response对象，返回null代表不需要处理
    """


def hook_response_to_client(response):
    """HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

    Args:
        response (Response): 响应对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java

    Returns:
        _type_: 经过处理后的response对象，返回null代表不需要处理
    """

def set_log(log1):
    global log
    log = log1


