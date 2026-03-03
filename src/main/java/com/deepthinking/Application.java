package com.deepthinking;

import com.dtflys.forest.springboot.annotation.ForestScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
@ForestScan(basePackages = {"com.deepthinking.client"})
public class Application {

    public static void main(String[] args) {
//        TimeZone.setDefault(TimeZone.getTimeZone(ZONE_ID));
        Locale.setDefault(Locale.CHINA);

        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        Environment env = ctx.getEnvironment();
        String str = String.format("################   Spring Boot Application: %s-%s %s #################",
                env.getProperty("spring.application.name"), env.getProperty("spring.profiles.active"), env.getProperty("server.port"));
        StringBuilder sb = new StringBuilder();
        sb.append("#".repeat(str.length()));
        log.info(sb.toString());
        log.info(str);
        log.info(sb.toString());
//        Properties p = System.getProperties();
//        SortedMap<String, String> m = Maps.newTreeMap();
//        p.keySet().forEach(e -> m.put(String.valueOf(e), p.getProperty(String.valueOf(e))));
//        m.keySet().forEach(k -> logger.info("{}={}", k, m.get(k)));
    }

}
