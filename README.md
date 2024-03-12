# Galaxy

Burp Suite Extension

# 功能点梳理

标题带*为待实现

## 1. Http Hook

应对部分流量响应加解密/加签的情况，使得用户在Burp中可以查看、修改明文请求/响应

### 1.1. Rpc

使用Rpc功能处理HttpHook需求

## 2. Bypass

提供绕过相关的功能

### 2.1. Bypass IP

增加常见传输IP的请求头, 默认为127.0.0.1

Repeater选中Request编辑器右键

### 2.2. Bypass Path

绕过某些路径被WAF拦截或权限不足，可以通过路径穿越等能力绕过的情况

在任意Request编辑器选中path右键发送至Intruder, payload-type选择Extension-generated, selected-generator选中Bypass Path

### 2.3. Bypass Url

绕过ssrf或任意重定向对host未验证或验证不足;

在Repeater的Request编辑器向需要测试的参数插入任意跳转或ssrf目标的url(如: ?target=https://www.baidu.com), 此时选中 https://www.baidu.com , 右键发送至Intruder; payload-type选择Extension-generated, selected-generator选中Bypass Url即可

## 3. Fuzz

提供与Fuzz相关的功能

### 3.1. Fuzz Sensitive Path

扫描敏感路径

a. 在任意Request编辑器选中path或某段, 右键发送至Intruder；payload-type选择Extension-generated,  selected-generator选中Fuzz Sensitive Path

b. 在Target标签页选中某个host, 在中间上侧Contents选中多条请求响应时右键选中该按钮, 会扫描所有请求所有子路径的敏感目录, 并将结果发送至Organizer

### 3.2. Fuzz Sensitive Path And Bypass

在Fuzz Sensitive Path的基础上增加Bypass, 3.1中a b两处均含有该增强版功能

### 3.3. Fuzz Swagger Docs

在任意Response编辑器右键选择该按钮, 会自动解析swagger文档并填充参数(复杂参数不解析), 发送请求后将结果发送至Organizer

### 3.4. Extract FuzzDict

在Target标签页选中某个host, 在中间上侧Contents选中多条请求响应时右键选中该按钮, 会分析所有的请求和响应, 将请求响应中的header key、cookie key、param key(query、form、json均支持)、path、action(path的最后一段) 梳理为FuzzDict对象并输出至Burp安装路径的.galaxy/fuzzDicts目录下

## 4. Cloud

提供云资源配置检查功能

### 4.1. Cloud Singer(*)

云资源一般使用签名作为认证, 该功能可以利用配置中的对应AK、SK对当前请求进行签名

### 4.2. S3 Checker(*)

利用配置中的AK、SK对S3的配置进行检查

## 5. Mixed

提供一些混杂的功能

### 5.1. Json To Query

将json参数转为query参数, 深层json对象会直接toString

### 5.2. Query To Json

将query参数转为json, 不支持数组参数

### 5.3. Message To SqlMap

在配置中增加了sqlmap的路径及默认参数的情况下; 在Repeater的Request编辑器在需要测试的位置输入*, 然后右键选中该按妞,
会创建临时文件(在Burp退出时自动删除)并调用sqlmap

### 5.4. Url To Repeater

在任意Request or Response编辑器选中Url, 会将Url转为请求报文并发送至Repeater(中文Burp转为Message存在一定的问题, 会有乱码出现)

## 6. RuleMatch(*)

在匹配到Request、Response符合某些规则的情况下, 设置颜色及提示

# 参考文档

javadoc

https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html

examples

https://github.com/PortSwigger/burp-extensions-montoya-api-examples

Aviator

https://www.yuque.com/boyan-avfmj/aviatorscript