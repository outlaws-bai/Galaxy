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
跨语言能力来自于graalpy (对应python3.11)
按 Ctrl（command） + ` 可查看内置函数
"""

SYMMETRIC_ALGORITHM = "AES/ECB/PKCS5Padding"
aesSecret = ThreadLocal()
aesSecret.set("32byteslongsecretkeyforaes256!aa".encode())
ASYMMETRIC_ALGORITHM = "RSA/ECB/PKCS1Padding"
publicKeyBase64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7JoQAWLsovzHjaUMZg2lwO4LCuP97mitUc4chqRlQD3NgyCWLqEnYyM+OJ7i6cyMuWLwGtMi29DoKLjpE/xRZR0OUk46PDCAtyDgIyejK7c7KlZTbiqb4PtiJNLZgg0UP62kLMycnpY/wg/R2G9g+7MiJWUV5SR+Lhryv8CWezQIDAQAB"
privateKeyBase64 = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALsmhABYuyi/MeNpQxmDaXA7gsK4/3uaK1RzhyGpGVAPc2DIJYuoSdjIz44nuLpzIy5YvAa0yLb0OgouOkT/FFlHQ5STjo8MIC3IOAjJ6MrtzsqVlNuKpvg+2Ik0tmCDRQ/raQszJyelj/CD9HYb2D7syIlZRXlJH4uGvK/wJZ7NAgMBAAECgYAhgbhRbZF4rp6Kdh6e00HN58G2BjQrl4MZeCOh+aoABPwlwD/EnMk36GAMtfzjWNjcI+PqGXT0GI7JotQo5ThpoweXX/uoeGOW+UkYLA6a67lmxfoZsDtY2+jnaWIs2c7Itz3ClRxo4tYwCoPNjtaBpMfPgZaYg2QN8/wLQPI66wJBAM0xpjb2OlLDs75lVxbm6v6Dx3YBS20GSqJqvf+14a/k7mrZ3PmAHOfqTqKOwbVQJmLbeOpU+sUBpeLpILKOCLcCQQDpfSsDhdosC6qTL9XnF2jS49iws2RBKw5YjDkClwA6VMNj5uzL1Rl7/AimLRMnB4BwrD95ksuOJsqNXW6wRGibAkAkk28PaQCodB38GFBX0r2ctJy/Wie5vV9caC6KAD/EfMhK357WEpIUfN2beFrrGOhewsRg8NjqeQq60dd0PIEtAkBYAm03O7n8Bj26kzpejA1gCLBCEqyEf/U9XUWT+1UDp7Wqr32sa1vaxyp/cNgaSxKX5eVbLwD5SRfqZ0B0wqRnAkATpUNiCqjQVS+OI5dwjoI1Rx3oI8pyKWOg3+QIHIRgL3pc8HLdZ2BkX4Vf6ANb4+noQnD/di1Mj+0pUL8RhIJE"

publicKey = CodeUtil.b64decode(publicKeyBase64)
privateKey = CodeUtil.b64decode(privateKeyBase64)
jsonKey1 = "data"
jsonKey2 = "key"
log = None

def hook_request_to_burp(request: Request) -> Request:
    """HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

    Args:
        request (Request): 请求对象

    Returns:
        Request: 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取需要解密的数据
    encryptedData: bytes = get_data(request.getContent())
    # 获取用来解密的密钥，该密钥已使用publicKey进行rsa加密
    encryptedKey: bytes = get_key(request.getContent())
    # 调用内置函数解密，拿到aes密钥
    key: bytes = asymmetric_decrypt(encryptedKey, privateKey)
    aesSecret.set(key)
    # 调用内置函数解密报文
    data: bytes = symmetric_decrypt(encryptedData, key)
    # 更新body为已解密的数据
    request.setContent(data)
    return request


def hook_request_to_server(request: Request) -> Request:
    """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

    Args:
        request (Request): 请求对象

    Returns:
        Request: 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取被解密的数据
    data: bytes = request.getContent()
    # 调用内置函数加密回去，这里使用设置的aesSecret进行加密
    encryptedData: bytes = symmetric_encrypt(data, aesSecret.get())
    # 调用内置函数加密aesSecret
    encryptedKey: bytes = asymmetric_encrypt(aesSecret.get(), publicKey)
    # 将已加密的数据转换为Server可识别的格式
    body: bytes = to_data(encryptedData, encryptedKey)
    # 更新body
    request.setContent(body)
    return request


def hook_response_to_burp(response: Response) -> Response:
    """HTTP响应从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

    Args:
        response (Response): 响应对象

    Returns:
        Response: 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取需要解密的数据
    encryptedData: bytes = get_data(response.getContent())
    # 调用内置函数解密
    data: bytes = symmetric_decrypt(encryptedData, aesSecret.get())
    # 更新body
    response.setContent(data)
    return response

def hook_response_to_client(response: Response) -> Response:
    """HTTP响应从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

    Args:
        response (Response): 响应对象

    Returns:
        Response: 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
    """
    # 获取被解密的数据
    data: bytes = response.getContent()
    # 调用内置函数加密回去
    encryptedData: bytes = symmetric_encrypt(data, aesSecret.get())
    # 更新body
    # 将已加密的数据转换为Server可识别的格式
    body: bytes = to_data(encryptedData, None)
    # 更新body
    response.setContent(body)
    return response

def asymmetric_decrypt(content: bytes, secret: bytes) -> bytes:
    return CryptoUtil.rsaDecrypt(ASYMMETRIC_ALGORITHM, content, secret)

def asymmetric_encrypt(content: bytes, secret: bytes) -> bytes:
    return CryptoUtil.rsaEncrypt(ASYMMETRIC_ALGORITHM, content, secret)

def symmetric_decrypt(content: bytes, secret: bytes) -> bytes:
    return CryptoUtil.aesDecrypt(SYMMETRIC_ALGORITHM, content, secret, None)

def symmetric_encrypt(content: bytes, secret: bytes) -> bytes:
    return CryptoUtil.aesEncrypt(SYMMETRIC_ALGORITHM, content, secret, None)

def get_data(content: bytes) -> bytes:
    return CodeUtil.b64decode(json.loads(convert_bytes(content))[jsonKey1])

def get_key(content: bytes) -> bytes:
    return CodeUtil.b64decode(json.loads(convert_bytes(content))[jsonKey2])


def to_data(content: bytes, secret: bytes) -> bytes:
    jsonBody = {}
    jsonBody[jsonKey1] = CodeUtil.b64encodeToString(content)
    if secret is not None:
        jsonBody[jsonKey2] = CodeUtil.b64encodeToString(secret)
    return json.dumps(jsonBody).encode()


def convert_bytes(java_byte_array: bytes) -> bytes:
    """将java的字节数组转为graalpy的字节数组, java的字节数组对应到graalpy中的类型是foreign对象, 如果想要用graalpy处理java的字节数组，最好先调用该函数"""
    return bytes([Byte.toUnsignedInt(b) for b in java_byte_array])


def set_log(log1):
    """程序在最开始会自动调用该函数，在上方函数可以放心使用log对象"""
    global log
    log = log1
    import sys
    log.info("python version: {}", sys.version)
