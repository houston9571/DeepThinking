package com.deepthinking.core.base;

import com.deepthinking.common.utils.IDUtils;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.HttpStatus;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.ResponseLogMessage;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpHeaders.ETAG;

@Slf4j
public class ForestLogHandler extends DefaultLogHandler {

    private String tag;


    @Override
    public void logContent(String content) {
        this.getLogger().info("[Forest]" + content, new Object[0]);
    }

    public void logRequest(RequestLogMessage requestLogMessage) {
        String content = this.requestLoggingContent(requestLogMessage);
        tag = IDUtils.uuid16();
        content = "[" + tag + "] " + content;
        this.logContent(content);
    }

    public void logResponseStatus(ResponseLogMessage responseLogMessage) {
        String content = this.responseLoggingContent(responseLogMessage);
        content = "[" + tag + "] " + content;
        this.logContent(content);
    }

    public void logResponseContent(ResponseLogMessage responseLogMessage) {
        if (responseLogMessage.getResponse() != null) {
            String content = "Response Content:\n\t" + responseLogMessage.getResponse().getContent();
            content = "[" + tag + "] " + content;
            this.logContent(content);
        }

    }

    protected String responseLoggingContent(ResponseLogMessage responseLogMessage) {
        ForestResponse response = responseLogMessage.getResponse();
        if (response != null && response.getException() != null) {
            response.getHeaders().addHeader(ETAG, tag);
            return "Response: [Network Error]: " + response.getException().getMessage();
        } else {
            int status = responseLogMessage.getStatus();
            if (status != HttpStatus.OK && status != HttpStatus.CREATED) {
                response.getHeaders().addHeader(ETAG, tag);
            }
            return status >= 0 ? "Response: Status = " + responseLogMessage.getStatus() + ", Time = " + responseLogMessage.getTime() + "ms" : "Response: [Network Error]: Unknown Network Error!";
        }
    }

}