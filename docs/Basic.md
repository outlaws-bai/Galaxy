# Basic

## Work Dir

Linux/Mac: ~/.galaxy

Windows: %USERPROFILE%/.galaxy

## Expression

使用 `nashorn` 实现表达式的执行，可以理解就是一行JavaScript代码。

## Template

${xxx} 中的内容会被当作 `Expression` 执行，执行结果通过 `common-text` 替换模板。

> 在Template要使用$字符时，需要双写转义。

## Log

所有日志会显示在两个地方：

1. Burp Extensions，选中插件可以看到，不过这里Burp限制了显示行数
2. work dir下有run.log文件，包含了所有日志
