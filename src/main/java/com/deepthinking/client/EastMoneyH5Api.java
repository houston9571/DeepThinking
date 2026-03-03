package com.deepthinking.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import org.springframework.stereotype.Component;

import java.util.Map;

@BaseRequest(baseURL = "https://emh5.eastmoney.com/api", headers = {"User-Agent:Mozilla/5.0...", "Host:emh5.eastmoney.com"})
@Component
public interface EastMoneyH5Api {


    @Post(url = "/GuBenGuDong/GetFirstRequest2Data")
    JSONObject getFirstRequest2Data(@JSONBody Map<String, String> map);
}
