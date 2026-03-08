package com.deepthinking.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import org.springframework.stereotype.Component;

import static cn.hutool.core.text.StrPool.COLON;
import static com.dtflys.forest.http.ForestHeader.HOST;

@Component
@BaseRequest(baseURL = "http://push2delay.eastmoney.com", headers = {"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36"})
public interface EastMoneyConceptApi {


    /**
     * 概念板块列表，按涨跌幅排序
     * http://push2delay.eastmoney.com/api/qt/clist/get?fs=m:90+t:3+f:!50&pn=1&pz=100&fields=f12,f13,f14,f1,f2,f4,f3,f6,f152,f20,f8,f104,f105,f297&fid=f3&po=1&np=1&fltt=1&invt=2&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=|0|0|0|web&_=1770654328438
     */
    @Get("/api/qt/clist/get?fs=m:90+t:3+f:!50&pn={pageNum}&pz={pageSize}&fields=f12,f13,f14,f1,f2,f4,f3,f6,f152,f20,f8,f104,f105,f297&fid=f3" +
            "&po=1&np=1&fltt=1&invt=2&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=|0|0|0|web&_={ts}")
    JSONObject syncConceptTradeList(@Var("pageNum") int pageNum, @Var("pageSize") int pageSize, @Var("ts") long ts);


    /**
     * 获取最新资金流向
     * secid可以是股票、板块
     * https://push2.eastmoney.com/api/qt/stock/get?secid=90.BK1172&fields=f135,f136,f137,f138,f139,f140,f141,f142,f143,f144,f145,f146,f147,f148,f149&invt=2&fltt=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466|0|1|0|web&dect=1&_=1770693880163
     */
    @Get("/api/qt/stock/get?secid={marketCode}.{conceptCode}&fields=f135,f136,f137,f138,f139,f140,f141,f142,f143,f144,f145,f146,f147,f148,f149" +
            "&invt=2&fltt=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466|0|1|0|web&dect=1&_={ts}")
    JSONObject syncFundsFlow(@Var("conceptCode") String conceptCode, @Var("marketCode") String marketCode, @Var("ts") long ts);

    /**
     * 所属概念的个股列表
     * <a href="https://push2delay.eastmoney.com/api/qt/clist/get?fs=b:BK1172+f:!50&pn=1&pz=100&fields=f12,f13,f14,f1,f2,f4,f3,f152,f5,f6,f7,f15,f18,f16,f17,f10,f8,f9,f23&fid=f3&po=1&np=1&fltt=1&invt=2&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=|0|0|0|web&_=1770654635342">...</a>
     */
    @Get("/api/qt/clist/get?fs=b:{conceptCode}+f:!50&pn=1&pz=100&fields=f2,f3,f7,f8,f12,f14" +
            "&fid=f3&po=1&np=1&fltt=1&invt=2&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=|0|0|0|web&_={ts}")
    JSONObject syncConceptStocks(@Var("conceptCode") String conceptCode, @Var("ts") long ts);

}
