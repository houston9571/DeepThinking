package com.deepthinking.core.base;//package com.deepthinking.core.base;
//
//import com.alibaba.fastjson2.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//import java.util.Map.Entry;
//
//@Slf4j
//@Service
//public class RestTemplateComponent {
//
//
//    private static final String TELEGRAM_API_URL_TEMPLATE = "https://api.telegram.org/bot%s/sendmessage";
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    public String post(String url, Map<String, Object> header, Map<String, Object> body) {
//        String response = null;
//        try {
//            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//            for (Entry<String, Object> pm : body.entrySet()) {
//                map.add(pm.getKey(), pm.getValue());
//            }
//            HttpHeaders headers = new HttpHeaders();
//            for (Entry<String, Object> pm : header.entrySet()) {
//                headers.add(pm.getKey(), pm.getValue().toString());
//            }
//            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
//            ResponseEntity<String> entity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
//            response = entity.getBody();
//        } catch (HttpClientErrorException e) {
//            log.error("RestTemplateComponent httpClient error: {} ", e.getResponseBodyAsString(), e);
//        } catch (Exception e) {
//            log.error("RestTemplateComponent Exception: {} ", e.getLocalizedMessage(), e);
//        }
//        log.info("URL      : POST {}", url);
//        log.info("Header   : {}", JSONObject.toJSONString(header));
//        log.info("Request  : {}", JSONObject.toJSONString(body));
//        log.info("Response : {}", response);
//        return response;
//    }
//
//    public <T> T postTelegram(Class<T> responseType, String authToken, String chatId, String text) {
//        T response = null;
//        String url = String.format(TELEGRAM_API_URL_TEMPLATE, authToken);
//        try {
//            String json = JSONObject.of("chat_id", chatId, "text", text).toJSONString();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
//            response = restTemplate.postForObject(url, httpEntity, responseType);
//        } catch (Exception e) {
//            log.error("postTelegram Exception: {} ", e.getLocalizedMessage(), e);
//        }
//        log.info("URL      : POST {} chat_id:{}", url, chatId);
//        log.info("Response : {}", response);
//        return response;
//    }
//
//    /**
//     * TOKEN = '7825231131:AAHZLSwqKHgReCPSuciZY862RwrtmpolapI'   CHAT_ID = -1002125193395
//     */
//    public String postTelegram(String authToken, String chatId, String text) {
//        log.info("postTelegram: {}", text);
////        return postTelegram(String.class, authToken, chatId, text);
//        return "";
//    }
//
//
//}
