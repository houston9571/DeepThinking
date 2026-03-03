//package com.optimus.mongo.repository;
//
//import com.optimus.base.PageInfo;
//import com.optimus.base.PageResult;
//import com.optimus.mongo.entity.SysLog;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//import java.time.LocalDateTime;
//
//public interface SysLogRepository extends MongoRepository<SysLog, Long> {
//
//    PageResult<SysLog> queryPage(PageInfo<SysLog> pageInfo, Class<SysLog> clazz, Criteria criteria);
//
//
//    int deleteSysLogsByCreateTimeBefore(LocalDateTime createTime);
//
//
//}
