//package com.optimus.mongo.entity;
//
//
//import com.optimus.mongo.BaseEntity;
//import lombok.*;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
///**
// * 系统日志
// **/
//@EqualsAndHashCode(callSuper = true)
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Document(collection = "sys_log")
//public class SysLog extends BaseEntity {
//
//    @Id
//    private Long id;
//
//    private String agentId;
//
//    private String type;
//
//    private String method;
//
//    private String uri;
//
//    private String params;
//
//    private String result;
//
//    private Integer millis;
//
//    private String ip;
//
//
//}
