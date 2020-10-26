package com.imooc;

import com.imooc.controller.intercepto.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    //静态文件配置 虚拟路径映射物理路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:D:/imooc_videos_dev/");

    }

    /*
    拦截器注册注入
     */
    @Bean
    public MiniInterceptor miniInterceptor(){
        return new MiniInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
                                                   .addPathPatterns("/bgm/**")
                                                   .addPathPatterns("/video/upload","/video/uploadCover"
                                                   ,"/video/userLike","/video/userUnlike","/video/saveComment")
                                                    .excludePathPatterns("/user/queryPublisher");
        super.addInterceptors(registry);
    }
}
