**请先阅读下方注意事项**

- 项目采用Burp `Montoya API` 开发，Burp版本不低于 `v2023.10.3.7` 。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 项目使用JDK 17进行开发及编译，请确保启动Burp的JDK不低于17。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 如果你下载或打包后的jar包含 `without-jython` 字样，请在Burp的Java environment(`Settings -> Extensions`)配置一个文件夹，并将 `jython-standalone-xxx.jar` 放在该文件夹。[Download](https://www.jython.org/download)
- 自行构建：`build.gradle -> shadowJar`

# hook方式显示不全

- 缺少python：如果你下载或打包后的jar包含 `without-jython` 字样，请在Burp的Java environment(`Settings -> Extensions`)配置一个文件夹，并将 `jython-standalone-xxx.jar` 放在该文件夹。[Download](https://www.jython.org/download)
- 缺少js、java：请确保启动Burp的是JDK，而不是JRE。[Modify](https://github.com/outlaws-bai/Galaxy/blob/main/docs/ToJDK.md)

# 本地能运行的代码在插件中不能运行

**python**：使用 jython 支持 python 代码的运行，和你本地环境有差异
**js**：使用 nashorn 支持 js 代码的运行，不等同于 node 环境，部分 js 语法不支持

> 如果有必须使用本地环境的需求，请使用 grpc 的方案，它会提供更具兼容性的跨语言方案

# python hook 方式中怎样载入三方包

> python 的 hook 方式来源于 jython 的跨语言支持，因此如果需要使用三方包，必须能通过 jython 编译，而 jython 本身只支持到 python 2.7.x

安装 python 2.7.x，再用 pip 安装必要的三方包，将三方包所在的文件夹配置到 sys path，如/Users/xxx/.pyenv/versions/2.7.x/lib/python2.7/site-packages
