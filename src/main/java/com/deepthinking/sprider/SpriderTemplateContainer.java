package com.deepthinking.sprider;

import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.common.utils.StringUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 采集模板容器，包含所有采集模板，系统启动时初始化模板
 */
@Slf4j
@Service
public class SpriderTemplateContainer {

    private Map<String, SpriderTemplate> map = Maps.newHashMap();

    /**
     * 模板文件一定要是UTF-8格式，否则中文无法解析
     */
    public void launcher(Resource... resources) {
        for (Resource tm : resources) {
            String filename = tm.getFilename();
            try {
                if (StringUtil.isNotEmpty(filename)) {
                    String str = StringUtil.readToString(tm.getInputStream());
                    SpriderTemplate st = JSONObject.parseObject(str, SpriderTemplate.class);
                    st.setSiteName(filename);
                    map.put(filename, st);
                    log.info("--> 正在初始化模板文件，filename:{} siteName:{}", filename, st.getSiteName());
                    for (AnalysisFactor factor : st.getFactors()) {
                        factor.getSubsets().forEach(s -> {
                            String p = s.getProperty();
//                            if (propertyMap.containsKey(p) && !s.getDescription().equals(propertyMap.get(p))) {
//                                log.error("Property重复 {}:{} -> {}:{}", p, s.getDescription(), p, propertyMap.get(p));
//                            } else
//                                propertyMap.put(p, s.getDescription());
                        });
                    }
                }
            } catch (Exception e) {
                log.error("--> 读取模板文件错误：" + filename, e);
            }
        }

    }

    /**
     * 采集模板类中会有很多动态信息，所有从容器获取时，需要复制一个对象
     *
     * @param tpl
     * @return
     */
    public SpriderTemplate getSpiderTemplate(String tpl) {
        SpriderTemplate st = map.get(tpl);
        if (st == null)
            log.error("没有找到对应的采集模板，name:" + tpl);
        SpriderTemplate t = new SpriderTemplate();
        BeanUtils.copyProperties(st, t);
//        t.getSubUrl().clear();
        return t;
    }

//    public String getDesc(String property) {
//        return propertyMap.getOrDefault(property, "");
//    }
}
