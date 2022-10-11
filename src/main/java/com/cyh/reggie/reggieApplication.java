package com.cyh.reggie;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ServletComponentScan//启动过滤器，才会扫描WebFilter注解
@SpringBootApplication
@MapperScan("com.cyh.reggie.mapper")
@EnableTransactionManagement//开启事务注解
public class reggieApplication {


    public static void main(String[] args) {
        SpringApplication.run(reggieApplication.class, args);
        log.info("项目启动成功");
    }
}
