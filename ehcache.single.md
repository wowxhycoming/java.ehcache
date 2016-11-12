# EHCache Single-node
spring 和 ehcache 配合实现缓存

## 一、基于注解的支持

### @Cacheable
@Cacheable可以标记在一个方法上，也可以标记在一个类上。当标记在一个方法上时表示该方法是支持缓存的，当标记在一个类上时则表示该类所有的方法都是支持缓存的。对于一个支持缓存的方法，Spring会在其被**调用后**将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，而不需要再次执行该方法。Spring在缓存方法的返回值时是以键值对进行缓存的，值就是方法的返回结果，至于键的话，Spring又支持两种策略，默认策略和自定义策略。**需要注意的是当一个支持缓存的方法在对象内部被调用时是不会触发缓存功能的**。
@Cacheable可以指定三个属性，value、key和condition。

#### value
value属性来指cache名称，当前方法的返回值被缓存到那个cache上。该属性必须指定。如果要在多个cacahe上进行缓存，可以用数组{"1","2"}

#### key
key属性是用来指定方法的返回结果在cache中对应的key。该属性支持SpringEL表达式。当没有指定该属性时，Spring将使用默认策略生成key。

1. 自定义key - 使用字符串
@Cacheable(value="users", key="someObjectCache")
public User find(Integer id,User user) { return someObject;}

2. 自定义key - 使用EL表达式，"#+参数名"或者"#p参数index"

    ```
    // 用传入参数做key  
    @Cacheable(value="users", key="#id")  
    public User find(Integer id,User user) { return someObject;}
    ```
    
    ```
    // 用参数列表中下标为0的做key
    @Cacheable(value="users", key="#p0")
    public User find(Integer id,User user) { return someObject;}
    ```
    ```
    // 用传入参数对象中的属性
    @Cacheable(value="users", key="#user.id")
    public User find(Integer id,User user) { return someObject;}
    ```
    ```
    // 参数列表中小标为0的参数的id属性做key
    @Cacheable(value="users", key="#p1.id")
    public User find(Integer id,User user) { return someObject;}
    ```

3. 自定义key - 使用spring提供的root对象
    ```
    // 当前方法名
    @Cacheable(value="users", key="#root.methodName") 
    public User find(Integer id,User user) { return someObject;}
    ```
    ```
    // 当前方法
    @Cacheable(value="users", key="#root.method.name")
    public User find(Integer id,User user) { return someObject;}
    ```
    ```
    // 当前被调用的对象
    @Cacheable(value="users", key="#root.target")
    public User find(Integer id,User user) { return someObject;}
    ```
    ```
    // 当前被调用的对象的class
    @Cacheable(value="users", key="#root.targetClass")
    public User find(Integer id,User user) { return someObject;}
    ```
    ```
    // 当前方法参数组成的数组
    @Cacheable(value="users", key="#root.args[0]")
    public User find(Integer id,User user) { return someObject;}
    ```
    ```
    // 当前被调用的方法使用的Cache
    @Cacheable(value="users", key="#root.caches[0].name")
    public User find(Integer id,User user) { return someObject;}
    ```

    当使用root对象的属性作为key时也可以将“#root”省略，因为Spring默认使用的就是root对象的属性。如：
    ```
    @Cacheable(value={"users", "xxx"}, key="caches[1].name")
    public User find(Integer id,User user) { return someObject;}
    ```

#### condition
condition属性指定发生的条件。有时候可能并不希望缓存一个方法所有的返回结果。通过condition属性可以实现这一功能。condition属性默认为空，表示将缓存所有的调用情形。
其值是通过SpringEL表达式来指定的，当为true时表示进行缓存处理；当为false时表示不进行缓存处理，即每次调用该方法时该方法都会执行一次。

```
// 当user的id为偶数时才会进行缓存
@Cacheable(value={"users"}, key="#user.id", condition="#user.id%2==0")
public User find(Integer id,User user) { return someObject;}
```
    
### @CachePut
在支持Spring Cache的环境下，对于使用@Cacheable标注的方法，Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。@CachePut也可以声明一个方法支持缓存功能。与@Cacheable不同的是使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。
@CachePut也可以标注在类上和方法上。使用@CachePut时我们可以指定的属性跟@Cacheable是一样的。

```
//每次都会执行方法，并将结果存入指定的缓存中
@CachePut("users")
public User find(Integer id) {
    returnnull;

}
```

### @CacheEvict
@CacheEvict是用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作。
@CacheEvict可以指定的属性有value、key、condition、allEntries和beforeInvocation。其中value、key和condition的语义与@Cacheable对应的属性类似。即value表示清除操作是发生在哪些Cache上的（对应Cache的名称）；key表示需要清除的是哪个key，如未指定则会使用默认策略生成的key；condition表示清除操作发生的条件。下面我们来介绍一下新出现的两个属性allEntries和beforeInvocation。

#### allEntries
allEntries是boolean类型，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。

```
// 清除 users 下所有的缓存
@CacheEvict(value="users", allEntries=true)
public void delete(Integer id) {
  System.out.println("delete user by id: " + id);
}
```

#### beforeInvocation
清除操作默认是在对应方法成功执行之后触发的，即方法如果因为抛出异常而未能成功返回时也不会触发清除操作。使用beforeInvocation可以改变触发清除操作的时间，当我们指定该属性值为true时，Spring会在调用该方法之前清除缓存中的指定元素。

```
@CacheEvict(value="users", beforeInvocation=true)
public void delete(Integer id) {
  System.out.println("delete user by id: " + id);
}
```

## 二、配置spring对ehcache的支持

### 基于annotation方式
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:cache="http://www.springframework.org/schema/cache"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
	   					   http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
                           http://www.springframework.org/schema/cache http://www.springframework.org/schema/cache/spring-cache-4.0.xsd
		">

   <cache:annotation-driven cache-manager="cacheManager" />
   
    <bean id="defaultCacheManager" class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean">
        <property name="configLocation">
            <value>ehcache.xml</value>
        </property>
    </bean>
    
    <bean id="cacheManager" class="org.springframework.cache.ehcache.EhCacheCacheManager">
        <property name="cacheManager">
            <ref bean="defaultCacheManager"/>
        </property>
    </bean>
   
</beans>
```

\<cache:annotation-driven/\>有一个cache-manager属性用来指定当前所使用的CacheManager对应的bean的名称，默认是cacheManager，所以当我们的CacheManager的id为cacheManager时我们可以不指定该参数，否则就需要我们指定了。

\<cache:annotation-driven/\>还可以指定一个mode属性，可选值有proxy和aspectj。默认是使用proxy。当mode为proxy时，只有缓存方法在外部被调用的时候Spring Cache才会发生作用，这也就意味着如果一个缓存方法在其声明对象内部被调用时Spring Cache是不会发生作用的。而mode为aspectj时就不会有这种问题。另外使用proxy时，只有public方法上的@Cacheable等标注才会起作用，如果需要非public方法上的方法也可以使用Spring Cache时把mode设置为aspectj。

