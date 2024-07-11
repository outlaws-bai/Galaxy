# Other Ability

其他小功能

> 下方带*为待实现。

## Parse Swagger Api Doc *

该功能会自动解析并生成所有接口的请求报文，将其发送到`Organizer`，可以对参数进行修改测试。

> 如果想要自动将生成的请求发送到服务端，可以在Galaxy -> Settings窗口中勾选。

**使用方法**：

在任意响应报文Editor右键，选择"Parse Swagger Doc"即可。

## Bypass Host Check *

绕过服务端在csrf/ssrf的测试点对host做了验证

> 如果想要自定义绕过Template，可以修改 `${Work Dir}/templates/bypassHostCheckTemplate.txt`文件。[Work Dir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Work-Dir), [Template](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#Tempalte)

**使用方法**：

在测试点输入要攻击的URL，选中它右键`Send To Intruder`, 然后在`Intruder` 中选择`Payload type -> Extension-generated`, `Selected generator -> Bypass Host Check`，之后点击`Start attack`即可。

## Bypass Auth Of Path *

通过修改Path的方式绕过某些认证/鉴权/拦截

**使用方法**：

以`/`为开始选中需要绕过的path(可以是整体或部分), 然后右键`Send To Intruder`。然后在`Intruder` 中选择`Payload type -> Extension-generated`, `Selected generator -> Bypass Auth Of Path`，之后点击`Start attack`即可。
