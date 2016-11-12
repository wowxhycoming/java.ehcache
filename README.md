# java.ehcache.single
直接运行TestAnnotation(me.xhy.javaEhcache.single.spring.annotation.TestAnnotation)

1. 第一次查询（字符串和对象两种参数）需要等待，以后的查询不需要
2. 更新过后 的第一次查询仍然需要等待

# java.ehcache.distribute
由三个项目组成，用于测试分布式缓存


集群多台服务器中的缓存，这里是要同步一些服务器的缓存，单击测试，所以端口不要相同

server1 hostName:localhost port:60001 cacheName:distribute

server2 hostName:localhost port:60002 cacheName:distribute

server3 hostName:localhost port:60003 cacheName:distribute

注意：每台要同步缓存的服务器的RMI通信socket端口都不一样，在配置的时候注意设置

为了保持缓存，应用容器启动程序：

server1 的jetty端口为 9001

server2 的jetty端口为 9002

server3 的jetty端口为 9003

# 避免混淆
为了避免混淆，每个项目中的配置文件均不同名。

# 不同点
## controller
根 mapping  ： d1 d2 d3

## ehcache配置文件
配置文件名称
diskStore path
cacheManagerPeerProviderFactory port 、 rmiUrls

## spring-context.xml
ehcache配置文件名称

## info.jsp 
默认跳转 和 功能url

## pom.xml
jetty 发布端口 和 发布名称

# 启动
创建 maven 命令， 使用 ```jetty:run``` 进行启动。
maven会根据pom.xml中的插件信息（org.mortbay.jetty - maven-jetty-plugin） 启动服务器。


