from org.m2sec.core.dynamic import ICodeHooker
from org.m2sec.core.utils import *
from org.m2sec.core.models import *
from org.slf4j import Logger
from java.util import HashMap


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


class Hooker(ICodeHooker):
    def __init__(self, log):
        self.log = log

    def hookRequestToBurp(self, request):
        """HTTP请求从客户端到达Burp时被调用。在此处完成请求解密的代码就可以在Burp中看到明文的请求报文。

        Args:
            request (_type_): 请求对象, https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java

        Returns:
            _type_: 经过处理后的request对象，返回null代表不需要处理
        """
        # 获取需要解密的数据
        encryptedData = self.get_data(request.getContent())
        # 调用函数解密
        data = self.decrypt(encryptedData)
        # 更新body为已加密的数据
        request.content = data
        return request

    def hookRequestToServer(self, request):
        """HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。

        Args:
            request (Request): 请求对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Request.java

        Returns:
            _type_: 经过处理后的request对象，返回null代表不需要处理
        """
        # 获取被解密的数据
        data = request.content
        # 调用函数加密回去
        encryptedData = self.encrypt(data)
        # 将已加密的数据转换为Server可识别的格式
        body = self.to_data(encryptedData)
        # 更新body
        request.content = body
        return request

    def hookResponseToBurp(self, response):
        """HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。

        Args:
            response (Request): 响应对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java

        Returns:
            _type_: 经过处理后的response对象，返回null代表不需要处理
        """
        # 获取需要解密的数据
        encryptedData = self.get_data(response.getContent())
        # 调用函数解密
        data = self.decrypt(encryptedData)
        # 更新body
        response.content = data
        return response

    def hookResponseToClient(self, response):
        """HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。

        Args:
            response (Response): 响应对象，https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/core/models/Response.java

        Returns:
            _type_: 经过处理后的response对象，返回null代表不需要处理
        """
        # 获取被解密的数据
        data = response.getContent()
        # 调用函数加密回去
        encryptedData = self.decrypt(data)
        # 更新body
        # 将已加密的数据转换为Server可识别的格式
        body = self.to_data(encryptedData)
        # 更新body
        response.content = body
        return response

    def decrypt(self, content):
        """解密函数

        Args:
            content (Response): 要解密的数据

        Returns:
            _type_: 解密结果
        """
        return CryptoUtil.aesDecrypt(ALGORITHM, content, secret, paramMap)

    def encrypt(self, content):
        """加密函数

        Args:
            content (byte[]): 要加密的数据

        Returns:
            _type_: 加密结果
        """
        return CryptoUtil.aesEncrypt(ALGORITHM, content, secret, paramMap)

    def get_data(self, content):
        return CodeUtil.b64decode(
            JsonUtil.jsonStrToMap(content.tostring()).get(jsonKey)
        )

    def to_data(self, content):
        jsonBody = {}
        jsonBody[jsonKey] = CodeUtil.b64encodeToString(content)
        temp = JsonUtil.toJsonStr(jsonBody)
        return temp.encode()
