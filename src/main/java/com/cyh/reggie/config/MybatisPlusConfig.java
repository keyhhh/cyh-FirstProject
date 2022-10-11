package com.cyh.reggie.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
MybatisPlus分页插件
 */
@Configuration
@Slf4j
public class MybatisPlusConfig {

    //通过拦截器的方式加入插件PaginationInnerInterceptor()


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("开启分页查询配置");
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }
}
