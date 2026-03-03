package com.deepthinking.ext.filter;

import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.common.exception.ServiceException;
import com.deepthinking.ext.base.Result;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;


@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalApiFilter implements Filter {

//    private final ApiParamAccessor apiParamAccessor;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        try {
//			apiParamAccessor.processCurrentRequest(request);
            chain.doFilter(request, response);
        } catch (ServiceException e) {
            response.setStatus(HttpStatus.OK.value());
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(JSONObject.toJSONString(Result.fail(e.getCode(), e.getMsg())));
            writer.flush();
            writer.close();
        }
    }


}
