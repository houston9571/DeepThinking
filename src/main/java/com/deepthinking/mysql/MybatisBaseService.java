package com.deepthinking.mysql;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deepthinking.common.exception.ServiceException;
import com.deepthinking.core.base.PageInfo;
import com.deepthinking.mysql.entity.BaseEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.deepthinking.common.enums.ErrorCode.INTERFACE_UNSUPPORTED;

public interface MybatisBaseService<P extends BaseEntity> {

    int save(P entity);

    int saveBatch(List<P> list);

    int saveBatch(List<P> list, int batchSize);

    int saveOrUpdate(P p, String[] columns);

    int saveOrUpdateBatch(List<P> list, String[] columns);

    int saveOrUpdateBatch(List<P> list, String[] columns, int batchSize);


    int updateById(P entity);

    int update(P entity, Wrapper<P> queryWrapper);


    int deleteById(Serializable id);

    int delete(Wrapper<P> queryWrapper);

    int deleteBatch(Collection<? extends Serializable> ids);


    Long count(P entity);

    boolean exist(Wrapper<P> queryWrapper);


    P findById(Serializable id);

    default P findByIdCache(Serializable id) {
        throw new ServiceException(INTERFACE_UNSUPPORTED, "findByIdCache");
    }

    P findOne(P entity);

    P findOne(P entity, QueryWrapper<P> query);

    List<P> findAll();


    List<P> queryList(P entity);

    List<P> queryList(P entity, QueryWrapper<P> queryWrapper);

    List<P> queryPage(PageInfo<P> pageInfo);

    List<P> queryPage(PageInfo<P> pageInfo, QueryWrapper<P> queryWrapper);
}
