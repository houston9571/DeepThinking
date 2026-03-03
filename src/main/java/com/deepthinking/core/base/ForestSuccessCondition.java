package com.deepthinking.core.base;

import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.HttpStatus;
import com.dtflys.forest.logging.RequestLogMessage;
import lombok.extern.slf4j.Slf4j;

import static org.apache.http.client.cache.HeaderConstants.ETAG;

@Slf4j
public class ForestSuccessCondition implements SuccessWhen {

    /**
     * 请求成功条件
     *
     * @param req Forest请求对象
     * @param res Forest响应对象
     * @return 是否成功，true: 请求成功，false: 请求失败
     */
    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        if (res.noException() && (res.statusCode() == HttpStatus.OK || res.statusCode() == HttpStatus.CREATED)) {
            return true;
        }
        RequestLogMessage reqLog = req.getRequestLogMessage();
        log.error("[Forest][{}]  \nRequest  {}  \n         Body: {} \nResponse Status: {}, Time: {}ms \n         Content: {}",
                res.getHeaderValue(ETAG), reqLog.getRequestLine(), req.body().encodeToString(), res.statusCode(), res.getTimeAsMillisecond(), res.getContent());
        return false;
    }
}