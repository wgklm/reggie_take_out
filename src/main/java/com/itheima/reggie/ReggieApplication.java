package com.itheima.reggie;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j //日志系统，用户调试DegBug等向控制台输出日志信息
@SpringBootApplication//设置Spring Boot启动类
//添加后控制器、过滤器、监听器可以通过相对于的注解自动注册到Spring容器中
@ServletComponentScan
@EnableTransactionManagement//开启事务支持
@EnableCaching //开启缓存注解功能
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功！！！");
    }
}
