package com.deepthinking.core.config;


import com.deepthinking.sprider.SpriderTemplateContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpiderLaunchListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    SpriderTemplateContainer spriderTemplateContainer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            log.info("----> 开始加载系统配置信息... ");
            ResourcePatternResolver pr = new PathMatchingResourcePatternResolver();     //加载多个resource
            spriderTemplateContainer.launcher(pr.getResources("classpath:templates/*.json"));
            log.info("----> 结束加载系统配置信息... ");
        } catch (Exception e) {
            log.error("初始化失败...", e);
        }
    }


}
