/******/ (function(modules) { // webpackBootstrap
    /******/ 	// The module cache
    /******/ 	var installedModules = {};
    /******/
    /******/ 	// The require function
    /******/ 	function __webpack_require__(moduleId) {
        /******/
        /******/ 		// Check if module is in cache
        /******/ 		if(installedModules[moduleId]) {
            /******/ 			return installedModules[moduleId].exports;
            /******/ 		}
        /******/ 		// Create a new module (and put it into the cache)
        /******/ 		var module = installedModules[moduleId] = {
            /******/ 			i: moduleId,
            /******/ 			l: false,
            /******/ 			exports: {}
            /******/ 		};
        /******/
        /******/ 		// Execute the module function
        /******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
        /******/
        /******/ 		// Flag the module as loaded
        /******/ 		module.l = true;
        /******/
        /******/ 		// Return the exports of the module
        /******/ 		return module.exports;
        /******/ 	}
    /******/
    /******/
    /******/ 	// expose the modules object (__webpack_modules__)
    /******/ 	__webpack_require__.m = modules;
    /******/
    /******/ 	// expose the module cache
    /******/ 	__webpack_require__.c = installedModules;
    /******/
    /******/ 	// define getter function for harmony exports
    /******/ 	__webpack_require__.d = function(exports, name, getter) {
        /******/ 		if(!__webpack_require__.o(exports, name)) {
            /******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
            /******/ 		}
        /******/ 	};
    /******/
    /******/ 	// define __esModule on exports
    /******/ 	__webpack_require__.r = function(exports) {
        /******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
            /******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
            /******/ 		}
        /******/ 		Object.defineProperty(exports, '__esModule', { value: true });
        /******/ 	};
    /******/
    /******/ 	// create a fake namespace object
    /******/ 	// mode & 1: value is a module id, require it
    /******/ 	// mode & 2: merge all properties of value into the ns
    /******/ 	// mode & 4: return value when already ns object
    /******/ 	// mode & 8|1: behave like require
    /******/ 	__webpack_require__.t = function(value, mode) {
        /******/ 		if(mode & 1) value = __webpack_require__(value);
        /******/ 		if(mode & 8) return value;
        /******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
        /******/ 		var ns = Object.create(null);
        /******/ 		__webpack_require__.r(ns);
        /******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
        /******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
        /******/ 		return ns;
        /******/ 	};
    /******/
    /******/ 	// getDefaultExport function for compatibility with non-harmony modules
    /******/ 	__webpack_require__.n = function(module) {
        /******/ 		var getter = module && module.__esModule ?
            /******/ 			function getDefault() { return module['default']; } :
            /******/ 			function getModuleExports() { return module; };
        /******/ 		__webpack_require__.d(getter, 'a', getter);
        /******/ 		return getter;
        /******/ 	};
    /******/
    /******/ 	// Object.prototype.hasOwnProperty.call
    /******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
    /******/
    /******/ 	// __webpack_public_path__
    /******/ 	__webpack_require__.p = "";
    /******/
    /******/
    /******/ 	// Load entry module and return exports
    /******/ 	return __webpack_require__(__webpack_require__.s = "./jssrc/zjlx/stock.ts");
    /******/ })
    /************************************************************************/
    /******/ ({

        /***/ "./config/web.js":
        /*!***********************!*\
          !*** ./config/web.js ***!
          \***********************/
        /*! no static exports found */
        /***/ (function(module, exports) {

            function getUrlParam(name){
                var urlpara = location.search;
                var par = {};
                if (urlpara != "") {
                    urlpara = urlpara.substring(1, urlpara.length);
                    var para = urlpara.split("&");
                    var parname;
                    var parvalue;
                    for (var i = 0; i < para.length; i++) {
                        parname = para[i].substring(0, para[i].indexOf("="));
                        parvalue = para[i].substring(para[i].indexOf("=") + 1, para[i].length);
                        par[parname] = parvalue;
                    }
                }
                if(typeof (par[name]) != "undefined"){
                    return par[name];
                }
                else{
                    return null;
                }
            }

            module.exports = {
                development: {
                    dataurl: function(){
                        return '//reportapi-uat.eastmoney.com/'
                    },
                    quoteurl: function(){
                        // return '//push2test.eastmoney.com/'
                        return '//push2.eastmoney.com/'
                    },
                    quotehisurl: function(){
                        return '//push2test.eastmoney.com/'
                    },
                    dcfmurl: function(){
                        return '//dcfm.eastmoney.com/'
                    },
                    anoticeurl: function(){
                        return '//np-anotice-stock.eastmoney.com/'
                    },
                    cnoticeurl: function(){
                        return '//np-cnotice-stock-test.eastmoney.com/'
                    },
                    datacenter: function(){
                        // return '//datacenter-web.eastmoney.com/'
                        let env = getUrlParam('myenv') || '';
                        if(env == 'prod') {
                            return '//datacenter-web.eastmoney.com/'
                        }
                        return '//testdatacenter.eastmoney.com/'
                    },
                    soapi: function(){
                        return '//searchapi.eastmoney.com/'
                    },
                    cmsdataapi: function(){
                        return '//cmsdataapi.eastmoney.com/'
                    },
                    newsinfo: function(){
                        return '//newsinfo.eastmoney.com/'
                    },
                    url: function(){
                        return ''
                    },
                    cmsapi : function () {
                        return '//np-listapi.eastmoney.com//'
                    }
                },
                zptest: {
                    dataurl: function(){
                        // return '//reportapi.eastmoney.com/'
                        return '//reportapi-uat.eastmoney.com/'   // '//reportapi.uat.emapd.com/'
                    },
                    quoteurl: function(){
                        // return '//push2test.eastmoney.com/'
                        return '//push2.eastmoney.com/'
                    },
                    quotehisurl: function(){
                        return '//push2test.eastmoney.com/'
                    },
                    dcfmurl: function(){
                        return '//dcfm.eastmoney.com/'
                    },
                    anoticeurl: function(){
                        return '//np-anotice-stock-test.eastmoney.com/'
                    },
                    cnoticeurl: function(){
                        // return '//np-cnotice-stock-test.emapd.com/'
                        return '//np-cnotice-stock-test.eastmoney.com/'
                    },
                    datacenter: function(){
                        // return '//datacenter-web.eastmoney.com/'
                        let env = getUrlParam('myenv') || '';
                        if(env == 'prod') {
                            return '//datacenter-web.eastmoney.com/'
                        }

                        return '//testdatacenter.eastmoney.com/'
                    },
                    soapi: function(){
                        return '//searchapi.eastmoney.com/'
                    },
                    cmsdataapi: function(){
                        return '//cmsdataapi.eastmoney.com/'
                    },
                    newsinfo: function(){
                        return '//newsinfo.eastmoney.com/'
                    },
                    url: function(){
                        return ''
                    },
                    cmsapi : function () {
                        return '//np-listapi.eastmoney.com//'
                    }
                },
                gray: {
                    dataurl: function(){
                        return '//reportapi.eastmoney.com/'
                    },
                    quoteurl: function(){
                        return '//push2.eastmoney.com/'
                    },
                    quotehisurl: function(){
                        return '//push2his.eastmoney.com/'
                    },
                    dcfmurl: function(){
                        return '//dcfm.eastmoney.com/'
                    },
                    anoticeurl: function(){
                        return '//np-anotice-stock.eastmoney.com/'
                    },
                    cnoticeurl: function(){
                        return '//np-cnotice-stock.eastmoney.com/'
                    },
                    datacenter: function(){
                        return '//graydatacenter.eastmoney.com/web/'
                    },
                    soapi: function(){
                        return '//searchapi.eastmoney.com/'
                    },
                    cmsdataapi: function(){
                        return '//cmsdataapi.eastmoney.com/'
                    },
                    newsinfo: function(){
                        return '//newsinfo.eastmoney.com/'
                    },
                    url: function(){
                        return ''
                    },
                    cmsapi : function () {
                        return '//np-listapi.eastmoney.com//'
                    }
                },
                production: {
                    dataurl: function(){
                        return '//reportapi.eastmoney.com/'
                    },
                    quoteurl: function(){
                        return '//push2.eastmoney.com/'
                    },
                    quotehisurl: function(){
                        return '//push2his.eastmoney.com/'
                    },
                    dcfmurl: function(){
                        return '//dcfm.eastmoney.com/'
                    },
                    anoticeurl: function(){
                        return '//np-anotice-stock.eastmoney.com/'
                    },
                    cnoticeurl: function(){
                        return '//np-cnotice-stock.eastmoney.com/'
                    },
                    datacenter: function(){
                        return '//datacenter-web.eastmoney.com/'
                    },
                    soapi: function(){
                        return '//searchapi.eastmoney.com/'
                    },
                    cmsdataapi: function(){
                        return '//cmsdataapi.eastmoney.com/'
                    },
                    newsinfo: function(){
                        return '//newsinfo.eastmoney.com/'
                    },
                    url: function(){
                        return ''
                    },
                    cmsapi : function () {
                        return '//np-listapi.eastmoney.com//'
                    }
                },
                getParam: function(name){
                    var urlpara = location.search;
                    var par = {};
                    if (urlpara != "") {
                        urlpara = urlpara.substring(1, urlpara.length);
                        var para = urlpara.split("&");
                        var parname;
                        var parvalue;
                        for (var i = 0; i < para.length; i++) {
                            parname = para[i].substring(0, para[i].indexOf("="));
                            parvalue = para[i].substring(para[i].indexOf("=") + 1, para[i].length);
                            par[parname] = parvalue;
                        }
                    }
                    if(typeof (par[name]) != "undefined"){
                        return par[name];
                    }
                    else{
                        return null;
                    }
                },
                getWebPath: function (name) {
                    var env=(window.service&&service.ENV)||this.getParam('env');
                    if (env&&this[env]) {
                        return this[env][name]()
                    }
                    return this.production[name]()
                }
            }



            /***/ }),

        /***/ "./jssrc/zjlx/base.ts":
        /*!****************************!*\
          !*** ./jssrc/zjlx/base.ts ***!
          \****************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            /**
             * pageopt
             */
            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            exports.__esModule = true;
            var fieldtools_1 = __importDefault(__webpack_require__(/*! ../../src/modules/commonutil/fieldtools */ "./src/modules/commonutil/fieldtools.ts"));
            var webconfig = __webpack_require__(/*! ../../config/web.js */ "./config/web.js");
///资金流向排行数据配置
            var mapping = {
                "f2": "zxj",
                "f3": "zdf",
                "f127": "zdf",
                "f109": "zdf",
                "f160": "zdf",
                "f12": "code",
                "f14": "name",
                "f62": "zlje",
                "f184": "zljzb",
                "f66": "cddje",
                "f69": "cddjzb",
                "f72": "ddje",
                "f75": "ddjzb",
                "f78": "zdje",
                "f81": "zdjzb",
                "f84": "xdje",
                "f87": "xdjzb",
                "f267": "zlje",
                "f268": "zljzb",
                "f269": "cddje",
                "f270": "cddjzb",
                "f271": "ddje",
                "f272": "ddjzb",
                "f273": "zdje",
                "f274": "zdjzb",
                "f275": "xdje",
                "f276": "xdjzb",
                "f164": "zlje",
                "f165": "zljzb",
                "f166": "cddje",
                "f167": "cddjzb",
                "f168": "ddje",
                "f169": "ddjzb",
                "f170": "zdje",
                "f171": "zdjzb",
                "f172": "xdje",
                "f173": "xdjzb",
                "f174": "zlje",
                "f175": "zljzb",
                "f176": "cddje",
                "f177": "cddjzb",
                "f178": "ddje",
                "f179": "ddjzb",
                "f180": "zdje",
                "f181": "zdjzb",
                "f182": "xdje",
                "f183": "xdjzb",
                "f205": "zdcode",
                "f204": "zdname",
                "f258": "zdcode",
                "f257": "zdname",
                "f261": "zdcode",
                "f260": "zdname",
                "f225": "zlpm1",
                "f263": "zlpm5",
                "f264": "zlpm10"
            };
            var objs = {};
            var pageopt = {
                statvalues: {
                    "1": "f12,f14,f2,f3,f62,f184,f66,f69,f72,f75,f78,f81,f84,f87,f204,f205,f124,f1,f13",
                    "3": "f12,f14,f2,f127,f267,f268,f269,f270,f271,f272,f273,f274,f275,f276,f257,f258,f124,f1,f13",
                    "5": "f12,f14,f2,f109,f164,f165,f166,f167,f168,f169,f170,f171,f172,f173,f257,f258,f124,f1,f13",
                    "10": "f12,f14,f2,f160,f174,f175,f176,f177,f178,f179,f180,f181,f182,f183,f260,f261,f124,f1,f13"
                },
                mktvalues: {
                    "all": "m:0+t:6+f:!2,m:0+t:13+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:23+f:!2,m:0+t:7+f:!2,m:1+t:3+f:!2",
                    "hsa": "m:0+t:6+f:!2,m:0+t:13+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:23+f:!2",
                    "sha": "m:1+t:2+f:!2,m:1+t:23+f:!2",
                    "kcb": "m:1+t:23+f:!2",
                    "sza": "m:0+t:6+f:!2,m:0+t:13+f:!2,m:0+t:80+f:!2",
                    "cyb": "m:0+t:80+f:!2",
                    "zxb": "m:0+t:13+f:!2",
                    "hb": "m:1+t:3+f:!2",
                    "sb": "m:0+t:7+f:!2",
                    "bja": "m:0+t:81+s:262144+f:!2"
                },
                bkvalues: {
                    "hy": "m:90 t:2",
                    "gn": "m:90 t:3",
                    "dy": "m:90 t:1"
                },
                columns: {
                    "f12": {
                        formatter: function (value, item, i, field) {
                            if (item.f13 != undefined)
                                return "<a href=\"//quote.eastmoney.com/unify/r/" + item.f13 + "." + value + "\">" + value + "</a>";
                            return "<a href=\"//quote.eastmoney.com/unify/r/" + fieldtools_1["default"].datatoquotecode(value) + "\">" + value + "</a>";
                        }
                    },
                    "f14": {
                        formatter: function (value, item, i, field) {
                            return "<a href=\"/stockdata/" + item.f12 + ".html\">" + fieldtools_1["default"].fmtsname(value) + "</a>";
                        }
                    },
                    "name_bk": {
                        formatter: function (value, item, i, field) {
                            return "<a href=\"//quote.eastmoney.com/unify/r/90." + item.f12 + "\">" + item.f14 + "</a>";
                        }
                    },
                    "ssbk": {
                        formatter: function (value, item, i, field) {
                            return "<a href=\"/bkzj/" + item.f265 + ".html\">" + item.f100 + "</a>";
                        }
                    },
                    "xg_bk": {
                        formatter: function (value, item, i, field) {
                            return "<a class=\"red\" href=\"/bkzj/" + item.f12 + ".html\">\u5927\u5355\u8BE6\u60C5</a>&nbsp;\n                <a href=\"//guba.eastmoney.com/list," + item.f12 + ".html\">\u80A1\u5427</a>";
                        }
                    },
                    "xg_gg": {
                        formatter: function (value, item, i, field) {
                            return "<a class=\"red\" href=\"/zjlx/" + item.f12 + ".html\">\u8BE6\u60C5</a>&nbsp;\n                <a href=\"/stockdata/" + item.f12 + ".html\">\u6570\u636E</a>&nbsp;\n                <a href=\"//guba.eastmoney.com/list," + item.f12 + ".html\">\u80A1\u5427</a>&nbsp;\n                <a href=\"/report/" + item.f12 + ".html\">\u7814\u62A5</a>";
                        }
                    },
                    "xg_ggd": {
                        formatter: function (value, item, i, field) {
                            return "<a class=\"red\" href=\"/zjlx/" + item.f12 + ".html\">\u8BE6\u60C5</a>&nbsp;\n                <a href=\"/stockdata/" + item.f12 + ".html\">\u6570\u636E</a>&nbsp;\n                <a href=\"//guba.eastmoney.com/list," + item.f12 + ".html\">\u80A1\u5427</a>";
                        }
                    },
                    "f2": {
                        formatter: function (value, item, i, field) {
                            return fieldtools_1["default"].fieldfmt(value, { num: item.f1 || 2, omit: false, trim: false, showcolor: true, colorvalue: item.f3 });
                        }
                    },
                    "zdf": emfield.bfb2_c,
                    "zlje": emfield.num2_c,
                    "zljzb": emfield.bfb2_c,
                    "cddje": emfield.num2_c,
                    "cddjzb": emfield.bfb2_c,
                    "ddje": emfield.num2_c,
                    "ddjzb": emfield.bfb2_c,
                    "zdje": emfield.num2_c,
                    "zdjzb": emfield.bfb2_c,
                    "xdje": emfield.num2_c,
                    "xdjzb": emfield.bfb2_c,
                    "f3": emfield.bfb2_c,
                    "f109": emfield.bfb2_c,
                    "f160": emfield.bfb2_c,
                    "f225": emfield.string,
                    "f263": emfield.string,
                    "f264": emfield.string,
                    "f184": emfield.bfb2_c,
                    "f165": emfield.bfb2_c,
                    "f175": emfield.bfb2_c,
                    "zdname": {
                        formatter: function (value, item, i, field) {
                            return fieldtools_1["default"].isEmpty(item.zdcode) ? '-' : "<a href=\"/zjlx/" + item.zdcode + ".html\">" + fieldtools_1["default"].fmtsname(value) + "</a>";
                        },
                    }
                },
                tophqnum: function () {
                    $.ajax({
                        url: webconfig.getWebPath('quoteurl') + 'api/qt/ulist.np/get',
                        type: 'GET',
                        dataType: 'jsonp',
                        jsonp: 'cb',
                        data: {
                            fltt: "2",
                            secids: "1.000001,0.399001",
                            fields: "f1,f2,f3,f4,f6,f12,f13,f104,f105,f106",
                            ut: "b2884a393a59ad64002292a3e90d46a5"
                        },
                    }).then(function (json) {
                        if (json && json.data && json.data.diff.length == 2) {
                            var shdata = json.data.diff[0];
                            var szdata = json.data.diff[1];
                            var _html = "<a href=\"//quote.eastmoney.com/unify/r/1.000001\" class=\"blue\">\n                        <b>\u4E0A\u8BC1</b>&nbsp;\n                        </a>:\n                        <b class=\"" + (shdata["f4"] > 0 ? "red" : (shdata["f4"] < 0 ? "green" : "")) + "\">\n                            " + fieldtools_1["default"].fieldfmt(shdata["f2"], { num: 2, omit: false }) + "&nbsp;\n                            <span class=\"arru\">\u2191</span><span class=\"arrd\">\u2193</span>" + fieldtools_1["default"].fieldfmt(shdata["f4"], { num: 2 }) + "&nbsp;\n                            <span class=\"arru\">\u2191</span><span class=\"arrd\">\u2193</span>" + fieldtools_1["default"].fieldfmt(shdata["f3"], { num: 2 }) + "%&nbsp;\n                            " + fieldtools_1["default"].fieldfmt(shdata["f6"], { num: 2, fix: "元" }) + "\n                        </b>\n                        (\u6DA8:<a href=\"//quote.eastmoney.com/center/list.html#10_0_0_u?sortType=C&amp;sortRule=-1\" class=\"red\">\n                        <b>" + shdata["f104"] + "</b>\n                        </a>\u5E73:\n                        <b>" + shdata["f106"] + "</b>\n                        \u8DCC:<a href=\"//quote.eastmoney.com/center/gridlist.html?st=ChangePercent&sr=1#hs_a_board\"  class=\"green\">\n                        <b>" + shdata["f105"] + "</b>\n                        </a>)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n                        <a href=\"//quote.eastmoney.com/unify/r/0.399001\" class=\"blue\">\n                        <b>\u6DF1\u8BC1</b>\n                        </a>:\n                        <b class=\"" + (szdata["f4"] > 0 ? "red" : (szdata["f4"] < 0 ? "green" : "")) + "\">\n                            " + fieldtools_1["default"].fieldfmt(szdata["f2"], { num: 2, omit: false }) + "&nbsp;\n                            <span class=\"arru\">\u2191</span><span class=\"arrd\">\u2193</span>" + fieldtools_1["default"].fieldfmt(szdata["f4"], { num: 2 }) + "&nbsp;\n                            <span class=\"arru\">\u2191</span><span class=\"arrd\">\u2193</span>" + fieldtools_1["default"].fieldfmt(szdata["f3"], { num: 2 }) + "%&nbsp;\n                            " + fieldtools_1["default"].fieldfmt(szdata["f6"], { num: 2, fix: "元" }) + "\n                        </b>\n                        (\u6DA8:<a href=\"//quote.eastmoney.com/center/list.html#10_0_0_u?sortType=C&amp;sortRule=-1\" class=\"red\">\n                        <b>" + szdata["f104"] + "</b>\n                        </a>\u5E73:\n                        <b>" + szdata["f106"] + "</b>\u8DCC:<a href=\"//quote.eastmoney.com/center/gridlist.html?st=ChangePercent&sr=1#hs_a_board\"  class=\"green\"><b>\n                        " + szdata["f105"] + "</b>\n                        </a>)";
                            $(".zjlx_tophqnum").html(_html);
                        }
                    });
                },
                drawchart_fs: function (chart, json, hidetitle) {
                    var kline_data = json && json.data && json.data.klines;
                    var periods = json && json.data && json.data.tradePeriods && json.data.tradePeriods.periods;
                    if (kline_data && kline_data.length > 0 && periods) {
                        $(chart._dom).removeClass("chart1_nodata");
                        var times = getPeriods(periods);
                        var data1_1 = [];
                        var data2_1 = [];
                        var data3_1 = [];
                        var data4_1 = [];
                        var data5_1 = [];
                        kline_data.forEach(function (data) {
                            var arr = data && data.split(',');
                            if (arr && arr.length >= 6) {
                                data1_1.push(arr[1]);
                                data2_1.push(arr[2]);
                                data3_1.push(arr[3]);
                                data4_1.push(arr[4]);
                                data5_1.push(arr[5]);
                            }
                        });
                        var option = {
                            title: {
                                show: !hidetitle,
                                subtext: '单位:元',
                                left: 'right',
                                subtextStyle: { color: "#000" }
                            },
                            tooltip: {
                                confine: true,
                                trigger: 'axis',
                                formatter: function (data) {
                                    var str = "";
                                    var date = data[0].axisValue;
                                    for (var i = 0; i < data.length; i++) {
                                        if (data[i].value) {
                                            str += data[i].marker + data[i].seriesName + ':' + fieldtools_1["default"].fieldfmt(data[i].value, { num: 4 }) + '<br/>';
                                        }
                                    }
                                    return date + '<br/>' + str;
                                }
                            },
                            legend: {
                                right: 20,
                                show: false
                            },
                            grid: {
                                top: hidetitle ? '10' : '40',
                                left: '5',
                                right: '20',
                                bottom: '10',
                                containLabel: true
                            },
                            xAxis: [{
                                type: 'category',
                                data: times,
                                axisTick: { show: false },
                                boundaryGap: false,
                                axisLine: {
                                    onZero: false,
                                    lineStyle: {
                                        color: "#999"
                                    }
                                },
                                axisLabel: {
                                    textStyle: {
                                        color: '#000'
                                    },
                                    interval: function (index, value) {
                                        return (value == "09:31" || value == "10:30" || value == "11:30" || value == "14:00" || value == "15:00");
                                    }
                                },
                                splitLine: {
                                    show: true,
                                    lineStyle: {
                                        type: 'dotted',
                                        color: "#dde9f3"
                                    }
                                }
                            },
                                {
                                    type: 'category',
                                    axisLine: {
                                        onZero: false,
                                        lineStyle: {
                                            color: "#999"
                                        }
                                    }
                                }],
                            yAxis: [
                                {
                                    type: 'value',
                                    axisTick: { show: false },
                                    axisLine: {
                                        lineStyle: {
                                            color: "#999",
                                            width: 1
                                        }
                                    },
                                    axisLabel: {
                                        textStyle: {
                                            color: '#000'
                                        },
                                        formatter: function (value, index) {
                                            return fieldtools_1["default"].fieldfmt(value, { num: 1, trim: true });
                                        }
                                    },
                                    splitLine: {
                                        show: true,
                                        lineStyle: {
                                            type: 'dotted',
                                            color: "#dde9f3"
                                        }
                                    }
                                },
                                {
                                    type: 'value',
                                    axisLine: {
                                        lineStyle: {
                                            color: "#999"
                                        }
                                    }
                                }
                            ],
                            series: [
                                {
                                    name: "主力净流入",
                                    data: data1_1,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: false,
                                    itemStyle: {
                                        color: "#FE3EE1"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line' //,
                                    // markLine: {
                                    //     symbol: "none",
                                    //     lineStyle: {
                                    //         type: 'dotted',
                                    //         color: "#aaa"
                                    //     },
                                    //     data: [
                                    //         {
                                    //             name: '0的水平线',
                                    //             yAxis: 0
                                    //         }
                                    //     ]
                                    // }
                                },
                                {
                                    name: "超大单净流入",
                                    data: data5_1,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: false,
                                    itemStyle: {
                                        color: "#650000"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                },
                                {
                                    name: "大单净流入",
                                    data: data4_1,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: false,
                                    itemStyle: {
                                        color: "#FF1117"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                },
                                {
                                    name: "中单净流入",
                                    data: data3_1,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: false,
                                    itemStyle: {
                                        color: "#FFB83D"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                },
                                {
                                    name: "小单净流入",
                                    data: data2_1,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: false,
                                    itemStyle: {
                                        color: "#94C4EE"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                }
                            ]
                        };
                        chart.setOption(option);
                    }
                    else {
                        $(chart._dom).addClass("chart1_nodata");
                    }
                },
                drawchart_cjfb: function (chart, data) {
                    if (data["f64"] + data["f70"] + data["f76"] + data["f82"] == 0) {
                        $(chart._dom).addClass("chart2_nodata");
                        return;
                    }
                    $(chart._dom).removeClass("chart2_nodata");
                    var option = {
                        title: {
                            subtext: '单位:元',
                            left: 'right',
                            subtextStyle: { color: "#000" }
                        },
                        color: ['#004800', '#0a820a', '#27b729', '#77e97a', '#ff8080', '#f83434', '#ae0000', '#650000'],
                        tooltip: {
                            confine: true,
                            trigger: 'item',
                            formatter: function (data) {
                                return data.marker + data.name + ':' + fieldtools_1["default"].fieldfmt(data.value, { num: 4 });
                            }
                        },
                        series: [
                            {
                                type: 'pie',
                                label: {
                                    position: "outside",
                                    show: true,
                                    color: "#000",
                                    formatter: '{b}'
                                },
                                radius: '70%',
                                center: ['50%', '50%'],
                                data: [
                                    { value: data["f65"], name: '超大单流出' },
                                    { value: data["f71"], name: '大单流出' },
                                    { value: data["f77"], name: '中单流出' },
                                    { value: data["f83"], name: '小单流出' },
                                    { value: data["f82"], name: '小单流入' },
                                    { value: data["f76"], name: '中单流入' },
                                    { value: data["f70"], name: '大单流入' },
                                    { value: data["f64"], name: '超大单流入' },
                                ]
                            }
                        ]
                    };
                    chart.setOption(option);
                },
                drawchart_phqs: function (chart, json, hidetitle) {
                    var kline_data = json && json.data && json.data.klines;
                    var len = (kline_data === null || kline_data === void 0 ? void 0 : kline_data.length) || 0;
                    if (kline_data && kline_data.length > 0) {
                        var kline_chart_data = kline_data.slice(-42); //从倒数第42到最后一个
                        var times = [];
                        var data1 = [];
                        var data2 = [];
                        var data3 = [];
                        var data4 = [];
                        var data5 = [];
                        for (var i = 0; i < kline_chart_data.length; i++) {
                            var data = kline_chart_data[i];
                            var arr = data && data.split(',');
                            if (arr && arr.length >= 6) {
                                times.push(fieldtools_1["default"].dateformat(arr[0], "MM-DD"));
                                data1.push(arr[1]);
                                data2.push(arr[2]);
                                data3.push(arr[3]);
                                data4.push(arr[4]);
                                data5.push(arr[5]);
                            }
                        }
                        var showSymbol = len === 1;
                        var option = {
                            title: {
                                show: !hidetitle,
                                subtext: '单位:元',
                                left: 'right',
                                subtextStyle: { color: "#000" }
                            },
                            tooltip: {
                                confine: true,
                                trigger: 'axis',
                                formatter: function (data) {
                                    var str = "";
                                    var date = data[0].axisValue;
                                    for (var i = 0; i < data.length; i++) {
                                        if (data[i].value) {
                                            str += data[i].marker + data[i].seriesName + ':' + fieldtools_1["default"].fieldfmt(data[i].value, { num: 4 }) + '<br/>';
                                        }
                                    }
                                    return date + '<br/>' + str;
                                }
                            },
                            legend: {
                                right: 20,
                                show: false
                            },
                            grid: {
                                top: hidetitle ? '10' : '40',
                                left: '5',
                                right: '25',
                                bottom: '10',
                                containLabel: true
                            },
                            xAxis: [{
                                type: 'category',
                                data: times,
                                boundaryGap: false,
                                axisTick: { show: false },
                                position: "bottom",
                                axisLine: {
                                    onZero: false,
                                    lineStyle: {
                                        color: "#999"
                                    }
                                },
                                axisLabel: {
                                    textStyle: {
                                        color: '#000'
                                    },
                                    showMaxLabel: true
                                },
                                splitLine: {
                                    show: true,
                                    lineStyle: {
                                        type: 'dotted',
                                        color: "#dde9f3"
                                    }
                                }
                            },
                                {
                                    type: 'category',
                                    axisLine: {
                                        onZero: false,
                                        lineStyle: {
                                            color: "#999"
                                        }
                                    }
                                }],
                            yAxis: [
                                {
                                    type: 'value',
                                    axisTick: { show: false },
                                    axisLabel: {
                                        textStyle: {
                                            color: '#000'
                                        },
                                        formatter: function (value, index) {
                                            return fieldtools_1["default"].fieldfmt(value, { num: 1, trim: true });
                                        }
                                    },
                                    splitLine: {
                                        show: true,
                                        lineStyle: {
                                            type: 'dotted',
                                            color: "#dde9f3"
                                        }
                                    },
                                    axisLine: {
                                        lineStyle: {
                                            color: "#999"
                                        }
                                    }
                                },
                                {
                                    type: 'value',
                                    axisLine: {
                                        lineStyle: {
                                            color: "#999"
                                        }
                                    }
                                }
                            ],
                            series: [
                                {
                                    name: "主力净流入",
                                    data: data1,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: showSymbol,
                                    itemStyle: {
                                        color: "#FE3EE1"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    // markLine: {
                                    //     symbol: "none",
                                    //     lineStyle: {
                                    //         type: 'dotted',
                                    //         color: "#aaa"
                                    //     },
                                    //     data: [
                                    //         {
                                    //             name: '0的水平线',
                                    //             yAxis: 0
                                    //         }
                                    //     ]
                                    // },
                                    type: 'line'
                                },
                                {
                                    name: "超大单净流入",
                                    data: data5,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: showSymbol,
                                    itemStyle: {
                                        color: "#650000"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                },
                                {
                                    name: "大单净流入",
                                    data: data4,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: showSymbol,
                                    itemStyle: {
                                        color: "#FF1117"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                },
                                {
                                    name: "中单净流入",
                                    data: data3,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: showSymbol,
                                    itemStyle: {
                                        color: "#FFB83D"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                },
                                {
                                    name: "小单净流入",
                                    data: data2,
                                    connectNulls: true,
                                    symbol: "circle",
                                    showSymbol: showSymbol,
                                    itemStyle: {
                                        color: "#94C4EE"
                                    },
                                    lineStyle: {
                                        width: 1
                                    },
                                    type: 'line'
                                }
                            ]
                        };
                        chart.setOption(option);
                    }
                },
                drawchart_fhtj: function (chart, data) {
                    var option = {
                        title: {
                            subtext: '单位:元',
                            left: 'right',
                            subtextStyle: { color: "#000" }
                        },
                        legend: {
                            show: false,
                        },
                        grid: {
                            top: 40,
                            right: 30,
                            bottom: 10,
                            left: 15,
                            containLabel: true,
                        },
                        tooltip: {
                            confine: true,
                            trigger: 'axis',
                            axisPointer: {
                                type: 'shadow'
                            },
                            formatter: function (data) {
                                var str = "";
                                var date = data[0].axisValue;
                                for (var i = 0; i < data.length; i++) {
                                    if (data[i].value) {
                                        str += data[i].marker + data[i].seriesName + ':' + fieldtools_1["default"].fieldfmt(data[i].value, { num: 4 }) + '<br/>';
                                    }
                                }
                                return date + '<br/>' + str;
                            }
                        },
                        xAxis: [{
                            type: 'value',
                            axisLabel: {
                                formatter: function (value, index) {
                                    return fieldtools_1["default"].fieldfmt(value, { trim: true });
                                }
                            },
                            axisTick: { show: false },
                            position: "bottom",
                            splitLine: {
                                show: true,
                                lineStyle: {
                                    type: 'dotted',
                                    color: "#dde9f3"
                                }
                            },
                            axisLine: {
                                lineStyle: {
                                    color: "#999"
                                }
                            }
                        },
                            {
                                type: 'value',
                                position: "top",
                                axisLine: {
                                    lineStyle: {
                                        color: "#999"
                                    }
                                }
                            }],
                        yAxis: [{
                            type: 'category',
                            data: ['一月(20日)\n资金流入', '一周(5日)\n资金流入'],
                            splitLine: {
                                show: true,
                                lineStyle: {
                                    type: 'dotted',
                                    color: "#dde9f3"
                                }
                            },
                            axisTick: { show: false },
                            axisLine: {
                                lineStyle: {
                                    color: "#999"
                                }
                            }
                        },
                            {
                                type: 'category',
                                position: "left",
                                axisLine: {
                                    onZero: false,
                                    lineStyle: {
                                        color: "#999"
                                    }
                                }
                            },
                            {
                                type: 'category',
                                position: "right",
                                axisLine: {
                                    onZero: false,
                                    lineStyle: {
                                        color: "#999"
                                    }
                                }
                            }],
                        series: [
                            {
                                name: "主力净流入",
                                type: 'bar',
                                color: '#FE3EE1',
                                data: [data["f252"], data["f278"]]
                            },
                            {
                                name: "超大单净流入",
                                type: 'bar',
                                color: '#650000',
                                data: [data["f253"], data["f279"]]
                            },
                            {
                                name: "大单净流入",
                                type: 'bar',
                                color: '#FF1117',
                                data: [data["f254"], data["f280"]]
                            },
                            {
                                name: "中单净流入",
                                type: 'bar',
                                color: '#FFB83D',
                                data: [data["f255"], data["f281"]]
                            },
                            {
                                name: "小单净流入",
                                type: 'bar',
                                color: '#94C4EE',
                                data: [data["f256"], data["f282"]]
                            }
                        ]
                    };
                    chart.setOption(option);
                },
                drawchart_bk: function (chart, json, key, change) {
                    key = key || "f62";
                    var data = json && json.data && json.data.diff;
                    if (data) {
                        var names = [], ydatas = [];
                        for (var i = 0; i < data.length; i++) {
                            var item = data[i];
                            names.push(item["f14"]);
                            ydatas.push(item[key]);
                            objs[item["f14"]] = item;
                        }
                        if (change) {
                            chart.setOption({
                                xAxis: { data: names }, series: [{
                                    name: "主力净流入", data: ydatas.map(function (num) {
                                        return num > 0 ? num : 0;
                                    })
                                }, {
                                    name: "主力净流出", data: ydatas.map(function (num) {
                                        return num < 0 ? num : 0;
                                    })
                                }]
                            });
                        }
                        else {
                            var option = {
                                title: {
                                    top: -8,
                                    left: 33,
                                    subtext: "单位:元",
                                    subtextStyle: {
                                        color: "#000"
                                    }
                                },
                                tooltip: {
                                    trigger: 'axis',
                                    formatter: function (data) {
                                        var str = "";
                                        var date = data[0].axisValue;
                                        var value = 0;
                                        for (var i = 0; i < data.length; i++) {
                                            if (!fieldtools_1["default"].isEmpty(data[i].value)) {
                                                value += data[i].value;
                                            }
                                        }
                                        str += "<span style=\"display:inline-block;margin-right:5px;border-radius:10px;width:10px;\n                            height:10px;background-color:" + (value >= 0 ? "#FF3F3E" : "#06960A") + ";\"></span><span style =\"\n                            display:inline-block;width:155px;\" >" + (value >= 0 ? "主力净流入" : "主力净流出") + ":" + fieldtools_1["default"].fieldfmt(value) + "</span>";
                                        return date + '<br/>' + str;
                                    },
                                    confine: true
                                },
                                dataZoom: [
                                    {
                                        type: 'slider',
                                        show: true,
                                        startValue: 0,
                                        endValue: 31,
                                        zoomLock: true
                                    }
                                ],
                                legend: {
                                    show: true,
                                    selectedMode: false,
                                    left: "right",
                                    data: [{ name: "主力净流入" }, { name: "主力净流出" }]
                                },
                                grid: {
                                    left: 20,
                                    right: 10,
                                    top: 40,
                                    bottom: 50,
                                    containLabel: true
                                },
                                xAxis: {
                                    type: 'category',
                                    triggerEvent: true,
                                    data: names,
                                    axisTick: { show: false },
                                    splitLine: {
                                        show: false
                                    },
                                    axisLabel: {
                                        interval: 0,
                                        textStyle: {
                                            color: '#00298F'
                                        },
                                        formatter: function (value) {
                                            var str = value.split("");
                                            return str.join("\n");
                                        }
                                    }
                                },
                                yAxis: [
                                    {
                                        type: 'value',
                                        //scale: true,
                                        axisTick: { show: false },
                                        axisLabel: {
                                            formatter: function (value, index) {
                                                return fieldtools_1["default"].fieldfmt(value, { num: 1 });
                                            }
                                        },
                                        splitLine: {
                                            show: true,
                                            lineStyle: {
                                                type: 'dotted',
                                                color: "#E6E6E6"
                                            }
                                        }
                                    }
                                ],
                                series: [
                                    {
                                        stack: '主力净额',
                                        name: "主力净流入",
                                        type: 'bar',
                                        color: "#FF3F3E",
                                        barWidth: 15,
                                        label: {
                                            normal: {
                                                show: true,
                                                position: 'top',
                                                formatter: function (value, index) {
                                                    return Math.abs(value.data) < 50000000 ? "" : fieldtools_1["default"].fieldfmt(value.data, { num: 1, zoom: -8, fix: "亿" });
                                                },
                                                fontSize: 8
                                            }
                                        },
                                        data: ydatas.map(function (num) {
                                            return num > 0 ? num : 0;
                                        })
                                    },
                                    {
                                        stack: '主力净额',
                                        name: "主力净流出",
                                        type: 'bar',
                                        color: "#06960A",
                                        barWidth: 15,
                                        label: {
                                            normal: {
                                                show: true,
                                                position: 'bottom',
                                                formatter: function (value, index) {
                                                    return Math.abs(value.data) < 50000000 ? "" : fieldtools_1["default"].fieldfmt(value.data, { num: 1, zoom: -8, fix: "亿" });
                                                },
                                                fontSize: 8
                                            }
                                        },
                                        data: ydatas.map(function (num) {
                                            return num < 0 ? num : 0;
                                        })
                                    }
                                ]
                            };
                            chart.setOption(option);
                            chart.off('click');
                            chart.on('click', function (params) {
                                if (params.componentType == "xAxis")
                                    window.open('/bkzj/' + objs[params.value]["f12"] + '.html');
                            });
                        }
                    }
                },
                loaddatanum: function (data) {
                    $("td[data-field],span[data-field]").each(function () {
                        var ele = $(this);
                        var key = ele.data("field");
                        var fix = ele.data("fix");
                        var sc = ele.data("showcolor") === undefined ? true : false;
                        var num = fix == "%" ? 2 : 4;
                        if (data[key] != undefined) {
                            ele.html(fieldtools_1["default"].fieldfmt(data[key], { num: num, fix: fix, showcolor: sc }));
                        }
                    });
                    $(".hqtime").html(fieldtools_1["default"].dateformat(data["f124"] * 1000, "HH:mm"));
                },
                loadlstable: function (tbody, json, type) {
                    var kline_data = json && json.data && json.data.klines;
                    if (kline_data && kline_data.length > 0) {
                        for (var i = kline_data.length - 1; i >= 0; i--) {
                            var data = kline_data[i];
                            var arr = data && data.split(',');
                            if (arr && arr.length >= 15) {
                                if (i == kline_data.length - 1) {
                                    $(".hqtime_ph").html(fieldtools_1["default"].dateformat(arr[0]));
                                }
                                var tr = "<tr>";
                                tr += "<td>" + fieldtools_1["default"].dateformat(arr[0]) + "</td>";
                                if (type != "bk") {
                                    tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[11], { num: 2, omit: false, trim: false, showcolor: true, colorvalue: arr[12] }) + "</td>";
                                    tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[12], { num: 2, fix: "%", showcolor: true }) + "</td>";
                                }
                                if (type == "沪深两市") {
                                    tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[13], { num: 2, omit: false, trim: false, showcolor: true, colorvalue: arr[14] }) + "</td>";
                                    tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[14], { num: 2, fix: "%", showcolor: true }) + "</td>";
                                }
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[1], { num: 2, showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[6], { num: 2, fix: "%", showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[5], { num: 2, showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[10], { num: 2, fix: "%", showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[4], { num: 2, showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[9], { num: 2, fix: "%", showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[3], { num: 2, showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[8], { num: 2, fix: "%", showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[2], { num: 2, showcolor: true }) + "</td>";
                                tr += "<td>" + fieldtools_1["default"].fieldfmt(arr[7], { num: 2, fix: "%", showcolor: true }) + "</td>";
                                tr += "</tr>";
                                tbody.append(tr);
                            }
                        }
                    }
                },
                getmkdata: function (hqcode) {
                    var data = {
                        lmt: "0",
                        klt: "1",
                        fields1: "f1,f2,f3,f7",
                        fields2: "f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65",
                        ut: "b2884a393a59ad64002292a3e90d46a5"
                    };
                    if (typeof (hqcode) == "string")
                        data.secid = hqcode;
                    else {
                        data.secid = hqcode[0];
                        data.secid2 = hqcode[1];
                    }
                    return $.ajax({
                        url: webconfig.getWebPath('quoteurl') + 'api/qt/stock/fflow/kline/get',
                        type: 'GET',
                        dataType: 'jsonp',
                        jsonp: 'cb',
                        data: data,
                    });
                },
                getdkdata: function (hqcode) {
                    var data = {
                        lmt: "0",
                        klt: "101",
                        fields1: "f1,f2,f3,f7",
                        fields2: "f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65",
                        ut: "b2884a393a59ad64002292a3e90d46a5"
                    };
                    if (typeof (hqcode) == "string")
                        data.secid = hqcode;
                    else {
                        data.secid = hqcode[0];
                        data.secid2 = hqcode[1];
                    }
                    return $.ajax({
                        url: webconfig.getWebPath('quotehisurl') + 'api/qt/stock/fflow/daykline/get',
                        type: 'GET',
                        dataType: 'jsonp',
                        jsonp: 'cb',
                        data: data,
                    });
                },
                getnumdata: function (hqcode) {
                    return $.ajax({
                        url: webconfig.getWebPath('quoteurl') + 'api/qt/ulist.np/get',
                        type: 'GET',
                        dataType: 'jsonp',
                        jsonp: 'cb',
                        data: {
                            fltt: "2",
                            secids: hqcode,
                            fields: "f62,f184,f66,f69,f72,f75,f78,f81,f84,f87,f64,f65,f70,f71,f76,f77,f82,f83,f164,f166,f168,f170,f172,f252,f253,f254,f255,f256,f124,f6,f278,f279,f280,f281,f282",
                            ut: "b2884a393a59ad64002292a3e90d46a5"
                        },
                    });
                },
                getbkdata: function (code, key) {
                    key = key || "f62";
                    return $.ajax({
                        url: webconfig.getWebPath("url") + "/dataapi/bkzj/getbkzj",
                        type: 'GET',
                        data: {
                            key: key,
                            code: code,
                        },
                    });
                },
                makedata: function (res) {
                    var _a, _b;
                    (_b = (_a = res === null || res === void 0 ? void 0 : res.data) === null || _a === void 0 ? void 0 : _a.diff) === null || _b === void 0 ? void 0 : _b.forEach(function (item) {
                        Object.keys(item).forEach(function (key) {
                            item[mapping[key]] = item[key];
                        });
                    });
                    return res;
                },
                upquerystat: function (options, queryparams) {
                    if (queryparams.data && queryparams.data.fid && queryparams.data.fields) {
                        Object.keys(mapping).forEach(function (key) {
                            if (queryparams.data.fid == mapping[key] && queryparams.data.fields.indexOf(key + ',') > -1)
                                queryparams.data.fid = key;
                        });
                    }
                    return queryparams;
                }
            };
            function getPeriods(periods) {
                var ret = [];
                for (var i = 0; i < periods.length; i++) {
                    var item = periods[i];
                    for (var t = item.b + 1; t <= item.e; t++) {
                        if (t % 100 >= 60)
                            continue;
                        var date_str = t.toString();
                        var time = date_str.substring(8, 10) + ':' + date_str.substring(10);
                        ret.push(time);
                    }
                }
                return ret;
            }
            window["pageopt"] = pageopt;
            exports["default"] = pageopt;


            /***/ }),

        /***/ "./jssrc/zjlx/stock.ts":
        /*!*****************************!*\
          !*** ./jssrc/zjlx/stock.ts ***!
          \*****************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            exports.__esModule = true;
            /**
             * 个股资金流向页
             */
            __webpack_require__(/*! ../../src/modules/singlestocktop/web */ "./src/modules/singlestocktop/web.ts");
            var base_1 = __importDefault(__webpack_require__(/*! ./base */ "./jssrc/zjlx/base.ts"));
            var chart1 = echarts.init(document.getElementById("chart1"));
            var chart2 = echarts.init(document.getElementById("chart2"));
            var chart3 = echarts.init(document.getElementById("chart3"));
            var chart4 = echarts.init(document.getElementById("chart4"));
            base_1["default"].tophqnum();
//加载页面中图
            loadchart();
//设置页面自刷
            setdatarefresh();
            function setdatarefresh() {
                //图10秒刷新
                window.setIntervalInTrade(function () {
                    loadchart1();
                    loadchart2();
                }, 10 * 1000);
                //数字10秒刷新
                window.setIntervalInTrade(base_1["default"].tophqnum, 30 * 1000);
            }
            function loadchart() {
                loadchart1();
                loadchart2();
                loadchart3();
            }
            function loadchart1() {
                base_1["default"].getmkdata(stockInfo.hqCode).then(function (json) {
                    base_1["default"].drawchart_fs(chart1, json);
                });
            }
            function loadchart2() {
                base_1["default"].getnumdata(stockInfo.hqCode).then(function (json) {
                    var data = json && json.data && json.data.diff && json.data.diff[0];
                    if (data) {
                        base_1["default"].drawchart_cjfb(chart2, data);
                        base_1["default"].drawchart_fhtj(chart4, data);
                        base_1["default"].loaddatanum(data);
                    }
                });
            }
            function loadchart3() {
                base_1["default"].getdkdata(stockInfo.hqCode).then(function (json) {
                    base_1["default"].drawchart_phqs(chart3, json);
                    base_1["default"].loadlstable($("#table_ls tbody"), json);
                });
            }


            /***/ }),

        /***/ "./src/modules/commonutil/fieldtools.ts":
        /*!**********************************************!*\
          !*** ./src/modules/commonutil/fieldtools.ts ***!
          \**********************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            /**
             * 数据字段常用公用方法
             */
            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            exports.__esModule = true;
            var moment_1 = __importDefault(__webpack_require__(/*! moment */ "moment"));
            var weeks = ['日', '一', '二', '三', '四', '五', '六'];
            var quarters = ['1', '1', '1', '2', '2', '2', '3', '3', '3', '4', '4', '4'];
            var quarters_z = ['一', '一', '一', '二', '二', '二', '三', '三', '三', '四', '四', '四'];
            var quarters0 = ['1', '1', '1', '1-2', '1-2', '1-2', '1-3', '1-3', '1-3', '1-4', '1-4', '1-4'];
            var quarters0_z = ['一', '一', '一', '一-二', '一-二', '一-二', '一-三', '一-三', '一-三', '一-四', '一-四', '一-四'];
            var reports = ['1季', '1季', '1季', '中', '中', '中', '3季', '3季', '3季', '年', '年', '年'];
            var reports_z = ['一季', '一季', '一季', '中', '中', '中', '三季', '三季', '三季', '年', '年', '年'];
            var defaultfmtconfig = {
                def: '-',
                omit: true,
                trim: undefined,
                strfmt: undefined,
                num: 432,
                smart: false,
                fix: '',
                zoom: 0,
                showcolor: false,
                showabs: false,
                qfw: false,
                colorfmt: '<span class="{color}">{value}</span>',
                gtcolor: 'red',
                ltcolor: 'green',
                colorzero: 0
            };
            exports["default"] = {
                /**
                 * 数据字段展示格式化方法(支持数字和日期处理)
                 *@param {string} value:字段,一般为字符串或数字
                 *@param {string} config:配置数据
                 *@param {fmtconfig} config {

   ```
            def?: string;// 缺省值默认 ‘-’ 优先级最高，不受其他参数影响
            datefmt?: string;//日期展示格式，如果有值的话做日期处理，有值的话除def外其他字段全部作废 优先级小于def (详见日期处理方法dateformat)
            strfmt?: any;//作为字符串处理的参数，如果有值的话做字符串处理，优先级小于def,datefmt(详见字符串处理方法strformat)
            omit?: boolean;//是否自动改为省略格式【xxxx万|xxxx亿|xxxx万亿】默认true
            trim?: boolean;//是否自动省略末尾的0，默认不省略,smart有值时候则默认为true，可单独配置
            num?: any;//保留小数位，默认值为 432（3位数保留一位小数，2位和1位数保留两位小数），可以设置保留小数规则（"5421" 代表
                五位数字及以上保存0位小数，
                四位数字及以上保留一位小数，
                二位数字及以上保留2位小数，
                一位数字保留3位小数）
            smart?: number;//是否自动精简结果【最多保留多少位有效数字】配合omit=true时使用 有值的话小数位数小于num，>1时生效
            fix?: string;//显示单位 默认空   举例 【‘%’，‘只’】
            zoom?: number;//缩放倍数（指数幂） 默认0【不放大+】  举例 【1就是放大10倍】
            showabs?: boolean//是否展示为绝对值 默认不展示
            showcolor?: boolean//是否展示颜色 默认不展示
            colorfmt?: string;//带颜色的返回模板 默认<span class="{color}">{value}</span>
            gtcolor?: string;//大于标准值的样式 默认red
            ltcolor?: string;//小于标准值的样式 默认green
            colorvalue?: number;//颜色比较使用的值，默认使用value字段
            colorzero?: number;//颜色比较标准值 默认0  与零比较

   ```
        }
                 **/
                fieldfmt: function (value, config) {
                    if (this.isEmpty(value)) {
                        return (config && config.def != undefined) ? config.def : defaultfmtconfig.def;
                    }
                    //无配置时尝试按数据类型转换
                    if ($.isEmptyObject(config)) {
                        if (/^\d{2,4}(\-|\/|\.)\d{1,2}\1\d{1,2}|^\d{13}$/.test(value)) { // 有bug
                            return this.dateformat(value, 'YYYY-MM-DD');
                        }
                        if (!$.isNumeric(value)) {
                            return value;
                        }
                    }
                    config = $.extend({}, defaultfmtconfig, config);
                    if (config && config.datefmt) {
                        return this.dateformat(value, config.datefmt, config.def);
                    }
                    if (config && config.strfmt) {
                        return this.strformat(value, config.strfmt, config.def);
                    }
                    if (isNaN(value) || !isFinite(value)) {
                        return config.def;
                    }
                    var oldvalue = parseFloat(value);
                    var fix = "";
                    var num = 2;
                    //@ts-ignore
                    value = oldvalue * Math.pow(10, config.zoom).toFixed(Math.abs(config.zoom));
                    if (config.showabs)
                        value = Math.abs(value);
                    var f = config.trim ? parseFloat : function (v) { return v; };
                    var abslength = Math.abs(parseInt(value)).toString().length;
                    if (config.omit) {
                        if (abslength > 12) {
                            value = value / 1000000000000;
                            abslength -= 12;
                            fix = '万亿';
                        }
                        else if (abslength > 8) {
                            value = value / 100000000;
                            abslength -= 8;
                            fix = '亿';
                        }
                        else if (abslength > 4) {
                            value = value / 10000;
                            abslength -= 4;
                            fix = '万';
                        }
                    }
                    if (-1 < value && value < 1)
                        abslength = 0;
                    if (config.smart) {
                        f = parseFloat;
                        config.num = "43220";
                    }
                    if ($.isNumeric(config.num) && config.num < 10)
                        num = config.num;
                    else {
                        var numstr = config.num.toString();
                        num = numstr.length - 1;
                        for (var i = 0; i < numstr.length; i++) {
                            if (abslength >= numstr[i]) {
                                num = i;
                                break;
                            }
                        }
                    }
                    value = f(this.toFixed(value, num));
                    value += fix + config.fix;
                    if (config.showcolor) {
                        var bz = config.colorvalue;
                        if (!$.isNumeric(bz))
                            bz = oldvalue;
                        var color = bz == config.colorzero ? '' : bz > config.colorzero ? config.gtcolor : config.ltcolor;
                        //修正数字过小造成的正负号丢失问题
                        if (oldvalue < 0 && config.showabs == false && value[0] != '-')
                            value = '-' + value;
                        value = config.colorfmt.replace('{color}', color).replace('{value}', value);
                    }
                    if (config.qfw)
                        return value.replace(/\d+/, function (n) {
                            return n.replace(/(\d)(?=(\d{3})+$)/g, function ($1) {
                                return $1 + ",";
                            });
                        });
                    return value;
                },
                /**
                 * 日期字段展示格式化方法
                 * @param {string} str:字段值+
                 * @param {string} fmt：日期格式 根据nomentjs 文档http://momentjs.cn/docs/
                 *                  添加 {W:"星期，['日', '一', '二', '三', '四', '五', '六']
                 *                  添加 {Q|q:"季度，如3月为'一|1'；6月为'二|2'"；9月为'三|3'"；12月为'四|4'"}
                 *                  添加 {J|j:"季度跨度，如3月为'一|1'；6月为'一-二|1-2'"；9月为'一-三|1-3'"；12月为示'一-四|1-4'"}
                 *                  添加 {B|b:"报告期，如3月为'一季|1季'；6月为'中'"；9月为'三季|3季'"；12月为'年'"}
                 **/
                dateformat: function (str, fmt, def) {
                    if (fmt === void 0) { fmt = 'YYYY-MM-DD'; }
                    if (def === void 0) { def = '-'; }
                    if (this.isEmpty(str)) {
                        return def;
                    }
                    try {
                        if (/^\d*$/.test(str)) {
                            str = parseInt(str);
                        }
                        var ret = moment_1["default"](str);
                        if (ret.isValid()) {
                            var value = ret.format(fmt);
                            var o = {
                                "W": weeks[ret.day()],
                                "Q": quarters_z[ret.month()],
                                "q": quarters[ret.month()],
                                "J": quarters0_z[ret.month()],
                                "j": quarters0[ret.month()],
                                "B": reports_z[ret.month()],
                                "b": reports[ret.month()]
                            };
                            for (var k in o) {
                                if (new RegExp("(" + k + ")").test(value)) {
                                    value = value.replace(RegExp.$1, o[k]);
                                }
                            }
                            return value;
                        }
                        return def;
                    }
                    catch (err) {
                        return def;
                    }
                },
                /**
                 * 字符串展示格式化方法
                 * @param {string} str:字段值
                 * @param {any} fmt：格式化参数，数组或对象
                 **/
                strformat: function (str, fmt, def) {
                    if (def === void 0) { def = '-'; }
                    if (this.isEmpty(str)) {
                        return def;
                    }
                    str = str.toString();
                    if (typeof (fmt) == "object") {
                        if (fmt.len && fmt.ellipsis) {
                            str = this.cutstr(str, fmt.len, fmt.ellipsis, def);
                        }
                        for (var key in fmt) {
                            if (fmt[key] !== undefined) {
                                var reg = new RegExp("({" + key + "})", "g");
                                str = str.replace(reg, fmt[key]);
                            }
                        }
                    }
                    else if (Array.isArray(fmt)) {
                        fmt.forEach(function (s, i) {
                            var reg = new RegExp("({)" + i + "(})", "g");
                            str = str.replace(reg, s);
                        });
                    }
                    return str;
                },
                /**
                 * js截取字符串，中英文都能用
                 * @param {string} str: 需要截取的字符串
                 * @param {number} len: 需要截取的长度【中文算两个】
                 * @param {string} ellipsis: 溢出文字
                 * @returns {string} 截取后的字符串
                 */
                cutstr: function (str, len, ellipsis, def) {
                    if (def === void 0) { def = '-'; }
                    if (this.isEmpty(str)) {
                        return def;
                    }
                    if (typeof ellipsis != "string")
                        ellipsis = "...";
                    var str_length = 0;
                    var str_len = 0;
                    var a;
                    for (var i = 0; i < str.length; i++) {
                        a = str.charAt(i);
                        str_length++;
                        if (escape(a).length > 4) {
                            //中文字符的长度经编码之后大于4
                            str_length++;
                        }
                        if (str_length <= len) {
                            str_len++;
                        }
                    }
                    //如果给定字符串小于指定长度，则返回源字符串；
                    if (str_length <= len) {
                        return str.toString();
                    }
                    else {
                        return str.substr(0, str_len).concat(ellipsis);
                    }
                },
                /**
                 * html标签转义
                 * @param {string} str: 需要处理的字符串
                 * @returns {string} 处理后的字符串
                 */
                escapeHTML: function (text) {
                    if (typeof text === 'string') {
                        return text
                            .replace(/&/g, '&amp;')
                            .replace(/</g, '&lt;')
                            .replace(/>/g, '&gt;')
                            .replace(/"/g, '&quot;')
                            .replace(/'/g, '&#039;')
                            .replace(/`/g, '&#x60;');
                    }
                    return text;
                },
                /**
                 * 验证数据是否存在
                 * @param data
                 */
                isEmpty: function (data) {
                    if (data === '' || data === '-' || data === undefined || data === null || typeof data === "undefined" || data === false) {
                        return true;
                    }
                    return false;
                },
                /**
                 *
                 * 分割数据转json,三期|f10
                 */
                splitdatatojson: function (data, fields) {
                    //console.info(fields);
                    var ret = {};
                    var datas = [];
                    var SplitSymbol = data.SplitSymbol;
                    var fs = data.FieldName.split(',');
                    if (!fields)
                        fields = fs;
                    var _loop_1 = function () {
                        itemstr = data.Data[i];
                        var model = {};
                        var arr = itemstr.split(SplitSymbol);
                        arr.forEach(function (v, i) {
                            var field = fs[i];
                            if (fields.indexOf(field) > -1)
                                model[fs[i]] = v;
                        });
                        datas.push(model);
                    };
                    var itemstr;
                    for (var i = 0; i < data.Data.length; i++) {
                        _loop_1();
                    }
                    Object.keys(data).forEach(function (key) {
                        if (key != "Data")
                            ret[key] = data[key];
                    });
                    ret.datas = datas;
                    //console.info(ret);
                    return ret;
                },
                /**
                 *
                 * 数据转行情code(仅支持沪深京港)
                 * 数据平台接口尽量传对象使用
                 * 多层逻辑
                 * obj 情况 有 SECURITY_CODE(或其他),TRADE_MARKET_CODE
                 * obj 情况 有 SECUCODE
                 * obj 情况 只有 SECURITY_CODE
                 * string 情况  带市场格式   如   “605199.SH"
                 * string 情况  6位数字    如  "300059"
                 * string 情况  5位数字    如  "00001"
                 *
                 *
                 * 都不成立的情况用第二个参数（尽量不用）
                 */
                datatoquotecode: function (item, scode) {
                    if (scode === void 0) { scode = ''; }
                    if (typeof item === "object") {
                        var code = item.SECURITY_CODE || item.SECURITYCODE || item.SCODE || item.SCode || item.scode || item.code || item.securitycode || item.securityCode;
                        if (item.TRADE_MARKET_CODE && code.length && code.length == 6) {
                            //沪
                            if (item.TRADE_MARKET_CODE == "069001001001" || item.TRADE_MARKET_CODE == "069001001003" || item.TRADE_MARKET_CODE == "069001001006") {
                                return "1." + code;
                            }
                            //深
                            else if (item.TRADE_MARKET_CODE == "069001002001" || item.TRADE_MARKET_CODE == "069001002002" || item.TRADE_MARKET_CODE == "069001002005") {
                                return "0." + code;
                            }
                            //京
                            else if (item.TRADE_MARKET_CODE == "069001017") {
                                return "0." + code;
                            }
                            //新三板，老三版
                            else if (item.TRADE_MARKET_CODE == "069001004001" || item.TRADE_MARKET_CODE == "069001004002") {
                                return "0." + code;
                            }
                        }
                        if (item.SECUCODE) {
                            item = item.SECUCODE;
                        }
                        else if (code) {
                            item = code;
                        }
                        else {
                            item = scode;
                        }
                    }
                    if (typeof item === "string") {
                        var codes = item.split('.');
                        if (codes.length == 2) {
                            var mkt = '';
                            if (codes[1].toUpperCase() == "SH")
                                mkt = '1';
                            else if (codes[1].toUpperCase() == "SZ")
                                mkt = '0';
                            else if (codes[1].toUpperCase() == "BJ")
                                mkt = '0';
                            else if (codes[1].toUpperCase() == "NQ")
                                mkt = '0';
                            else if (codes[1].toUpperCase() == "HK")
                                mkt = '116';
                            return mkt + "." + codes[0];
                        }
                        if (/\d{6}/.test(item)) {
                            item = /\d{6}/.exec(item)[0];
                            var one = item.substr(0, 1);
                            var three = item.substr(0, 3);
                            if (one == "4" || one == "8" || three == "920") {
                                //沪
                                return "0." + item;
                            }
                            else if (one == "5" || one == "6" || one == "9") {
                                //京
                                return "1." + item;
                            }
                            else {
                                if (three == "009" || three == "126" || three == "110" || three == "201" || three == "202" || three == "203" || three == "204") {
                                    //沪
                                    return "1." + item;
                                }
                                else {
                                    //深   其它
                                    return "0." + item;
                                }
                            }
                        }
                        else if (/^\d{5}/.test(item)) {
                            item = /^\d{5}/.exec(item)[0];
                            //港
                            return "116." + item;
                        }
                    }
                },
                fmtsname: function (name) {
                    return "<span title=\"" + this.escapeHTML(this.strformat(name)) + "\">" + this.cutstr(name, 8) + "</span>";
                },
                getUrlParam: function (name) {
                    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
                    var r = window.location.search.substr(1).match(reg);
                    if (r != null)
                        return decodeURIComponent(r[2]);
                    return null;
                },
                toFixed: function (value, num) {
                    var power = Math.pow(10, num);
                    return (Math.round(value * power) / power).toFixed(num);
                },
                enhancedXSSCheck: function (input) {
                    if (typeof input !== 'string') {
                        return null;
                    }
                    // 检测HTML标签和script标签的正则表达式
                    var htmlTagRegex = /<[^>]+>/i;
                    var scriptTagRegex = /<script\b[^>]*>([\s\S]*?)<\/script>/gi;
                    // 检测到任何HTML标签或script标签就返回null
                    if (htmlTagRegex.test(input) || scriptTagRegex.test(input)) {
                        console.log(input);
                        return false;
                    }
                    // 安全的内容直接返回
                    return true;
                    // return xssPatterns.some(pattern => pattern.test(input)) ? false : true;
                }
            };


            /***/ }),

        /***/ "./src/modules/singlestocktop/web.ts":
        /*!*******************************************!*\
          !*** ./src/modules/singlestocktop/web.ts ***!
          \*******************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            exports.__esModule = true;
            /****
             * 前端个股单页头部通用组件
             *
             *  */
            var fieldtools_1 = __importDefault(__webpack_require__(/*! ../commonutil/fieldtools */ "./src/modules/commonutil/fieldtools.ts"));
            var webconfig = __webpack_require__(/*! ../../../config/web.js */ "./config/web.js");
            var code = stockInfo.code;
            var hqcode = stockInfo.hqCode;
            gettopqute();
            getstockdatastate();
            setInterval(gettopqute, 30000);
//获取当前个股数据状态信息,主要用于处理状态变化:  数据四期接口  周康
            function getstockdatastate() {
                $.ajax({
                    url: webconfig.getWebPath('datacenter') + 'api/data/v1/get',
                    type: 'GET',
                    dataType: 'jsonp',
                    data: {
                        reportName: 'RPT_STOCK_HEADERCHANGE',
                        columns: 'ALL',
                        filter: "(SECURITY_CODE=\"" + code + "\")",
                        pageNumber: 1,
                        pageSize: 10,
                        source: "WEB",
                        client: "WEB"
                    }
                }).then(function (json) {
                    var _a;
                    var list = ((_a = json === null || json === void 0 ? void 0 : json.result) === null || _a === void 0 ? void 0 : _a.data) || [];
                    if ((list === null || list === void 0 ? void 0 : list.length) > 0) {
                        var $box = $(".sinstock-filter-wrap");
                        var html = '';
                        var i = 0;
                        var item = list === null || list === void 0 ? void 0 : list[0];
                        item.BILLBOARD_CODE == 1 ? i = i + 1 : $('td[data-page="gglhb"]', $box).remove(); //龙虎榜单
                        item.BLOCKTRADE_CODE == 1 ? i = i + 1 : $('td[data-page="dzjy"]', $box).remove(); //大宗交易
                        item.MARGIN_CODE == 1 ? i = i + 1 : $('td[data-page="rzrq"]', $box).remove(); //融资融券
                        item.MUTUAL_CODE == 1 ? i = i + 1 : $('td[data-page="hsgtcg"]', $box).remove(); //沪深港通
                        var hyLink = "/report/industry.jshtml?hyid=" + item.INDUSTRY_CODE + "&hyname=" + item.INDUSTRY_NAME;
                        var htmls = [
                            '<td data-page="gdzjc"><a href="//data.eastmoney.com/executive/gdzjc/' + code + '.html">股东增减持</a></td>',
                            '<td data-page="hyyb"><a href="' + hyLink + '">行业研报</a></td>',
                            '<td data-page="wdm"><a href="//guba.eastmoney.com/qa/qa_search.aspx?company=' + code + '&keyword=&questioner=&qatype=1">问董秘</a></td>',
                            '<td data-page="zx"><a href="//so.eastmoney.com/web/s?keyword=' + code + '">资讯</a></td>'
                        ];
                        html = htmls.slice(0, 4 - i).join(' ');
                        $("tr", $box).eq(1).append(html);
                        //是否显示new标识
                        var new_img = '<span class="icon icon_new valign-ttop" alt="新"></span>';
                        item.BILLBOARD_CODE_NEW == 1 ? $('td[data-page="gglhb"] a', $box).append(new_img) : ""; //龙虎榜单
                        item.BLOCKTRADE_CODE_NEW == 1 ? $('td[data-page="dzjy"] a', $box).append(new_img) : ""; //大宗交易
                        item.NOTICE_CODE == 1 ? $('td[data-page="notices"] a', $box).append(new_img) : ""; //公告大全
                        item.HOLDER_SUM_CODE == 1 ? $('td[data-page="gdhs"] a', $box).append(new_img) : ""; //股东户数
                        item.ORG_SURVEY_CODE == 1 ? $('td[data-page="jgdy"] a', $box).append(new_img) : ""; //机构调研
                        item.GM_CODE == 1 ? $('td[data-page="gddh"] a', $box).append(new_img) : ""; //股东大会
                        item.EXEUTIVE_STOCK_CODE == 1 ? $('td[data-page="ggcg"] a', $box).append(new_img) : ""; //高管持股
                        item.SALE_LIFT_CODE == 1 ? $('td[data-page="xsjj"] a', $box).append(new_img) : ""; //限售解禁
                        item.STOCK_RESERCH_CODE == 1 ? $('td[data-page="ggyb"] a', $box).append(new_img) : ""; //个股研报
                        item.CAPSTOCK_STRUCTURE_CODE == 1 ? $('td[data-page="gbjg"] a', $box).append(new_img) : ""; //股本结构
                        item.HOLDER_CHANGE_CODE == 1 ? $('td[data-page="gdzjc"] a', $box).append(new_img) : ""; //股东增减持、
                        $('td[data-page="hsgtcg"]', $box).remove(); // 去掉沪深港通持股入口
                    }
                    else {
                        $('td[data-page="hsgtcg"]', $box).remove(); // 去掉沪深港通持股入口
                        // $("tr", $box).eq(1).append(`<td data-page="gdzjc"><a href="//data.eastmoney.com/executive/gdzjc/${code}.html">股东增减持</a></td>`);
                    }
                });
            }
//获取行情信息 ：行情接口 马文豹
            function gettopqute() {
                $.ajax({
                    url: webconfig.getWebPath('quoteurl') + 'api/qt/stock/get',
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonp: 'cb',
                    data: {
                        fltt: "2",
                        invt: "2",
                        secid: hqcode,
                        fields: "f57,f58,f43,f47,f48,f168,f169,f170,f152",
                        ut: "b2884a393a59ad64002292a3e90d46a5"
                    },
                }).then(function (json) {
                    if (json && (json === null || json === void 0 ? void 0 : json.data)) {
                        var data = json === null || json === void 0 ? void 0 : json.data;
                        $("#newPrice").html(fieldtools_1["default"].fieldfmt(data.f43, { showcolor: true, colorvalue: data.f169, num: data.f152 })); //最新价
                        $("#zd").html(fieldtools_1["default"].fieldfmt(data.f169, { showcolor: true, num: data.f152 })); //涨跌
                        $("#zdf").html(fieldtools_1["default"].fieldfmt(data.f170, { showcolor: true, num: data.f152, fix: "%" })); //涨跌幅
                        $("#hs").html(fieldtools_1["default"].fieldfmt(data.f168, { fix: "%", num: 2 })); //换手率
                        $("#sum").html(fieldtools_1["default"].fieldfmt(data.f47, { fix: "手", num: 2 })); //总手
                        $("#totalPrice").html(fieldtools_1["default"].fieldfmt(data.f48, { num: 2 })); //金额
                    }
                });
            }


            /***/ }),

        /***/ "moment":
        /*!*************************!*\
          !*** external "moment" ***!
          \*************************/
        /*! no static exports found */
        /***/ (function(module, exports) {

            module.exports = moment;

            /***/ })

        /******/ });
//# sourceMappingURL=stock.js.map