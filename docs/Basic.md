# Basic

## Work Dir

Linux/Mac: ~/.galaxy

Windows: %USERPROFILE%/.galaxy

## Expression

使用 [mvel](https://github.com/mvel/mvel) 实现表达式的执行，可以理解就是一行代码。语法参考

## Template

${xxx} 中的内容会被当作 `Expression` 执行，执行结果通过 `common-text` 替换模板。

> 在Template要使用$字符时，需要双写转义。

## Log

运行中的日志会发送到两个地方：

1. `Burp -> Extensions -> Galaxy -> Output/Errors` （仅显示部分）
2. [WorkDir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic.md#work-dir) / run.log
