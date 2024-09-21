var CodeUtil = Java.type("org.m2sec.core.utils.CodeUtil")
var CryptoUtil = Java.type("org.m2sec.core.utils.CryptoUtil")
var HashUtil = Java.type("org.m2sec.core.utils.HashUtil")
var JsonUtil = Java.type("org.m2sec.core.utils.JsonUtil")
var MacUtil = Java.type("org.m2sec.core.utils.MacUtil")
var FactorUtil = Java.type("org.m2sec.core.utils.FactorUtil")
var Request = Java.type("org.m2sec.core.models.Request")
var Response = Java.type("org.m2sec.core.models.Response")

SYMMETRIC_ALGORITHM = "SM4/ECB/PKCS5Padding"
sm4Secret = stringToByteArray("16byteslongkey12")

ASYMMETRIC_ALGORITHM = "SM2"
SM2_MODE = "c1c2c3"
publicKey1Base64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEBv9Z+xbmSOH3W/V9UEpU1yUiJKNGh/I8EiENTPYxX3GujsZyKhuEUzxloKCATcNaKWi7w/yK3PxGONM4xvMlIQ=="
privateKey1Base64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgWmIprZ5a6TsqRUgy32J+F22AYIKl+14P4qlw/LPPCcagCgYIKoEcz1UBgi2hRANCAAQG/1n7FuZI4fdb9X1QSlTXJSIko0aH8jwSIQ1M9jFfca6OxnIqG4RTPGWgoIBNw1opaLvD/Irc/EY40zjG8yUh"

publicKey2Base64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE/1kmIjlOfsqG9hN4b/O3hiSI91ErgVDeqB9YOgCFiUiFyPo32pCHh691zGnoAj0l/P132CyLgBeH6TUa/TrLUg=="
privateKey2Base64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgP8vW9tEh0dMP5gJNsol5Gyc6jvvgK1NRqOVg8VaLYVygCgYIKoEcz1UBgi2hRANCAAT/WSYiOU5+yob2E3hv87eGJIj3USuBUN6oH1g6AIWJSIXI+jfakIeHr3XMaegCPSX8/XfYLIuAF4fpNRr9OstS"

publicKey1 = CodeUtil.b64decode(publicKey1Base64)
privateKey1 = CodeUtil.b64decode(privateKey1Base64)

publicKey2 = CodeUtil.b64decode(publicKey2Base64)
privateKey2 = CodeUtil.b64decode(privateKey2Base64)
jsonKey1 = "data"
jsonKey2 = "key"
log = void 0

/**
 * 内置模版，需要自定义代码文件时查看该文档：https://github.com/outlaws-bai/Galaxy/blob/main/docs/Custom.md
 * 按 Ctrl（command） + ` 可查看内置函数
 */

/**
 * HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
 * @param {Request} request 请求对象
 * @returns 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_request_to_burp(request) {
    // 获取需要解密的数据
    encryptedData = get_data(request.getContent());
    // 获取用来解密的密钥，该密钥已使用publicKey1进行sm2加密
    encryptedKey = get_key(request.getContent())
    // 调用内置函数解密，拿到sm4密钥
    key = asymmetric_decrypt(encryptedKey, privateKey1)
    // 调用内置函数解密报文
    data = symmetric_decrypt(encryptedData, key)
    // 更新body为已解密的数据
    request.setContent(data);
    return request;
}


/**
 * HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
 * @param {Request} request 请求对象
 * @returns 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_request_to_server(request) {
    // 获取被解密的数据
    data = request.getContent();
    // 调用内置函数加密回去，这里使用设置的sm4Secret进行加密
    encryptedData = symmetric_encrypt(data, sm4Secret)
    // 调用内置函数加密sm4Secret
    encryptedKey = asymmetric_encrypt(sm4Secret, publicKey1)
    // 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData, encryptedKey)
    // 更新body
    request.setContent(body);
    return request;
}


/**
 * HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
 * @param {Response} response 响应对象
 * @returns 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_response_to_burp(response) {
    // 获取需要解密的数据
    encryptedData = get_data(response.getContent());
    // 获取用来解密的密钥，该密钥已使用publicKey2进行sm2加密
    encryptedKey = get_key(response.getContent())
    // 调用内置函数解密，拿到sm4密钥
    key = asymmetric_decrypt(encryptedKey, privateKey2)
    // 调用内置函数解密报文
    data = symmetric_decrypt(encryptedData, key)
    // 更新body
    response.setContent(data);
    return response;
}


/**
 * HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。
 * @param {Response} response 响应对象
 * @returns 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_response_to_client(response) {
    // 获取被解密的数据
    data = response.getContent();
    // 调用内置函数加密回去，这里使用设置的sm4Secret进行加密
    encryptedData = symmetric_encrypt(data, sm4Secret)
    // 调用内置函数加密sm4Secret
    encryptedKey = asymmetric_encrypt(sm4Secret, publicKey2)
    // 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData, encryptedKey)
    // 更新body
    response.setContent(body);
    return response;
}

function asymmetric_decrypt(content, secret) {
    return CryptoUtil.sm2Decrypt(SM2_MODE, content, secret);
}

function asymmetric_encrypt(content, secret) {
    return CryptoUtil.sm2Encrypt(SM2_MODE, content, secret);
}

function symmetric_decrypt(content, secret) {
    return CryptoUtil.sm4Decrypt(SYMMETRIC_ALGORITHM, content, secret, null);
}

function symmetric_encrypt(content, secret) {
    return CryptoUtil.sm4Encrypt(SYMMETRIC_ALGORITHM, content, secret, null);
}

function get_data(content) {
    return CodeUtil.b64decode(JsonUtil.jsonStrToMap(byteArrayToString(content)).get(jsonKey1))
}

function get_key(content) {
    return CodeUtil.b64decode(JsonUtil.jsonStrToMap(byteArrayToString(content)).get(jsonKey2))
}


function to_data(content, secret) {
    jsonBody = {}
    jsonBody[jsonKey1] = CodeUtil.b64encodeToString(content)
    jsonBody[jsonKey2] = CodeUtil.b64encodeToString(secret)
    return stringToByteArray(JsonUtil.toJsonStr(jsonBody))
}

/**
 * 程序在最开始会自动调用该函数，在上方函数可以放心使用log对象
 */
function set_log(log1) {
    log = log1
}

function stringToByteArray(str) {
    let byteArray = new Uint8Array(str.length);
    for (let i = 0; i < str.length; i++) {
        byteArray[i] = str.charCodeAt(i);
    }
    return byteArray;
}

function byteArrayToString(byteArray) {
    return String.fromCharCode.apply(null, byteArray);
}