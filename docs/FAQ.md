**请先阅读下方注意事项**

- 项目采用Burp `Montoya API` 开发，Burp版本不低于 `v2023.10.3.7` 。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 项目使用JDK 17进行开发及编译，请确保启动Burp的JDK不低于17。 [Update](https://github.com/outlaws-bai/Galaxy?tab=readme-ov-file#%E5%B8%B8%E7%94%A8%E5%9C%B0%E5%9D%80)
- 项目使用了动态编译，请确保启动Burp的是JDK，而不是JRE。[Modify](https://github.com/outlaws-bai/Galaxy/blob/main/docs/ToJDK.md)
- 如果你下载或打包后的jar包含 `without-jython` 字样，请在Burp的Java environment(`Settings -> Extensions`)配置一个文件夹，并将 `jython-standalone-xxx.jar` 放在该文件夹。[Download](https://www.jython.org/download)
- 自行构建：`build.gradle -> shadowJar`
