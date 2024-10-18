# 使用JDK启动Burp

当你在Burp官网下载了可执行文件的版本安装时，它是使用JRE启动，需要将其改为JDK

## Windows

一般来说，如果你的项目使用JRE，那么在Burp安装目录会有一个名为 `jre` 的文件夹，我们只需要将其修改为 JDK 即可。

![image-20240728130816351](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728130816351.png)

**1. 查看当前jre的版本**

```bash
cd jre/bin
java --version
```

比如我使用Burp官网 `2024.6.3` 版本的可执行文件安装后，jre的版本是21

![image-20240728130907717](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728130907717.png)

**2. 下载同版本JDK**

JDK21的下载路径如下，如果其他版本可自行搜索下载、安装

https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html

**3. 备份**

将jre文件夹打包

![image-20240728131345012](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131345012.png)

**4. 删除**

![image-20240728131449867](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131449867.png)

**5. 替换**

将步骤2中已安装目录下的所有文件移动至jre目录下

![image-20240728131616508](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131616508.png)

![image-20240728131634209](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131634209.png)

## Mac

应用程序 -> 显示包内容

![image-20240728225224470](https://raw.githubusercontent.com/tdyj/picture/main/img/202407282310431.png)

1. 备份jre.bundle为jre.bundle.bk
2. 选择jre对应版本的jdk（一般安装在`/Library/Java/JavaVirtualMachines`目录下），复制到该目录下
3. 重命名为jre.bundle即可

![image-20240728225422084](https://raw.githubusercontent.com/tdyj/picture/main/img/202407282310506.png)

完成，祝好。

