# community：牛客论坛项目

这个牛客项目旨在提供一个平台，供技术爱好者和实习生们讨论与技术相关的问题。无论你是一名学生、工程师还是正在寻找实习机会的求职者，这里都是一个互相交流和学习的社区。例如，发布帖子，发布系统信息，学生间可以互相关注，私信来互相交流。

## 项⽬环境
软件版本配置
    
    1、 JDK1.8
    
    2、 apache-maven-3.6.3
    
    3、 SpringBoot 2.4.1
    
    4、 Redis-x64-5.0.10
    
    5、 Elasticsearch-7.9.3
    
    6、 kafka_2.13-2.7.0
    
    7、 wkhtmltopdf(⻓图⽣成⼯具)
    
    8、 mysql 5.7(建议版本>=5.7)


sql⽂件介绍

    1、 init_schema.sql --> 建表sql
    
    2、 init_data.sql --> 初始化数据库数据SQL
    
    3、 tables_mysql_innodb.sql --> quarter定时任务表SQL

## 项⽬概览

### 1. 核⼼功能：
    
    发帖、评论、私信、 转发；
    
    点赞、关注、通知、搜索；
    
    权限、统计、调度、监控；


### 2. 核⼼技术：

    Spring Boot、 SSM
    
    Redis、 Kafka、 ElasticSearch
    
    Spring Security、 Quatz、 Caffeine


### 3. 项⽬亮点：

- 项⽬构建在Spring Boot+SSM框架之上，并统⼀的进⾏了状态管理、事务管理、异常处理；
  
- 利⽤Redis实现了点赞和关注功能，单机可达5000TPS；
  
- 利⽤Kafka实现了异步的站内通知，单机可达7000TPS；
  
- 利⽤ElasticSearch实现了全⽂搜索功能，可准确匹配搜索结果，并⾼亮显示关键词；
  
- 利⽤Caffeine+Redis实现了两级缓存，并优化了热⻔帖⼦的访问，单机可达8000QPS。
  
- 利⽤Spring Security实现了权限控制，实现了多重⻆⾊、 URL级别的权限管理；
  
- 利⽤HyperLogLog、 Bitmap分别实现了UV、 DAU的统计功能， 100万⽤户数据只需*M内存空间；
  
- 利⽤Quartz实现了任务调度功能，并实现了定时计算帖⼦分数、定时清理垃圾⽂件等功能；
  
- 利⽤Actuator对应⽤的Bean、缓存、⽇志、路径等多个维度进⾏了监控，并通过⾃定义的端点对数据库连接进
⾏了监控。

### 4. 网络架构

   <img width="959" alt="网络架构改" src="https://github.com/JBC-JJM/community/assets/80510746/6d922780-c498-4882-bed2-5e3c4eaf4cf9">

### 5. 项目总结

  ![image](https://github.com/JBC-JJM/community/assets/80510746/e6d0d711-5cfb-4302-9a70-bcb12efddd18)

   
