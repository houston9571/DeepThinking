package com.deepthinking.common.utils;

import cn.hutool.core.util.StrUtil;
import com.deepthinking.common.enums.LanguageEnum;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class SpringContextUtils implements ApplicationContextAware {

    public static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    public static Class<? extends Object> getType(String name) {
        return applicationContext.getType(name);
    }


    /**
     * "zh"
     */
    public static String getLanguage() {
        String s = LocaleContextHolder.getLocale().getLanguage();
        return StrUtil.isNotBlank(s) ? s : LanguageEnum.DEFAULT.getLanguage();
    }

    /**
     * "CN"
     */
    public static String getCountry() {
        String s = LocaleContextHolder.getLocale().getCountry();
        return StrUtil.isNotBlank(s) ? s : LanguageEnum.DEFAULT.getCountry();
    }

    /**
     * "zh-CN"
     */
    public static String getLanguageTag() {
        String s = LocaleContextHolder.getLocale().toLanguageTag();
        return StrUtil.isNotBlank(s) ? s : LanguageEnum.DEFAULT.getLanguageTag();
    }

}
