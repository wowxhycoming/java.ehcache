# EHCache Distributed note
使用 ehcache 实现分布式缓存

## 一、需要了解

### 缓存集群方案

1. RMI
2. JGroups
3. EhCache Server
4. Terracotta
5. JMS

其中的三种最为常用集群方式，分别是 RMI、JGroups 以及 EhCache Server 。
这里主要介绍RMI的方式。 对应项目 java.ehcache.distribute1、java.ehcache.distribute2、java.ehcache.distribute3

### 分布式缓存需要关心的问题

1. 如何知道集群环境中的其他缓存
2. 分布式传送的消息是什么形式
3. 什么情况需要进行复制？增加（Puts），更新（Updates）或是失效（Expiries）？
4. 采用什么方式进行复制？同步还是异步方式？

为了安装分布式缓存，你需要配置一个PeerProvider、一个CacheManagerPeerListener，

它们对于一个CacheManager来说是全局的。每个进行分布式操作的cache都要添加一个cacheEventListener来传送消息。

## 二、集群缓存概念及其配置

### 正确的元素类型

只有可序列化的元素可以进行复制。
一些操作，比如移除，只需要元素的键值而不用整个元素；在这样的操作中即使元素不是可序列化的但键值是可序列化的也可以被复制。

### 成员发现（Peer Discovery）

Ehcache进行集群的时候有一个cache组的概念。每个cache都是其他cache的一个peer，没有主cache的存在，既集群中的每个节点都是对等的。

刚才的一个问题：如何知道集群环境中的其他缓存？这个问题可以命名为成员发现（Peer Discovery）。

Ehcache提供了两种机制用来进行成员发现：手动 和 自动。要使用一个内置的成员发现机制要在`ehcache的配置文件`中指定`cacheManagerPeerProviderFactory`元素的class属性为`net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory`。

1. 自动的成员发现

自动的发现方式用TCP广播机制来确定和维持一个广播组。它只需要一个简单的配置可以自动的在组中添加和移除成员。在集群中也不需要什么优化服务器的知识，这是默认推荐的。

成员每秒向群组发送一个“心跳”。如果一个成员 5秒种都没有发出信号它将被群组移除。如果一个新的成员发送了一个“心跳”它将被添加进群组。

任何一个用这个配置安装了复制功能的cache都将被其他的成员发现并标识为可用状态。

要设置自动的成员发现，需要指定`ehcache配置文件`中`cacheManagerPeerProviderFactory元素的properties属性`。

2. 自动的成员发现示例

假设在集群中有两台服务器。希望同步sampleCache1和sampleCache2。每台独立的服务器都要有这样的配置：配置server1和server2
```
<cacheManagerPeerProviderFactory
    class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
    properties="peerDiscovery=automatic, 
                multicastGroupAddress=230.0.0.1, 
                multicastGroupPort=4446, 
                timeToLive=32"
/>
```

3. 手动进行成员发现

进行手动成员配置要知道每个监听器的IP地址和端口。`成员不能在运行时动态地添加和移除`。在技术上很难使用广播的情况下就可以手动成员发现，例如在集群的服务器之间有一个不能传送广播报文的路由器。你也可以用手动成员发现进行`单向`的数据复制，只让server2知道server1，而server1不知道server2。

配置手动成员发现，需要指定ehcache配置文件中cacheManagerPeerProviderFactory的properties属性。

4. 手动进行成员发现示例

假设你在集群中有两台服务器。你要同步sampleCache1和sampleCache2。下面是每个服务器需要的配置：

配置server1
```
<cacheManagerPeerProviderFactory
    class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
    properties="peerDiscovery=manual,
                rmiUrls=//server2:40001/sampleCache11|//server2:40001/sampleCache12"
/>
```

配置server2
```
<cacheManagerPeerProviderFactory
    class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
    properties="peerDiscovery=manual,
                rmiUrls=//server1:40001/sampleCache11|//server1:40001/sampleCache12"/>
```

rmiUrls配置的是服务器cache peers的列表。`注意不要重复配置`。

### 配置CacheManagerPeerListener

每个CacheManagerPeerListener监听从成员们发向当前CacheManager的消息。配置CacheManagerPeerListener需要指定一个CacheManagerPeerListenerFactory，它以插件的机制实现，用来创建CacheManagerPeerListener。

cacheManagerPeerListenerFactory的属性有：<br/>
class – 一个完整的工厂类名 <br/>
properties – 只对这个工厂有意义的属性，使用逗号分隔 <br/>

properties的属性：<br/>
hostname (可选) – 运行监听器的服务器名称。标明了做为集群群组的成员的地址，同时也是你想要控制的从集群中接收消息的接口。<br/>
port – 监听器监听的端口。<br/>
socketTimeoutMillis (可选) – Socket超时的时间。默认是2000ms。当你socket同步缓存请求地址比较远，不是本地局域网。你可能需要把这个时间配置大些，不然很可能延时导致同步缓存失败。

Ehcache有一个内置的基于RMI的分布系统。它的监听器是RMICacheManagerPeerListener，这个监听器可以用RMICacheManagerPeerListenerFactory来配置。
```
<cacheManagerPeerListenerFactory
    class="net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory"
    properties="hostName=localhost, port=40001, socketTimeoutMillis=2000"
/>
```

注意：
1. 在CacheManager初始化的时候会检查hostname是否可用。如果hostName不可用，CacheManager将拒绝启动并抛出一个连接被拒绝的异常。
2. 如果指定hostname，hostname将使用InetAddress.getLocalHost().getHostAddress()来得到。

警告：
1. 不要将localhost配置为本地地址127.0.0.1，因为它在网络中不可见将会导致不能从远程服务器接收信息从而不能复制。在同一台机器上有多个CacheManager的时候，你应该只用localhost来配置。

### 配置CacheReplicators

每个要进行同步的cache都需要设置一个用来向CacheManagerr的成员复制消息的缓存事件监听器。这个工作要通过为每个cache的配置增加一个cacheEventListenerFactory元素来完成。

cacheEventListenerFactory的属性：<br/>
class – 使用net.sf.ehcache.distribution.RMICacheReplicatorFactory<br/>
这个工厂支持以下属性：<br/>
replicatePuts=true | false – 当一个新元素增加到缓存中的时候是否要复制到其他的peers. 默认是true。<br/>
replicateUpdates=true | false – 当一个已经在缓存中存在的元素被覆盖时是否要进行复制。默认是true。<br/>
replicateRemovals= true | false – 当元素移除的时候是否进行复制。默认是true。<br/>
replicateAsynchronously=true | false – 复制方式是异步的（指定为true时）还是同步的（指定为false时）。默认是true。<br/>
replicatePutsViaCopy=true | false – 当一个新增元素被拷贝到其他的cache中时是否进行复制指定为true时为复制，默认是true。<br/>
replicateUpdatesViaCopy=true | false – 当一个元素被拷贝到其他的cache中时是否进行复制（指定为true时为复制），默认是true。<br/>

可以使用ehcache的默认行为从而减少配置的工作量，默认的行为是以异步的方式复制每件事；可以像下面的例子一样减少RMICacheReplicatorFactory的属性配置：
```
<cache name="distribution"
    maxElementsInMemory="10"
    eternal="true"
    overflowToDisk="false"
    memoryStoreEvictionPolicy="LFU">
    <cacheEventListenerFactory class="net.sf.ehcache.distribution.RMICacheReplicatorFactory"/>
</cache>
```
也可以全部指定出来：
```
<cache name="sampleCache2"
    maxElementsInMemory="10"
    eternal="false"
    timeToIdleSeconds="100"
    timeToLiveSeconds="100"
    overflowToDisk="false">
    <cacheEventListenerFactory 
        class="net.sf.ehcache.distribution.RMICacheReplicatorFactory"
        properties="replicateAsynchronously=true,
                    replicatePuts=true, 
                    replicateUpdates=true, 
                    replicateUpdatesViaCopy=false, 
                    replicateRemovals=true "/>
</cache>
```

## 常见的问题

### 被序列化的对象一定要实现 Serializable 接口

### 分布式测试方式

1. 更新时会在缓存中删除 key 
2. 查询时会在缓存中添加 key

如果在节点1对 key 进行了更新操作，这时，所有节点将同时删除 key 的缓存， 由于没有使用数据库，所以持久化数据是不同步的。

要想使缓存同步，需要在发生更新的节点上 紧接一个查询操作，这时，将同步该查询结果到其他节点。

### Windows上的Tomcat
有一个Tomcat或者是JDK的bug，在tomcat启动时如果tomcat的安装路径中有空格的话，在启动时RMI监听器会失败。参见http://archives.java.sun.com/cgi-bin/wa?A2=ind0205&L=rmi-users&P=797和http://www.ontotext.com/kim/doc/sys-doc/faq-howto-bugs/known-bugs.html。

由于在Windows上安装Tomcat默认是装在“Program Files”文件夹里的，所以这个问题经常发生。

### 广播阻断
自动的peer discovery与广播息息相关。广播可能被路由阻拦，像Xen和VMWare这种虚拟化的技术也可以阻拦广播。如果这些都打开了，你可能还在要将你的网卡的相关配置打开。一个简单的办法可以得知广播是否有效，那就是使用`ehcache remote debugger`来看“心跳”是否可用。

### 广播传播的不够远或是传得太远
可以通过设置badly misnamed time to live来控制广播传播的距离。用广播IP协议时，timeToLive的值指的是数据包可以传递的域或是范围。约定如下：

0是限制在同一个服务器

1是限制在同一个子网

32是限制在同一个网站

64是限制在同一个region

128是限制在同一个大洲

255是不限制

译者提示：上面这些资料翻译的不够准确，请读者自行寻找原文理解吧。

在Java实现中默认值是1，也就是在同一个子网中传播。改变timeToLive属性可以限制或是扩展传播的范围。

###################################
## 简单汇总
发现成员 : cacheManagerPeerProviderFactory 。 一个内置成员 ： net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory

分布系统监听器 ： RMICacheManagerPeerListener 。 用 RMICacheManagerPeerListenerFactory 来配置。

向（CacheManager的）成员复制消息的监听器 ： RMICacheReplicatorFactory 。 通过<cache>下的 <cacheEventListenerFactory>标签来设置。

