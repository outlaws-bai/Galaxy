from org.m2sec.core.utils import *
from org.m2sec.core.models import *
from org.slf4j import Logger


"""
You can complete the requirements in Hooker by calling encryption and decryption functions in Java.

The available classes are as follows...
utils：可能用到的工具类
https://github.com/outlaws-bai/Galaxy/tree/main/src/main/java/org/m2sec/core/utils
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


