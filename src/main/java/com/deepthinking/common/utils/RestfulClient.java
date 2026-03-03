package com.deepthinking.common.utils;

import com.deepthinking.common.exception.NetWorkException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import static com.deepthinking.common.enums.ErrorCode.NETWORK_FAILED;


/**
 * Restful客户端
 */
public class RestfulClient {
    private static Logger logger = LoggerFactory.getLogger(RestfulClient.class);

    private static CloseableHttpClient client = null;

    //设置httpclient连接参数：请求超时时间 数据等待时间 buff大小
    private static PoolingHttpClientConnectionManager connManager = null;

    public final static int connectTimeout = 5000;


    //	@Value("${service.access_token}")
    //    String webToken;

    static {
        if (client == null) {
            init(new TrustManager[]{new DefaultTrustManager()});
        }
    }
/*

    public RestfulClient() {
        if (client == null) {
            init(new TrustManager[]{new DefaultTrustManager()});
        }
    }

    public RestfulClient(TrustManager trustManager) {
        if (client == null) {
            init(trustManager);
        }
    }
*/

    /**
     * RestfulClient初始化
     * TLS 1.0是IETF(工程任务组)制定的一种新的协议，它建立在SSL 3.0协议规范之上，是SSL 3.0的后续版本
     */
    private static boolean init(TrustManager... trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManager, new SecureRandom());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200)
                    .setMaxLineLength(2000)
                    .build();
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints)
                    .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(20);
            client = HttpClients.custom().setConnectionManager(connManager).build();
            return true;
        } catch (KeyManagementException e) {
            logger.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException", e);
        }
        return false;
    }


    /**
     * GET请求
     *
     * @param url     请求url
     * @param headers headers列表
     * @return
     */
    public static String get(String url, Map<String, String> headers) {
        return processRequest(new HttpGet(url), headers, "UTF-8");
    }

    /**
     * GET请求
     *
     * @param url     请求url
     * @param headers headers列表
     * @return
     */
    public static String get(String url, Map<String, String> headers, String encoding) {
        return processRequest(new HttpGet(url), headers, encoding);
    }

    /**
     * DELETE请求
     *
     * @param url     请求url
     * @param headers 头信息
     * @return
     */
    public static String delete(String url, Map<String, String> headers) {
        return processRequest(new HttpDelete(url), headers, null);
    }


    /**
     * POST请求
     *
     * @param url     请求url
     * @param headers 头信息
     * @param params  参数
     * @param body    请求体
     * @return
     */
    public static String post(String url, Map<String, String> headers, Map<String, String> params, String body) {
        return processRequest(new HttpPost(url), headers, params, body, null);
    }

    /**
     * POST请求，自动添加header: Content-Type="application/json;charset=UTF-8"
     *
     * @param url     请求url
     * @param headers 头信息
     * @param params  参数
     * @param body    请求体
     * @return
     */
    public static String postAsJson(String url, Map<String, String> headers, Map<String, String> params, String body) {
        headers = headers != null ? headers : Maps.newHashMap();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        return post(url, headers, params, body);
    }

    public static String postAsFile(String url, Map<String, String> headers, String paramName, String fileName, byte[] fileData, String body) {
        return processFile(new HttpPost(url), headers, paramName, fileName, fileData, body);
    }

    /**
     * PUT请求
     *
     * @param url     请求url
     * @param headers 头信息
     * @param params  参数
     * @param body    请求体
     * @return
     */
    public static String put(String url, Map<String, String> headers, Map<String, String> params, String body) {
        return processRequest(new HttpPut(url), headers, params, body, null);
    }

    /**
     * PUT请求，自动添加header: Content-Type="application/json;charset=UTF-8"
     *
     * @param url     请求url
     * @param headers 头信息
     * @param params  参数
     * @param body    请求体
     * @return
     */
    public static String putAsJson(String url, Map<String, String> headers, Map<String, String> params, String body) {
        headers = headers != null ? headers : Maps.newHashMap();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        return put(url, headers, params, body);
    }

    public static String putAsFile(String url, Map<String, String> headers, String paramName, String fileName, byte[] fileData, String body) {
        return processFile(new HttpPut(url), headers, paramName, fileName, fileData, body);
    }


    public static String processFile(HttpEntityEnclosingRequestBase request, Map<String, String> headers, String paramName, String fileName, byte[] fileData, String body) {
        HttpResponse clientResponse = null;
        try {
            if (headers != null && headers.size() > 0) {
                for (Entry<String, String> entry : headers.entrySet())
                    request.setHeader(entry.getKey(), entry.getValue());
            }
            MultipartEntityBuilder mb = MultipartEntityBuilder.create();
            mb.addPart(paramName, new ByteArrayBody(fileData, fileName));
            request.setEntity(mb.build());

            if (body != null && body.length() > 0) {
                request.setEntity(new StringEntity(body, "UTF-8"));
            }
            clientResponse = client.execute(request);
            if (clientResponse.getEntity() != null) {
                return EntityUtils.toString(clientResponse.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            throw new NetWorkException(NETWORK_FAILED, request.getURI() , body);
        } finally {
            if (request != null) {
                request.abort();
            }
        }
        return "";
    }

    private static String processRequest(HttpRequestBase request, Map<String, String> headers, String encoding) {

        HttpResponse clientResponse = null;
        try {
            if (headers != null && headers.size() > 0) {
                for (Entry<String, String> entry : headers.entrySet())
                    request.setHeader(entry.getKey(), entry.getValue());
            }
            clientResponse = client.execute(request);
            int code = clientResponse.getStatusLine().getStatusCode();
            if (code != 200) {
                logger.error("---->页面请求错误：StatusCode={}", code);
                return "";
            }
            if (clientResponse.getEntity() != null) {
                if (StringUtil.isNotEmpty(encoding))
                    return EntityUtils.toString(clientResponse.getEntity(), encoding);
                else
                    return EntityUtils.toString(clientResponse.getEntity());
            }
        } catch (Exception exp) {
            throw new NetWorkException(NETWORK_FAILED, request.getURI() , "");
        } finally {
            if (request != null) {
                request.abort();
            }
        }
        return "";
    }

    private static String processRequest(HttpEntityEnclosingRequestBase request, Map<String, String> headers, Map<String, String> params, String body, String encoding) {

        HttpResponse clientResponse = null;
        try {
            if (headers != null && headers.size() > 0) {
                for (Entry<String, String> entry : headers.entrySet())
                    request.setHeader(entry.getKey(), entry.getValue());
            }
            if (params != null && params.size() > 0) {
                ArrayList<BasicNameValuePair> nvps = Lists.newArrayList();
                for (Entry<String, String> entry : params.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }
            if (body != null && body.length() > 0) {
                request.setEntity(new StringEntity(body, "UTF-8"));
            }
            clientResponse = client.execute(request);
            int code = clientResponse.getStatusLine().getStatusCode();
            if (code != 200) {
                logger.error("---->页面请求错误：StatusCode={}", code);
                return "";
            }
            if (clientResponse.getEntity() != null) {
                if (StringUtil.isNotEmpty(encoding))
                    return EntityUtils.toString(clientResponse.getEntity(), encoding);
                else
                    return EntityUtils.toString(clientResponse.getEntity());
            }
        } catch (Exception exp) {
            throw new NetWorkException(NETWORK_FAILED, request.getURI() , body);
        } finally {
            if (request != null) {
                request.abort();
            }
        }
        return "";
    }


    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}
