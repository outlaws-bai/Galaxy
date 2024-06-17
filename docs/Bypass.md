# Bypass

主要做Bypass相关的功能

## Bypass Url

**场景**

发现了可能存在SSRF或URL重定向的测试点，存在host校验，想要进行绕过。

**实现**

不同的校验方式有不同的绕过方式，本功能总结了一些绕过的模板。用当前流量的url和用户想要攻击的url对象作为模板的入参，生成payload并发送请求

```
${originUrl.getProtocol()}://${evilUrl.getHost()}?${originUrl.getHost()}
${originUrl.getProtocol()}://${originUrl.getHost()}@${evilUrl.getHost()}
${originUrl.getProtocol()}://${evilUrl.getHost()}/${originUrl.getHost()}
...
```

## Bypass Path

**场景**

通过接口文档或其它方式发现了某个存在漏洞的接口，但WAF或程序的鉴权规则禁止对该接口的访问，想要Bypass

**实现**

> 利用WAF或程序鉴权规则与程序路由匹配规则的逻辑不统一，原理参考自 [浅谈 URL 解析与鉴权中的陷阱](https://tttang.com/archive/1899/)

获取到请求的path后，通过调用 [该函数](https://github.com/outlaws-bai/Galaxy/blob/main/src/main/java/org/m2sec/modules/bypass/BypassTools.java#L14) 生成一系列绕过的payload

## Bypass IP

增加常见Hop By Hop传输IP的请求头, 值默认为127.0.0.1

## 