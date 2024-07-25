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

1. Burp Extensions，Once the plugin is selected, you can see that Burp has limited the number of displayed lines.
2. In the work directory, there is a file named run.log, which contains all the logs.
