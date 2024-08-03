# Http Hook

Automatically decrypt in the scenario of double encryption in HTTP messages. [Design concept](https://xz.aliyun.com/t/15051).

## Introduction

The idea of this project is to hand over the request/response object to you. You only need to retrieve the encrypted data from the request/response, call the decryption function provided by the project, and modify the request/response.

> We need some programming foundation, and the project has built-in hook scripts for various encryption scenarios, which can be used as a reference

## Flowchart:

![流程图](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621105543574.png)

`hookRequestToBurp`: Function/interface called when an HTTP request reaches Burp from the client. Code for decrypting the request can be completed here to view the plaintext request message in Burp.

`hookRequestToServer`: Function/interface called when an HTTP request is about to be sent from Burp to the server. Code for encrypting the request can be completed here to send the encrypted request message to the server.

`hookResponseToBurp`: Function/interface called when an HTTP response reaches Burp from the server. Code for decrypting the response can be completed here to view the plaintext response message in Burp.

`hookResponseToClient`: Function/interface called when an HTTP response is about to be sent from Burp to the client. Code for encrypting the response can be completed here to return the encrypted response message to the client.

## Interface introduction:

After installation, you will see a page like this:

![image-20240730215219927](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240730215219927.png)

`Hooker`: [Implementation method](https://github.com/outlaws-bai/Galaxy/blob/main/docs/HttpHook.md#%E5%AE%9E%E7%8E%B0%E6%96%B9%E5%BC%8F), optional js, python, java, grpc.

`Hook Request`: Switch, used to determine whether the request needs to be hooked.

`Hook Response`: Switch, used to determine whether the response needs to be hooked.

`Expression`: js bool [Expression](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic_EN.md#Expression), used to determine whether the request needs to be hooked.

## Implementation

Support four Hooks to be implemented in four ways: grpc, java, python, and js.

These four can be divided into two categories: grpc (grpc), and code (java, python, js).

`grpc`: You can implement the grpc server in any language and implement the four Hooks within it. Here, they are four interfaces, and you need to implement their functionality using third-party libraries.

`code`: You can implement script files in the corresponding language and implement the four Hooks within them. Here, they are four functions, and you need to `find the encrypted/decrypted data in the request/response` -> `call the encryption/decryption code in the project` -> `modify the request/response object` in order to implement their functionality.

## Test

After starting the HTTP Hook service, right-click in any HTTP request/response editor to find the corresponding button.

## Logs

Running logs will be sent to two places:

1. `Burp -> Extensions -> Galaxy -> Output/Errors`
2. [WorkDir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic_EN.md#work-dir) / run.log

## Example

**grpc**

[java](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/core/httphook/HttpHookGrpcServer.java)

[python](https://github.com/outlaws-bai/PyGRpcServer)

**code**

https://github.com/outlaws-bai/Galaxy/tree/main/src/main/resources/examples

