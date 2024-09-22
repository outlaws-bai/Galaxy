import json
import base64
from java.org.m2sec.core.utils import (
    CodeUtil,
    CryptoUtil,
    HashUtil,
    MacUtil,
    FactorUtil,
)
from java.org.m2sec.core.models import Request, Response
from java.lang import Byte, ThreadLocal

"""
内置模版，需要自定义代码文件时查看该文档：https://github.com/outlaws-bai/Galaxy/blob/main/docs/Custom.md
按 Ctrl（command） + ` 可查看内置函数
"""
SYMMETRIC_ALGORITHM = "AES/ECB/PKCS5Padding"
aesSecret = ThreadLocal()
aesSecret.set("32byteslongsecretkeyforaes256!aa".encode())
ASYMMETRIC_ALGORITHM = "RSA"
publicKeyBase64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7JoQAWLsovzHjaUMZg2lwO4LCuP97mitUc4chqRlQD3NgyCWLqEnYyM+OJ7i6cyMuWLwGtMi29DoKLjpE/xRZR0OUk46PDCAtyDgIyejK7c7KlZTbiqb4PtiJNLZgg0UP62kLMycnpY/wg/R2G9g+7MiJWUV5SR+Lhryv8CWezQIDAQAB"
privateKeyBase64 = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALsmhABYuyi/MeNpQxmDaXA7gsK4/3uaK1RzhyGpGVAPc2DIJYuoSdjIz44nuLpzIy5YvAa0yLb0OgouOkT/FFlHQ5STjo8MIC3IOAjJ6MrtzsqVlNuKpvg+2Ik0tmCDRQ/raQszJyelj/CD9HYb2D7syIlZRXlJH4uGvK/wJZ7NAgMBAAECgYAhgbhRbZF4rp6Kdh6e00HN58G2BjQrl4MZeCOh+aoABPwlwD/EnMk36GAMtfzjWNjcI+PqGXT0GI7JotQo5ThpoweXX/uoeGOW+UkYLA6a67lmxfoZsDtY2+jnaWIs2c7Itz3ClRxo4tYwCoPNjtaBpMfPgZaYg2QN8/wLQPI66wJBAM0xpjb2OlLDs75lVxbm6v6Dx3YBS20GSqJqvf+14a/k7mrZ3PmAHOfqTqKOwbVQJmLbeOpU+sUBpeLpILKOCLcCQQDpfSsDhdosC6qTL9XnF2jS49iws2RBKw5YjDkClwA6VMNj5uzL1Rl7/AimLRMnB4BwrD95ksuOJsqNXW6wRGibAkAkk28PaQCodB38GFBX0r2ctJy/Wie5vV9caC6KAD/EfMhK357WEpIUfN2beFrrGOhewsRg8NjqeQq60dd0PIEtAkBYAm03O7n8Bj26kzpejA1gCLBCEqyEf/U9XUWT+1UDp7Wqr32sa1vaxyp/cNgaSxKX5eVbLwD5SRfqZ0B0wqRnAkATpUNiCqjQVS+OI5dwjoI1Rx3oI8pyKWOg3+QIHIRgL3pc8HLdZ2BkX4Vf6ANb4+noQnD/di1Mj+0pUL8RhIJE"

publicKey = CodeUtil.b64decode(publicKeyBase64)
privateKey = CodeUtil.b64decode(privateKeyBase64)
jsonKey1 = "data"
jsonKey2 = "key"
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
    # 获取用来解密的密钥，该密钥已使用publicKey进行rsa加密
    encryptedKey = get_key(request.getContent())
    # 调用内置函数解密，拿到aes密钥
    key = asymmetric_decrypt(encryptedKey, privateKey)
    aesSecret.set(key)
    # 调用内置函数解密报文
    data = symmetric_decrypt(encryptedData, key)
    # 更新body为已解密的数据
    request.setContent(data)
    return request


def hook_request_to_server(request):
    """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

    Args:
        request (Request): 请求对象

    Returns:
        Request: 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取被解密的数据
    data = request.getContent()
    # 调用内置函数加密回去，这里使用设置的aesSecret进行加密
    encryptedData = symmetric_encrypt(data, aesSecret.get())
    # 调用内置函数加密aesSecret
    encryptedKey = asymmetric_encrypt(aesSecret.get(), publicKey)
    # 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData, encryptedKey)
    # 更新body
    request.setContent(body)
    return request


def hook_response_to_burp(response):
    """HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

    Args:
        response (Response): 响应对象

    Returns:
        Response: 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取需要解密的数据
    encryptedData = get_data(response.getContent())
    # 调用内置函数解密
    data = symmetric_decrypt(encryptedData, aesSecret.get())
    # 更新body
    response.setContent(data)
    return response

def hook_response_to_client(response):
    """HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

    Args:
        response (Response): 响应对象

    Returns:
        Response: 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取被解密的数据
    data = response.getContent()
    # 调用内置函数加密回去
    encryptedData = symmetric_encrypt(data, aesSecret.get())
    # 更新body
    # 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData, None)
    # 更新body
    response.setContent(body)
    return response

def asymmetric_decrypt(content, secret):
    return CryptoUtil.rsaDecrypt(ASYMMETRIC_ALGORITHM, content, secret)

def asymmetric_encrypt(content, secret):
    return CryptoUtil.rsaEncrypt(ASYMMETRIC_ALGORITHM, content, secret)

def symmetric_decrypt(content, secret):
    return CryptoUtil.aesDecrypt(SYMMETRIC_ALGORITHM, content, secret, None)

def symmetric_encrypt(content, secret):
    return CryptoUtil.aesEncrypt(SYMMETRIC_ALGORITHM, content, secret, None)

def get_data(content):
    return CodeUtil.b64decode(json.loads(convert_bytes(content))[jsonKey1])

def get_key(content):
    return CodeUtil.b64decode(json.loads(convert_bytes(content))[jsonKey2])


def to_data(content, secret):
    jsonBody = {}
    jsonBody[jsonKey1] = CodeUtil.b64encodeToString(content)
    if secret is not None:
        jsonBody[jsonKey2] = CodeUtil.b64encodeToString(secret)
    return json.dumps(jsonBody).encode()

def set_log(log1):
    """程序在最开始会自动调用该函数，在上方函数可以放心使用log对象"""
    global log
    log = log1
    import sys
    log.info("python version: {}", sys.version)

def convert_bytes(java_byte_array):
    """将java的字节数组转为graalpy的字节数组, java的字节数组对应到graalpy中的类型是foreign对象, 如果想要用graalpy处理java的字节数组，最好先调用该函数"""
    return bytes([Byte.toUnsignedInt(b) for b in java_byte_array])


