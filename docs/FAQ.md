# 请先阅读下方注意事项

- 项目采用Burp `Montoya API` 开发，Burp版本不低于 `v2023.10.3.7` 。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 项目使用JDK 17进行开发及编译，请确保启动Burp的JDK不低于17。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 自行构建：`build.gradle -> shadowJar`
- [Release](https://github.com/outlaws-bai/Galaxy/releases)中对应版本的注意事项

# hook方式显示不全

- 缺少jython：请在Burp的Java environment(`Settings -> Extensions`)配置一个文件夹，并将 `jython-standalone-xxx.jar` 放在该文件夹。[Download](https://www.jython.org/download)
- 缺少java：请确保启动Burp的是JDK，而不是JRE。[Modify](https://github.com/outlaws-bai/Galaxy/blob/main/docs/ToJDK.md)

# 本地能运行的代码在插件中不能运行

**python**：使用 jython/graalpy 支持 python 代码的运行，和你本地环境有差异或存在兼容性问题

**js**：使用  graaljs 支持 js 代码的运行，不等同于 node 环境，部分 js 语法或三方库不支持

> 如果有必须使用本地环境的需求，请使用 grpc/http 的方案，它们会提供更具兼容性的跨语言方案

# jython/graalpy hooker 怎样载入三方包

> python 的 hook 方式来源于 jython/graalpy 的跨语言支持，因此如果需要使用三方包，该三方包必须能通过 jython/graalpy 解释及运行，而 jython 只支持到 python 2.7，graalpy 支持 python3.11。但同时他们都有兼容性问题，并非所有三方包都可以导入

安装对应的 python 版本，创建虚拟环境，再用 pip 安装必要的三方包，在代码编辑器中将三方包所在的文件夹配置到 sys path，如 xxx/venv/lib/python2.7/site-packages

```python
import sys
sys.path.append("xxx/venv/lib/python2.7/site-packages")
````

# js hooker 怎样载入三方包

例如 `crypto-js`

1. 保存 https://cdn.jsdelivr.net/npm/crypto-js@4.1.1/crypto-js.js 为 crypto-js.js文件
2. 在 js 引擎中执行 `load('xxx/crypto-js.js');` 即可加载该 js 文件，之后就可以使用其中的函数了

```js
// 加载
load('xxx/crypto-js.js');              
// 测试 crypto-js 是否可以正常工作
var hash = CryptoJS.MD5("Message");
console.log(hash.toString()); // 4c2a8fe7eaf24721cc7a9f0175115bd4       
```
