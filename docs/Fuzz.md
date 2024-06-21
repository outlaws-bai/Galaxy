# Fuzz

Fuzz相关的功能

## Fuzz Sensitive Path

该功能位于Burp Suite Intruder模块，是一个Payload生成器。

### 场景

当你发现某个Path `/api/user/getUserInfo`，想要分别对`/`，`/api/`，`/api/user/`，进行目录扫描，如果你使用常规的目录扫描器，需要运行三次

### 实现

获取Path中每一层的目录，并且追加上内置的字典(可自定义)，作为最终的测试路径。

以`/api/user/getUserInfo`接口为例，假设我们想要扫描`/actuator`，通过本插件处理后会生成如下接口

```
/actuator
/api/actuator
/api/user/actuator
```

> 如果你需要自定义模板，可以在 `user.home/.galaxy/dcit/fuzzSensitivePathDict.txt`中增加自己的扫描路径

### 使用

例如，你想要扫描在 https://www.baidu.com/api/user/getUserInfo 

1. 选中/api/user/getUserInfo 右键发送到Intruder
2. 转到Intruder，修改Payload Type: Extension-generated；Select generator: FuzzSensitivePath

### 效果

![image-20240621155107548](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621155107548.png)

![image-20240621155115754](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621155115754.png)

![image-20240621155125684](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621155125684.png)

## Fuzz Swagger Docs

该功能是一个右键按钮，在任意一个响应编辑器中选中右键找到后点击即可

### 场景

当你发现了swagger或openapi的接口文档泄露，由于接口众多，需要一个插件解析接口文档，代入参数，发送请求

### 实现

解析Swagger接口文档，生成每个接口的测试请求、自动化解析参数代入、发送请求

### 使用

在任意模块的响应编辑器中，右键找到名为 `FuzzSwaggerDocs` 的按钮点击，即可生成请求并发送，将所有的流量转发到Burp的Organizer模块

> 如果你担心批量请求可能造成业务问题，可以在配置中关闭自动发送请求，这样只会将生成后的请求发送到Organizer，再根据自身需求修改后测试。

```yaml
fuzzConfig: # Fuzz 模块的功能配置
  swaggerGeneratedRequestAutoSend: false # 通过FuzzSwaggerDocs生成的请求是否发送给服务端
```

## Extract FuzzDict

该功能是一个右键按钮，在Target -> Site map -> 选中一个域名 -> Contents -> ctrl + A选中所有历史请求，即可右键找到该按钮

**场景**

当发现某个可能存在问题的接口，但无法获取入参，需要通过fuzz的方式测试有哪些入参，但由于不同公司不同业务可能有不同的命名习惯，需要手动总结fuzz的参数列表，该功能可以将所有位置的参数、path等总结为字典存储到文件

**实现**

分析多个流量，获取请求&响应中的header名称、cookie键值、参数名称(包括query、form、json的每一级)、路径、action(path的最后一段)
创建`FuzzDict`对象并输出至`user.home/.galaxy/dcit/fuzzSensitivePathDict.txt`文件夹下

