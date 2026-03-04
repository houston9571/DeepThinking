package com.deepthinking.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import org.springframework.stereotype.Component;

@Component
@BaseRequest(headers = {"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36"})
public interface EastMoneyStockApi {

    String URL_PUSH2DELAY = "http://push2delay.eastmoney.com";
    String URL_DATACENTER = "http://datacenter.eastmoney.com";
    String URL_DATACENTER_WEB = "http://datacenter-web.eastmoney.com";

    /**
     * 分页获取股票当日交易行情
     * http://push2delay.eastmoney.com/api/qt/clist/get?fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f14,f15,f16,f17,f18,f20,f21,f23,f24,f34,f35,f37,f40,f41,f45,f46,f48,f49,f57,f64,f65,f66,f69,f70,f71,f72,f75,f76,f77,f78,f81,f82,f83,f84,f87,f109,f129,f297&pn=1&pz=100&np=1&invt=2&fid=f12&po=0&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466%7C0%7C1%7C0%7Cweb&fs=m:0+t:6+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:23+f:!2,m:0+t:81+s:262144+f:!2&_=1770898894918
     */
    @Get(url = URL_PUSH2DELAY  + "/api/qt/clist/get?fields={fields}&pn={pageNum}&pz={pageSize}" +
            "&np=1&invt=2&fid=f12&po=0&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466|0|1|0|web" +
            "&fs=m:0+t:6+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:23+f:!2,m:0+t:81+s:262144+f:!2&_={ts}")
    JSONObject getStockTradeList(@Var("fields") String fields, @Var("pageNum") int pageNum, @Var("pageSize") int pageSize, @Var("ts") long ts);

    /**
     * 股票每分钟交易行情
     * https://push2delay.eastmoney.com/api/qt/stock/trends2/get?secid=1.600089&klt=1&fields1=f1&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62
     */
    @Get(url = URL_PUSH2DELAY + "/api/qt/stock/trends2/get?secid={marketCode}.{stockCode}&_={ts}&klt=1&fields1=f1&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62")
    JSONObject getStockTrends(@Var("stockCode") String stockCode, @Var("marketCode") String marketCode, @Var("ts") long ts);

    /**
     * 实时资金流向
     * klt=1 K线类型 secid可以是股票、板块
     * http://push2delay.eastmoney.com/api/qt/stock/fflow/kline/get?klt=1&secid=1.600089&lmt=10&fields1=f1,f2,f3&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65
     */
    @Get(url = URL_PUSH2DELAY + "/api/qt/stock/fflow/kline/get?klt={klt}&secid={marketCode}.{stockCode}&lmt={lmt}&fields1=f1,f2,f3&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65")
    JSONObject getFundsFlowLines(@Var("stockCode") String stockCode, @Var("marketCode") String marketCode, @Var("klt") int klt, @Var("lmt") int lmt);


    /**
     * 股票实时交易行情
     * http://push2delay.eastmoney.com/api/qt/stock/get?secid=1.600089&ut=fa5fd1943c7b386f172d6893dbfba10b&invt=2&fields=f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178
     */
    @Get(url = URL_PUSH2DELAY + "/api/qt/stock/get?secid={marketCode}.{stockCode}&_={ts}&ut=fa5fd1943c7b386f172d6893dbfba10b&invt=2&fields=f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178")
    JSONObject getStockTradeRealtime(@Var("stockCode") String stockCode, @Var("marketCode") String marketCode, @Var("ts") long ts);




    /**
     * 个股所属概念
     * https://datacenter.eastmoney.com/securities/api/data/get?type=RPT_F10_CORETHEME_BOARDTYPE&sty=ALL&filter=(SECUCODE="600986.SH")&p=1&ps=&sr=1&st=BOARD_RANK&source=HSF10&client=PC&v=04715492084049595
     */
    @Get(url = URL_DATACENTER + "/securities/api/data/get?type=RPT_F10_CORETHEME_BOARDTYPE&sty=ALL" +
            "&filter=(SECUCODE=\"{stockCode}.{market}\")&p=1&ps=&sr=1&st=BOARD_RANK&source=HSF10&client=PC&v=04715492084049595" )
    JSONObject syncStockConcepts(@Var("stockCode") String stockCode, @Var("market") String market);


    /**
     * 实时访问主要指数 fs指定指数代码
     * https://push2.eastmoney.com/api/qt/clist/get?_=1770608220902&fs=i:1.000001,i:0.399001,i:0.399006,i:1.000300,i:100.HSI,i:100.UDI,i:100.DJIA,i:100.SPX,i:100.NDX,i:100.MCX,i:100.FCHI,i:100.GDAXI&fields=f2,f4,f3,f12,f13,f14&np=1&invt=2&pn=1&pz=100&po=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466|0|1|0|web
     */
    JSONObject getStockMarketIndex(@Var("tradeDate") String tradeDate, @Var("stockCode") String stockCode);


}
