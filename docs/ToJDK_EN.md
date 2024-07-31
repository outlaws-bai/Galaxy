# Start Burp using JDK

When you download the executable file version for installation from the Burp official website, it starts using JRE. You need to change it to JDK in order to use the project properly.

## Windows

Generally, if your project uses JRE, there will be a folder named `jre` in the Burp installation directory. We just need to modify it to JDK.

![image-20240728130816351](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728130816351.png)

**1. Check the current version of the JRE.**

```bash
cd jre/bin
java --version
```

For example, when I use the executable file of version `2024.6.3` from the Burp official website, the version of JRE is 21.

![image-20240728130907717](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728130907717.png)

**2. Download the same version of JDK.**

The download path for JDK21 is as follows, for other versions please search and download/install on your own:

https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html

**3. Backup:**

Pack the jre folder:

![image-20240728131345012](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131345012.png)

**4. Delete**

![image-20240728131449867](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131449867.png)

**5. Replace**

Move all the files installed in the directory from step 2 to the jre directory.

![image-20240728131616508](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131616508.png)

![image-20240728131634209](https://raw.githubusercontent.com/outlaws-bai/picture/main/image-20240728131634209.png)

## Mac

Application -> Show Package Contents:

![image-20240728225224470](https://raw.githubusercontent.com/tdyj/picture/main/img/202407282310431.png)

Backup jre.bundle as jre.bundle.bk, select the corresponding version of jdk (usually installed in the `/Library/Java/JavaVirtualMachines` directory), copy it to that directory, and rename it as jre.bundle.

![image-20240728225422084](https://raw.githubusercontent.com/tdyj/picture/main/img/202407282310506.png)



enjoy！

