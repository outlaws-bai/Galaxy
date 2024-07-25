# Http Hook

## Design concept:

https://xz.aliyun.com/t/15051

## Basic Information:

**Option Explanation:**

`Hook Request`: Do you need to hook the request?。

`Hook Response`: Do you need to respond to the Hook?。

`Check Expression`:Determine if the request requires a hook JavaScript boolean expression:。[Expression](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Expression)

**Hook stage**：

`hookRequestToBurp`：When the HTTP request arrives at Burp from the client, it is called. The code for decrypting the request can be completed here, and the plaintext request message can be seen in Burp.
`hookRequestToServer`：The HTTP request is called when it is about to be sent from Burp to the Server. The code for encrypting the request should be completed here so that the encrypted request message can be sent to the Server.
`hookResponseToBurp`：When the HTTP request reaches Burp from the Server, it is called. The code for decrypting the response can be completed here, and the plaintext response message can be seen in Burp.
`hookResponseToClient`：The HTTP request is called when it is about to be sent from Burp to the Client. Complete the code for encrypting the response here to return the encrypted response message to the Client.

**Flowchart:**：

![流程图](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621105543574.png)

**TEST**：Right-click on `Http Hook` in `Repeater` and click on the button to test it. Make sure you have already started the Http Hook service TES.T

## Implementation method:

Support Grpc, Java, Python, and JavaScript to implement four stages.

Can be divided into two categories:

`Grpc` ：Call the corresponding "Hook interface" in the lifecycle of the HTTP message. You need to implement the Grpc Server in another language and use third-party libraries to implement the required functionality of the corresponding "Hook interface" on your own.

`Code` ：Call the corresponding `Hook function` at specific stages in the lifecycle of the HTTP message. You need to write a script in the supported language to combine and call DataObjects and Utils in the project, implementing the functionality that the corresponding `Hook function` should have.

**Comparison**:

`Grpc`：You need to implement a Grpc Server in another language. The advantage is strong cross-language capability and strong runtime compatibility. The disadvantage is slightly higher learning cost, dependency on IO -> may have performance issues, possible compatibility issues between different language algorithms, and inability to meet requirements in the case of dynamic keys.

`Code`：Advantages include interacting with JVM to call Java's native encryption and decryption libraries, which eliminates algorithm compatibility issues for Java and includes multiple examples in the project. The cost is low. The downside is the need to be familiar with the project's DataObjects and Utils, and potential runtime compatibility issues.

### Grpc

You need to write and start a GRPC server by yourself. Implement four Hook functions.

See GRPC proto  [HttpHook.proto](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/proto/HttpHook.proto)

Here are several Grpc Servers in different languages：
1. [java](https://github.com/outlaws-bai/Galaxy/blob/main/src/test/java/org/m2sec/core/httphook/HttpHookGrpcServer.java)
2. [python](https://github.com/outlaws-bai/PyGRpcServer)。

### Java

> You must use JDK to start Burp, as it requires dynamic compilation of .java files, which JRE does not satisfy.

You need to select a JAVA file and modify it as needed until it meets your requirements. The program will dynamically compile and call the Hook function in it.

example
1. [AesCbc](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/AesCbc.java)
2. [AesEcb](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/AesEcb.java)
3. [AesGcm](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/AesGcm.java)
4. [Rsa](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/Rsa.java)
5. [Sm2](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/Sm2.java)

### Python

You need to select a Python file and modify it as needed until it meets your requirements. The program will call the corresponding functions during different lifecycles of different HTTP messages.

example：
1. [aes_cbc](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/aes_cbc.py)

### JS

You need to select a Js file and modify it as needed until it meets your requirements, the program will call the corresponding function during the lifecycle of different HTTP messages.

example：
1. [aes_cbc](https://github1s.com/outlaws-bai/Galaxy/blob/main/src/main/resources/examples/aes_cbc.js)

