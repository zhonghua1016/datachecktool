package com.example.datacheck.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * created by yuanjunjie on 2019/6/1 10:01 PM
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 增加静态路径/static/映射，
        // 虽然springboot默认会从/static/中查找静态资源，
        // 但是不匹配url：/static路径
        if(!registry.hasMappingForPattern("/static/**")){
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/static/");
        }
    }

}
