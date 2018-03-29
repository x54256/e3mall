# e3mall项目Tips
伪静态化：`<url-partten>*.html</url-partten>`，用于SEO搜索引擎优化

handler与controller：

    Controller是一个类
    handler是类中的方法
    
CMS系统：内容维护系统


服务（Service）层tomcat作用：
    
    1.初始化Spring容器（然后就没用了），因为调用的Dubbo发布的服务，根本就没用http；所以我们可以通过一个main方法初始化容器来发布Dubbo服务
    2.便于部署；可以直接打成一个war包发布服务，否则需要先加载一堆jar包，在去执行xxx类

ajax请求数据格式：

    $.post("/content/category/update",{id:node.id,name:node.text});
    虽然是json格式的，但发送的请求还是id=1&name=xxx这种格式
    
MySQL数据库`select last_insert_id();`是根据当前事务来取的，所以并不会取错
    
    mybaits返回主键配置
    <selectKey keyProperty="id" resultType="long" order="AFTER">
      select last_insert_id()
    </selectKey>


Spring引入的properties文件只有一个生效
    
    但可以修改Spring配置文件：<context:property-placeholder location="classpath:*.properties"/>
    
    
扫描多个mapper包的方法

    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 基本包，会自动扫描子包 -->
        <property name="basePackage" value="cn.e3mall.mapper,**cn.e3mall.mapper2**"/>
    </bean>
    
    
返回逻辑视图时要去掉`@ResponseBody`（代表直接向浏览器写内容，如果不是字符串，转成json格式）注解


Tomcat的强项是处理JSP和Servlet，所以处理静态页面可以使用任意http服务器（nginx）


由于建立Tomcat集群后会出现只有一台Tomcat服务器上有Session，所以就需要Session共享；但`Session复制`会有Tomcat节点上限
所以我们使用sso单点登录系统（来解决session共享问题）


分布式事务：一般都没有使用这种的，一般都是使用MQ来解决的


为了保证我们模拟的session在不同浏览器上的状态不同，所以我们不能使用userId，要模仿session的sessionId使用UUID

1）设置响应application/json
2）jsonP
3）跨域戴cookie