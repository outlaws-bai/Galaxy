# Basic

## Work Dir

Linux/Mac: ~/.galaxy

Windows: %USERPROFILE%/.galaxy

## Expression

Using `nashorn` to execute expressions is essentially running a single line of JavaScript code.

## Template

The content in ${xxx} will be treated as an `Expression` and the result of the execution will replace the template through `common-text`.

> When using the $ character in Template, double escape is required.

## Log

All logs will be displayed in two places:

1. `Burp -> Extensions -> Galaxy -> Output/Errors`
2. [WorkDir](https://github.com/outlaws-bai/Galaxy/blob/main/docs/Basic_EN.md#work-dir) / run.log
