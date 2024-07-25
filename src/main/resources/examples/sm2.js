var CodeUtil = Java.type("org.m2sec.core.utils.CodeUtil")
var CryptoUtil = Java.type("org.m2sec.core.utils.CryptoUtil")
var HashUtil = Java.type("org.m2sec.core.utils.HashUtil")
var JsonUtil = Java.type("org.m2sec.core.utils.JsonUtil")
var MacUtil = Java.type("org.m2sec.core.utils.MacUtil")
var FactorUtil = Java.type("org.m2sec.core.utils.FactorUtil")
var Request = Java.type("org.m2sec.core.models.Request")
var Response = Java.type("org.m2sec.core.models.Response")
var String = Java.type("java.lang.String")

ALGORITHM = "SM2"
publicKey1Base64 = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEBv9Z+xbmSOH3W/V9UEpU1yUiJKNGh/I8EiENTPYxX3GujsZyKhuEUzxloKCATcNaKWi7w/yK3PxGONM4xvMlIQ==";
privateKey1Base64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgWmIprZ5a6TsqRUgy32J+F22AYIKl+14P4qlw/LPPCcagCgYIKoEcz1UBgi2hRANCAAQG/1n7FuZI4fdb9X1QSlTXJSIko0aH8jwSIQ1M9jFfca6OxnIqG4RTPGWgoIBNw1opaLvD/Irc/EY40zjG8yUh";

publicKey2Base64="MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE/1kmIjlOfsqG9hN4b/O3hiSI91ErgVDeqB9YOgCFiUiFyPo32pCHh691zGnoAj0l/P132CyLgBeH6TUa/TrLUg==";
privateKey2Base64 =
    "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgP8vW9tEh0dMP5gJNsol5Gyc6jvvgK1NRqOVg8VaLYVygCgYIKoEcz1UBgi2hRANCAAT/WSYiOU5+yob2E3hv87eGJIj3USuBUN6oH1g6AIWJSIXI+jfakIeHr3XMaegCPSX8/XfYLIuAF4fpNRr9OstS";

publicKey1 = CodeUtil.b64decode(publicKey1Base64);
privateKey1 = CodeUtil.b64decode(privateKey1Base64);

publicKey2 = CodeUtil.b64decode(publicKey2Base64);
privateKey2 = CodeUtil.b64decode(privateKey2Base64);
jsonKey = "data"
log = void 0

/**
 * 内置模版，需要自定义代码文件时查看该文档：https://github.com/outlaws-bai/Galaxy/blob/main/docs/Custom.md
 * 按 Tab 可查看内置函数
 */

/**
 * HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
 * @param {Request} request 请求对象
 * @returns 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_request_to_burp(request){
    // 获取需要解密的数据
    encryptedData = get_data(request.getContent());
    // 调用内置函数解密
    data = decrypt(encryptedData, privateKey1);
    // 更新body为已加密的数据
    request.setContent(data);
    return request;
}


/**
 * HTTP请求从Burp将要发送到Server时被调用。在此处完成请求加密的代码就可以将加密后的请求报文发送到Server。
 * @param {Request} request 请求对象
 * @returns 经过处理后的request对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_request_to_server(request){
    // 获取被解密的数据
    data = request.getContent();
    // 调用内置函数加密回去
    encryptedData = encrypt(data, publicKey1);
    // 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData);
    // 更新body
    request.setContent(body);
    return request;
}


/**
 * HTTP请求从Server到达Burp时被调用。在此处完成响应解密的代码就可以在Burp中看到明文的响应报文。
 * @param {Response} response 响应对象
 * @returns 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_response_to_burp(response){
    // 获取需要解密的数据
    encryptedData = get_data(response.getContent());
    // 调用内置函数解密
    data = decrypt(encryptedData, privateKey2);
    // 更新body
    response.setContent(data);
    return response;
}


/**
 * HTTP请求从Burp将要发送到Client时被调用。在此处完成响应加密的代码就可以将加密后的响应报文返回给Client。
 * @param {Response} response 响应对象
 * @returns 经过处理后的response对象，返回null代表从当前节点开始流量不再需要处理
 */
function hook_response_to_client(response){
    // 获取被解密的数据
    adata = response.getContent();
    // 调用内置函数加密回去
    encryptedData = encrypt(data, publicKey2);
    // 更新body
    // 将已加密的数据转换为Server可识别的格式
    body = to_data(encryptedData);
    // 更新body
    response.setContent(body);
    return response;
}

function decrypt(content, secret) {
    return CryptoUtil.sm2Decrypt(content, secret);
}

function encrypt(content, secret) {
    return CryptoUtil.sm2Encrypt(content, secret);
}

function get_data(content){
    return CodeUtil.b64decode(JsonUtil.jsonStrToMap(new String(content)).get(jsonKey))
}


function to_data(content){
    jsonBody = {}
    jsonBody[jsonKey] = CodeUtil.b64encodeToString(content)
    return JsonUtil.toJsonStr(jsonBody).getBytes()
}

/**
 * 程序在最开始会自动调用该函数，在上方函数可以放心使用log对象
 */
function set_log(log1){
    log = log1
}