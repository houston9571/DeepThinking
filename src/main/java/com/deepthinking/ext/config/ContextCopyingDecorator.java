package com.deepthinking.ext.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextCopyingDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        //获取主线程中的请求信息
        RequestAttributes context = RequestContextHolder.currentRequestAttributes();
        return () -> {
            try {
                //将主线程中的请求信息，设置到子线程中
                RequestContextHolder.setRequestAttributes(context);
                //执行子线程
                runnable.run();
            } finally {
                //线程结束，必须清空，否则内存泄漏
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }

}
