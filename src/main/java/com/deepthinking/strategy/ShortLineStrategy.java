//package com.deepthinking.strategy;
//
///**
// * 1分钟超短线 隔夜持仓决策系统
// * ta4j 0.22 + 全东财算法对齐
// */
//public class ShortLineStrategy {
//
//
//        private final BarSeries series;
//        private final EastMoney1minKDJ kdj;
//        private final RSIIndicator rsi6;
//        private final BollingerBandsIndicator boll;
//        private final MACDIndicator macd;
//        private final EMAIndicator signalMacd;
//        private final OBVIndicator obv;
//        private final SMAIndicator obvMa5;
//        private final ClosePriceIndicator close;
//
//        public Overnight1minStrategy(BarSeries series) {
//            this.series = series;
//            this.close = new ClosePriceIndicator(series);
//
//            // 1. KDJ(5,2,2) 东财1分钟超短
//            this.kdj = new EastMoney1minKDJ(series);
//
//            // 2. RSI6
//            this.rsi6 = new RSIIndicator(close, 6);
//
//            // 3. BOLL(20,2) 东财版
//            SMAIndicator ma20 = new SMAIndicator(close, 20);
//            this.boll = new BollingerBandsIndicator(ma20, 20, 2.0);
//
//            // 4. MACD
//            this.macd = new MACDIndicator(close, 12, 26);
//            this.signalMacd = new EMAIndicator(macd, 9);
//
//            // 5. OBV + OBV_MA5
//            this.obv = new OBVIndicator(series);
//            this.obvMa5 = new SMAIndicator(obv, 5);
//        }
//
//        // ===================== 指标获取 =====================
//        public double getK() {
//            return kdj.getK(series.getEndIndex()).doubleValue();
//        }
//
//        public double getD() {
//            return kdj.getD(series.getEndIndex()).doubleValue();
//        }
//
//        public double getJ() {
//            return kdj.getJ(series.getEndIndex()).doubleValue();
//        }
//
//        public double getRsi6() {
//            return rsi6.getValue(series.getEndIndex()).doubleValue();
//        }
//
//        public double getBollMiddle() {
//            return boll.getMiddleBand().getValue(series.getEndIndex()).doubleValue();
//        }
//
//        public double getClose() {
//            return close.getValue(series.getEndIndex()).doubleValue();
//        }
//
//        public boolean isMacdLong() {
//            return macd.getValue(series.getEndIndex()).isGreaterThan(signalMacd.getValue(series.getEndIndex()));
//        }
//
//        public boolean isObvStrong() {
//            return obv.getValue(series.getEndIndex()).isGreaterThan(obvMa5.getValue(series.getEndIndex()));
//        }
//
//        // ===================== 核心：隔夜判断 =====================
//        public String judgeOvernight() {
//            int last = series.getEndIndex();
//            double c = getClose();
//            double j = getJ();
//            double rsi = getRsi6();
//            double mid = getBollMiddle();
//
//            boolean cond1 = c > mid;                    // 价格在布林中轨之上
//            boolean cond2 = isMacdLong();              // MACD 多头
//            boolean cond3 = rsi >= 50 && rsi <= 75;    // 强势区不超买
//            boolean cond4 = j <= 85;                    // J 不过热
//            boolean cond5 = isObvStrong();             // 资金健康
//
//            int score = 0;
//            if (cond1) score++;
//            if (cond2) score++;
//            if (cond3) score++;
//            if (cond4) score++;
//            if (cond5) score++;
//
//            if (score >= 4) {
//                return "可以隔夜 ✅";
//            } else if (score == 3) {
//                return "谨慎隔夜 ⚠️";
//            } else {
//                return "不建议隔夜 ❌";
//            }
//        }
//    }
//}