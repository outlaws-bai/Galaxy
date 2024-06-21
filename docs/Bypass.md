# Bypass

Bypass相关的功能

## Bypass Url

该功能位于Burp Suite Intruder模块，是一个Payload生成器

### 场景

当你发现了一个可能存在SSRF或URL跳转的点，但是服务端对host做了校验，可能是startsWith/endsWith等等。

### 实现

程序会先获取当前请求的URL和期望攻击的URL对象，然后通过内置模板(可自定义)进行渲染，之后将结果作用于测试点

例如, 内置模板中有如下一条，程序会经过如下处理后将其作用于测试点

```java
String template = "${originUrl.getProtocol()}://${originUrl.getHost()}@${evilUrl.getHost()}";
URL originUrl = new URL("https://baidu.com");
URL evilUrl = new URL("https://evil.com");
Map<String, Object> env = new HashMap<>();
env.put("originUrl", originUrl);
env.put("evilUrl", evilUrl);
String res = Render.renderTemplate(template, env); // https://baidu.com@evil.com
```

> 如果你需要自定义模板，可以在 `user.home/.galaxy/dcit/bypassUrlDict.txt`中增加自己的模板

### 使用

例如，你在 https://www.baidu.com/jump?url=xxx 发现一个测试点，想要尝试跳转到https://evil.com

1. 在Reapter模块，修改xxx为https://evil.com，选中其右键发送到Intruder
2. 转到Intruder，修改Payload Type: Extension-generated；Select generator: BypassUrl

### 效果

![image-20240621153035597](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621153035597.png)

![image-20240621153045475](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621153045475.png)

![image-20240621153129925](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621153129925.png)

## Bypass Path

该功能位于Burp Suite Intruder模块，是一个Payload生成器

### 场景

通过接口文档或其它方式发现了某个存在漏洞的接口，但WAF或程序的鉴权规则禁止对该接口的访问，想要Bypass

### 实现

> 利用WAF或程序鉴权规则与程序路由匹配规则的逻辑不统一，原理参考自 [浅谈 URL 解析与鉴权中的陷阱](https://tttang.com/archive/1899/)

获取到请求的path后，通过调用 [该函数](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/modules/bypass/BypassTools.java#L14) 生成一系列绕过的payload

例如，当path为 `/api/user/admin/listUsers`，会自动生成不同的绕过path

```java
String path = "/api/user/admin/listUsers";
BypassTools.generateBypassPathPayloads(path).forEach(System.out::println);
/*
 * /api/user/admin/listUsers;
 * /api/user/admin/listUsers/
 * /;/api/;/user/;/admin/;/listUsers
 * /%61%70%69/%75%73%65%72/%61%64%6D%69%6E/%6C%69%73%74%55%73%65%72%73
 * ...
 * */
```

### 使用

例如，你想要对 https://www.baidu.com/user/admin/listUsers 进行Bypass Path

1. 选中 `/user/admin/listUsers` 右键发送到Intruder
2. 转到Intruder，修改Payload Type: Extension-generated；Select generator: BypassPath

### 效果

![image-20240621153316844](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621153316844.png)

![image-20240621153329296](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621153329296.png)

![image-20240621153354123](https://raw.githubusercontent.com/outlaws-bai/picture/main/img/image-20240621153354123.png)

## Bypass IP

该功能位于Burp Suite Repeater模块，是一个按钮。右键后可以逐级找到

点击后增加常见Hop By Hop传输IP的请求头, 值默认为127.0.0.1, 可以通过配置文件修改

```yaml
bypassConfig: # Bypass 模块的功能配置
  bypassIPDefaultValue: 127.0.0.1 # Bypass IP中的IP值
```

