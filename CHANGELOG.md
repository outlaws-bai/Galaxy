# Release History

## Under release

1. 右键加解密失败时改为抛出异常

## 2024-08-04 Release 2.2.9

1. 更新hook脚本代码编辑器中查看内置函数的快捷键为Ctrl（command） + `
2. httphook的右键增加响应的加解密Item，并且结果改为弹窗保证可以在所有报文编辑器中可以使用
3. sm2、rsa增加transformation入参，以支持可选模式，并且向前兼容
4. 内置加解密工具类增加tea、xtea、xxtea加解密函数
5. 代码编辑器中部分函数增加更详细的提示
6. 项目中引入HttpClient，保证有联动jsrpc、frida的能力
7. 内置示例中增加动态密钥示例，与靶场中同名的页面对应
8. 从该版本开始，提供两个jar包，差异为是否包含jython

## 2024-07-29 Release 2.2.8

1. 增加更多示例：des、3des、aes+rsa、sm2+sm4
2. 编辑器中代码提示增加headers.has、headers.hasIgnoreCase、des加解密、3des加解密等可用函数
3. 修复展示堆栈信息时，没有展示出message的问题
4. 安装时增加JDK启动检查
5. 修复选项为JS时，重新加载panel无法正确显示的问题

## 2024-07-26 Release 2.2.7

1. fix some bugs
2. 完成py、js的部分示例

## 2024-07-20 Release 2.2.6

1. fix some bugs
2. 代码编辑器增加提示/补全功能

## 2024-07-17 Release 2.2.5

1. Request/Response 增加getBody、getJson方法。@outlaws-bai
2. 更新examples中的描述。@outlaws-bai
3. 移除无用的grpc-services。@outlaws-bai
4. 增加自定义日志Appender，将日志自动输出到Burp的logging。@outlaws-bai

## 2024-07-17 Release 2.2.4

1. 修复path正常化处理遇到根目录失效的bug。@outlaws-bai
2. 修复js和py模板中的描述错误。@outlaws-bai

## 2024-07-14 Release 2.2.3

1. 修复已知BUG。@outlaws-bai
2. Setting增加打开工作目录按钮。@outlaws-bai
3. HTTP HOOK新增两个Test按钮。@outlaws-bai

## 2024-07-14 Release 2.2.2

1. fix bug。@outlaws-bai
2. 修改示例中的描述。@outlaws-bai

## 2024-07-14 Release 2.2.1

1. 通过导入jython的方式解决http hook -python 无法使用的问题。@outlaws-bai

## 2024-07-14 Release 2.2.0

1. http hook功能中新增语言支持：js、python 。@outlaws-bai
2. 修复已知bug 。@outlaws-bai

## 2024-07-13 Release 2.1.0

1. 通过java file实现http hook的方式下，修改对其的调用方式。并同步修改examples。@outlaws-bai
2. 当更新版本时，会自动将原有工作路径rename，以避免版本与其下文件不相符合问题。@outlaws-bai
3. 部分代码优化。@outlaws-bai

## 2024-07-12 Release 2.0.0

1. 表达式实现方式更改为使用nashorn执行JS代码完成。@outlaws-bai
2. 增加HttpHook的UI界面。@outlaws-bai

## 2024-07-08 Release 1.x

废弃。
