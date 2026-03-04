package com.deepthinking.ext.interceptor;

import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestInterceptor;

public class ForestHttpInterceptor implements ForestInterceptor {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String host = request.getHost();
        request.addHeader(ForestHeader.HOST, host);
        request.addHeader(ForestHeader.REFERER, host);
        return true;
    }


}