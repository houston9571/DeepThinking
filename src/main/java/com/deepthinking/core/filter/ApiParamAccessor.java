package com.deepthinking.core.filter;//package com.deepthinking.core.filter;
//
//import com.google.common.collect.Lists;
//import com.deepthinking.common.utils.IPUtils;
//import com.optimus.vo.ApiInfo;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//
//import java.util.List;
//
//import static com.deepthinking.common.constant.Constants.*;
//
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ApiParamAccessor {
//
//
//    private static final List<String> CHECK_PLATFORM = Lists.newArrayList("/api/player/balance", "/api/order/transferOut");
//
//    private static final List<String> COMMON_INTERFACE = Lists.newArrayList("/api/matchList", "/api/leagueList");
//
//    private static final String ENTER_GAME = "/api/player/enterGame";
//
//    private static final String SYSLOG_FILTER_PREFIX = "/api/bet";
//
//
//    public void processCurrentRequest(HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        String method = request.getMethod();
//        String ip = IPUtils.getIpAddr(request);
//        log.error("{}: {}", uri, ip);
//        ApiInfo apiInfo = ApiInfo.builder().ip(ip).build();
//        RequestContextHolder.currentRequestAttributes().setAttribute(APP_INFO, apiInfo, RequestAttributes.SCOPE_REQUEST);
//    }
//
//
//}
