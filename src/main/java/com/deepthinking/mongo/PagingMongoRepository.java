//package com.deepthinking.ext.base;
//
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import com.google.common.collect.Lists;
//import com.optimus.base.PageInfo;
//import com.optimus.base.PageResult;
//import com.optimus.mongo.BaseEntity;
//import com.optimus.utils.DateUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
//import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
//import org.springframework.util.ReflectionUtils;
//
//import java.beans.PropertyDescriptor;
//import java.io.Serializable;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.List;
//
//import static cn.hutool.core.text.StrPool.COMMA;
//import static com.optimus.constant.Constants.MAX_PAGE_SIZE;
//import static com.optimus.mongo.BaseEntity.CREATE_TIME;
//import static com.optimus.mongo.BaseEntity.UPDATE_TIME;
//
//@Slf4j
//public class PagingMongoRepository<T extends BaseEntity, ID extends Serializable> extends SimpleMongoRepository<T, ID> {
//
//    private final MongoOperations mongoOperations;
//
//    public PagingMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
//        super(metadata, mongoOperations);
//        this.mongoOperations = mongoOperations;
//    }
//
//
//    public List<T> queryList(T entity, Class<T> clazz) {
//        return queryList(entity, clazz, null);
//    }
//
//    public List<T> queryList(T entity, Class<T> clazz, Criteria criteria) {
//        Query query = wrapperQuery(entity, criteria);
//        return mongoOperations.find(query, clazz);
//    }
//
//    public PageResult<T> queryPage(PageInfo<T> pageInfo, Class<T> clazz) {
//        return queryPage(pageInfo, clazz, null);
//    }
//
//    public PageResult<T> queryPage(PageInfo<T> pageInfo, Class<T> clazz, Criteria criteria) {
//        Query query = wrapperQuery(pageInfo.getData(), criteria);
//        int pageIndex = pageInfo.getPageIndex() > 0 ? pageInfo.getPageIndex() : 1;
//        int pageSize = pageInfo.getPageSize();
//        long count = 0;
//        List<T> list = Lists.newArrayList();
//        if (pageSize > 0 && pageSize <= MAX_PAGE_SIZE) {
//            count = mongoOperations.count(query, clazz);
//            log.info("queryPage pageIndex:{} pageSize:{} count:{} in collection:{} ", pageIndex, pageSize, count, clazz.getSimpleName());
//            if (count > 0) {
//                query.with(PageRequest.of(pageIndex - 1, pageSize));
//                list = mongoOperations.find(query, clazz);
//            }
//        } else {
//            list = mongoOperations.find(query, clazz);
//            pageIndex = 1;
//            pageSize = list.size();
//            count = pageSize;
//        }
//        return PageResult.success(pageIndex, pageSize, count, list);
//    }
//
//
//    public Query wrapperQuery(T entity, Criteria criteria) {
//        Query query = new Query();
//        if (ObjectUtil.isEmpty(criteria)) {
//            criteria = new Criteria();
//        }
//        if (ObjectUtil.isNotEmpty(entity)) {
//            Class<?> cls = entity.getClass();
//            Field[] fields = cls.getDeclaredFields();
//            for (Field field : fields) {
//                try {
//                    String fieldName = field.getName();
//                    if (fieldName.equals("serialVersionUID") || fieldName.equals(CREATE_TIME) || fieldName.equals(UPDATE_TIME) || fieldName.endsWith("Start") || fieldName.endsWith("End")) {
//                        continue;
//                    }
//                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, cls);
//                    Method getMethod = pd.getReadMethod();
//                    Object value = ReflectionUtils.invokeMethod(getMethod, entity);
//                    if (value == null) {
//                        continue;
//                    }
//                    if (value instanceof String) {
//                        if (StrUtil.isBlank(String.valueOf(value))) {
//                            continue;
//                        }
//                    }
//                    fieldName = "id".equals(fieldName) ? "_id" : fieldName;
//                    criteria.and(fieldName).is(value);
//                } catch (Exception e) {
//                    log.error("无法解析字符串方法名 {} ", field.getName());
//                }
//            }
//            if (StrUtil.isAllNotBlank(entity.getCreateTimeStart(), entity.getCreateTimeEnd())) {
//                criteria.and(CREATE_TIME).gte(DateUtils.parseDate(entity.getCreateTimeStart())).lte(DateUtils.parseDate(entity.getCreateTimeEnd()));
//            }
//            if (StrUtil.isAllNotBlank(entity.getUpdateTimeStart(), entity.getUpdateTimeEnd())) {
//                criteria.and(UPDATE_TIME).gte(DateUtils.parseDate(entity.getUpdateTimeStart())).lte(DateUtils.parseDate(entity.getUpdateTimeEnd()));
//            }
//            if (StrUtil.isNotBlank(entity.getOrderColumn())) {
//                String[] cols = entity.getOrderColumn().split(COMMA);
//                String[] asc = StrUtil.isNotBlank(entity.getIsAsc()) ? entity.getIsAsc().split(COMMA) : new String[0];
//                List<Sort.Order> orders = Lists.newArrayList();
//                for (int i = 0; i < cols.length; i++) {
//                    orders.add("1".equals(i < asc.length ? asc[i] : "1") ? Sort.Order.asc(cols[i]) : Sort.Order.desc(cols[i]));
//                }
//                log.info("wrapperQuery collection:{} orderColumn={} isAsc={}", entity.getClass().getSimpleName(), cols, asc);
//                query.with(Sort.by(orders));
//            }
//        }
//        query.addCriteria(criteria);
//        log.info("wrapperQuery collection:{} {}", entity.getClass().getSimpleName(), query.getQueryObject().toJson());
//        return query;
//    }
//}
