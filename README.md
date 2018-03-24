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

